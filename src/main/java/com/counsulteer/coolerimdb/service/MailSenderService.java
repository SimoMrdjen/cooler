package com.counsulteer.coolerimdb.service;

public interface MailSenderService {
    void sendEmail(String email, String subject, String content);
}
