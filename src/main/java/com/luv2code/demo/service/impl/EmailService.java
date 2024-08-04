package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.luv2code.demo.service.IEmailBulider;
import com.luv2code.demo.service.IEmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final IEmailBulider emailBulider;

    @Override
    @Async
    public void sendOtpEmail(String to, String otp) throws MessagingException, IOException {
        String subject = "Your Verification Code";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        String htmlBody = emailBulider.buildEmailBody(otp);

        helper.setTo(to);
        helper.setFrom("ahmedelazab1210@gmail.com");
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        try {
            javaMailSender.send(message);
        } catch (RuntimeException e) {
            log.error("Mail server connection failed!");
            throw new RuntimeException("Mail server connection failed!", e);
        }

        log.info("OTP email sent successfully to: {}", to);
    }

}
