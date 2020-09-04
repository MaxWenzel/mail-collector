package org.devzone.mailcollector.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.util.Properties;

public class GmailClient {

    private static final Logger logger = LoggerFactory.getLogger(EmailFetchService.class);

    private Store store;

    public static void main(String[] args) throws MessagingException {
        GmailClient client = new GmailClient();

    }

    public GmailClient() throws MessagingException {

        Properties properties = System.getProperties();
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(properties, null);

        try {
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", "pvbund@gmail.com", "PVBund1895");
        } catch (MessagingException e) {
        }

        javax.mail.Folder[] folders = store.getDefaultFolder().list("*");

        for (javax.mail.Folder folder : folders) {
            if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
                logger.info("FolderName: {} ", folder.getName());
            }
        }
    }

//    public List<MailWrapper> getLastHoursMails(int hours) throws GmailClientException {
//        return getLastHoursMails(hours, "[Gmail]/Todos");
//    }
//
//    public List<MailWrapper> getLastHoursMails(int hours, String inboxName) throws GmailClientException {
//        Folder inbox = null;
//
//        try {
//            inbox = store.getFolder(inboxName);
//            inbox.open(Folder.READ_ONLY);
//
//            Message[] messages = inbox.search(lastHoursSearchTerm(hours));
//
//            return Arrays.stream(messages)
//                    .map(MailWrapper::instance)
//                    .collect(Collectors.toList());
//        } catch (MessagingException e) {
//            throw new GmailClientException(e);
//        } finally {
//            closeFolder(inbox);
//        }
//    }
//
//    public List<MailWrapper> getMailsSince(DateTime fromDate, String inboxName) throws GmailClientException {
//        Folder inbox = null;
//
//        try {
//            inbox = store.getFolder(inboxName);
//            inbox.open(Folder.READ_ONLY);
//
//            Message[] messages = inbox.search(sinceSearchTerm(fromDate));
//
//            return Arrays.stream(messages)
//                    .map(MailWrapper::instance)
//                    .collect(Collectors.toList());
//        } catch (MessagingException e) {
//            throw new GmailClientException(e);
//        } finally {
//            closeFolder(inbox);
//        }
//    }
//
//    private SearchTerm sinceSearchTerm(DateTime fromDate) {
//        DateTime rightNow = new DateTime();
//
//        SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LE, rightNow.toDate());
//        SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GE, fromDate.toDate());
//
//        return new AndTerm(newerThan, olderThan);
//    }
//
//    private SearchTerm lastHoursSearchTerm(int hours) {
//        DateTime rightNow = new DateTime();
//        DateTime past = rightNow.minusHours(hours);
//
//        SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LE, rightNow.toDate());
//        SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GE, past.toDate());
//
//        return new AndTerm(newerThan, olderThan);
//    }
//
//    private void closeFolder(Folder inbox) {
//        try {
//            if (inbox != null)
//                inbox.close(false);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }

}