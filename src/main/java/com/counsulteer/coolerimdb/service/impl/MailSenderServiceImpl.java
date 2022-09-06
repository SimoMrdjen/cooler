package com.counsulteer.coolerimdb.service.impl;

import com.counsulteer.coolerimdb.service.MailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.Properties;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Value("${mail.smtp.host}")
    private String mailHost;

    @Value("${mail.smtp.ssl.trust}")
    private String sslTrust;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.sender}")
    private String sender;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    private static final Logger log = LoggerFactory.getLogger(MailSenderServiceImpl.class);

    @Override
    public void sendEmail(String email, String subject, String content) {
        Session session = createSession();

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            log.info("Mail sent to: "+Arrays.toString(message.getRecipients(Message.RecipientType.TO)));

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException ae) {
            log.error(ae.toString());

        }

    }

    private Session createSession() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", mailHost);
        prop.put("mail.smtp.port", smtpPort);
        prop.put("mail.smtp.ssl.trust", "email.consulteer.tk");
        prop.put("smtp.starttls.required", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
}
