package com.counsulteer.coolerimdb.unittest;


import com.counsulteer.coolerimdb.service.MailSenderService;
import com.counsulteer.coolerimdb.service.impl.MailSenderServiceImpl;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = MailSenderServiceImpl.class)
@TestPropertySource(properties = {"EMAIL_SENDER=mail@mail.com", "EMAIL_USERNAME=mail@mail.com", "EMAIL_PASSWORD=123abc"})
class MailSenderServiceImplUnitTest {
    @Value("${mail.username}")
    private String login;
    @Value("${mail.password}")
    private String password;
    private GreenMail greenMail;
    @Autowired
    private MailSenderService mailSenderService;

    @BeforeEach
    public void beforeEach() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.withConfiguration(GreenMailConfiguration.aConfig().withUser(login, password));
        greenMail.start();
    }

    @AfterEach
    public void afterEach() {
        greenMail.stop();
    }

    @Test
    public void shouldSendMailWhenSendMailCalled() throws MessagingException {
        assertThat(mailSenderService).isNotNull();
        String recipientEmail = "nikola.protic@consulteer.com";
        String mailSubject = "mail subject";
        String mailContent = "mail content";

        mailSenderService.sendEmail(recipientEmail, mailSubject, mailContent);
        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(recipientEmail);
        assertThat(message.getSubject()).isEqualTo(mailSubject);
        assertThat(GreenMailUtil.getBody(message)).contains(mailContent);

        greenMail.stop();
    }

}