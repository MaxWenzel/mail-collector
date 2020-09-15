package org.devzone.mailcollector.service;

import org.devzone.mailcollector.config.MailConfiguration;
import org.devzone.mailcollector.model.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EmailFetchService {

    private static final Logger logger = LoggerFactory.getLogger(EmailFetchService.class);

    public static final String DATE_FILE_PATTERN = "yyyy_MM_dd_";

    private final Environment environment;
    private final MailConfiguration mailConfiguration;

    public EmailFetchService(Environment environment, MailConfiguration mailConfiguration) {
        this.environment = environment;
        this.mailConfiguration = mailConfiguration;
    }

    private static final List<String> UNKNOWN_MIME_TYPES = new ArrayList<>();

    public void fetchMails() throws Exception {

        Path backupDir = createAttachmentBackup(mailConfiguration.getMailFolderName(), mailConfiguration.getLocalFolder());

        Properties properties = System.getProperties();
        properties.setProperty("mail.store.protocol", mailConfiguration.getProtocol());
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.ssl.enable", Boolean.toString(mailConfiguration.isSslEnabled()));

        Session session = Session.getInstance(properties);

        Store store = session.getStore(mailConfiguration.getProtocol());
        store.connect(mailConfiguration.getHost(), mailConfiguration.getUser(), mailConfiguration.getPassword());

//        printAllFolders(store);
//        if (true) return;

        Folder inbox = store.getFolder(mailConfiguration.getMailFolderName());
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();

        logger.info("Start processing messages");
        for (Message message : messages) {
            //logger.info("sendDate: {}, subject: {}", message.getSentDate(), message.getSubject());
            if (message.isMimeType("text/plain")) {
                continue;
            }
            saveAllAttachments(backupDir, message, MimeType.getAllMimeTypes().toArray(new String[]{}));
        }
    }

    private Path createAttachmentBackup(String mailFolder, String localDir) throws IOException {
        mailFolder = normalizePath(mailFolder);
        Path directoryPath = createBackupDirectoryPath(mailFolder, localDir);
        Files.createDirectory(directoryPath);
        return directoryPath;
    }

    private String normalizePath(String pathSegment) {
        pathSegment = pathSegment.replaceAll("\\[|\\]", "").replaceAll("/|\\s+|:|'|\\+", "_").replaceAll("_+", "_");
        int lastIndexOf = pathSegment.lastIndexOf(".");
        if (lastIndexOf != -1) {
            pathSegment = pathSegment.substring(0, lastIndexOf).replaceAll("\\.", "_") + pathSegment.substring(lastIndexOf);
        }

        return pathSegment;
    }

    private Path createBackupDirectoryPath(String folderName, String parent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FILE_PATTERN);
        String date = simpleDateFormat.format(new Date());
        folderName = environment.getActiveProfiles()[0].toUpperCase(Locale.GERMAN) + "_" + folderName;
        Path path = Paths.get(parent, date + folderName);
        return path;
    }

    public void saveAllAttachments(Path backupDir, Message message, String... mimeType)
            throws MessagingException, IOException {
        int counter = 1;
        if (message.isMimeType("multipart/*")) {
            logger.debug("Process multipart/* message");
            Multipart mp = (Multipart) message.getContent();

            // Laufe über alle Teile (Anhänge)
            for (int j = 1; j < mp.getCount(); j++) {
                Part part = mp.getBodyPart(j);
                String disp = part.getDisposition();
                if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
                    MimeBodyPart mimePart = (MimeBodyPart) part;
                    // Gib MIME-Typ jedes Anhangs aus; im Fall von XML die Nachricht
                    if (!isValidMimeType(mimePart, List.of(mimeType))) {
                        if (!UNKNOWN_MIME_TYPES.contains(mimePart.getContentType())) {
                            logger.warn("Unknown MimeType detected: {}", mimePart.getContentType());
                            UNKNOWN_MIME_TYPES.add(mimePart.getContentType());
                        }
                        continue;
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FILE_PATTERN);
                    Date sentDay = message.getSentDate() != null ? message.getSentDate() : new Date();
                    String date = simpleDateFormat.format(sentDay);
                    Path file = null;
                    boolean isValidFileName = false;
                    while (true) {
                        if (!StringUtils.isEmpty(part.getFileName())) {
                            String fileName = normalizePath(part.getFileName());
                            try {
                                file = Paths.get(backupDir.toString(), date + String.format("%03d", counter++) + "_" + fileName);
                            } catch (Exception e) {
                                logger.error("Cannot create filename with suffix {}", fileName);
                                break;
                            }
                            if (!Files.exists(file)) {
                                isValidFileName = true;
                                break;
                            }
                        } else {
                            logger.error("Detected invalid filename <{}> with Sentdate {}", part.getFileName(), message.getSentDate());
                            break;
                        }
                    }
                    if (isValidFileName) {
                        mimePart.saveFile(file.toString());
                    }
                }
            }
        }
    }

    private boolean isValidMimeType(MimeBodyPart mimePart, List<String> mimeTypes) throws MessagingException {
        for (String mimeType : mimeTypes) {
            if (mimePart.isMimeType(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private List<Folder> printAllFolders(Store store) throws MessagingException {
        javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
        List<Folder> result = new ArrayList<>();
        for (javax.mail.Folder folder : folders) {
            if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
                logger.info("Found Folder with Name: <{}> with {} messages", folder.getFullName(), folder.getMessageCount());
                result.add(folder);
            }
        }

        return result;
    }
}