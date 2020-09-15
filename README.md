# Mail attachment collector

Backup mail attachments from online email account.

## Usage

```bash
$ java -jar build/libs/java -jar target/myapplication-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=outlook
```

## FAQ

Q: Why do I get AuthenticationFailedException when accessing Gmail? [new!]
A: Most likely you failed to [enable less secure apps](https://www.google.com/settings/security/lesssecureapps).

## Settings 

| Provider        | Imap           |
| --------------- |:-------------:|
| Gmail           | imap.gmail.com |
| Gmx             | imap.gmx.net |
| Outlook         | outlook.office365.com |

## Folders

### Gmail

* INBOX
* Notes
* [Gmail]/Alle Nachrichten
* [Gmail]/Entwürfe
* [Gmail]/Gesendet
* [Gmail]/Markiert
* [Gmail]/Papierkorb
* [Gmail]/Spam
* [Gmail]/Wichtig

###  GMX

* Ablage
* Entwürfe
* Gelöscht
* Gesendet
* INBOX
* OUTBOX
* PV BUNd
* Spamverdacht


### Outlook

Archiv
Drafts
Deleted
Sent
Junk
Notes
Outbox
Inbox
Scheduled

## Links

- [Mail FAQ](https://javaee.github.io/javamail/FAQ#gmail): very important
- [MimeTypes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types)