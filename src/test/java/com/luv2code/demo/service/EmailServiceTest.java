package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import com.luv2code.demo.service.impl.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private IEmailBulider emailBulider;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        mimeMessage = new MimeMessage(session);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendOtpEmailSuccess() throws MessagingException, IOException {
    	
        String to = "test@example.com";
        String otp = "123456";
        String htmlBody = "<html>Your OTP is: " + otp + "</html>";

        when(emailBulider.buildEmailBody(otp)).thenReturn(htmlBody);

        doNothing().when(javaMailSender).send(mimeMessage);

        emailService.sendOtpEmail(to, otp);

        verify(emailBulider, times(1)).buildEmailBody(otp);
        verify(javaMailSender, times(1)).send(mimeMessage);
        
    }

    @Test
    void testSendOtpEmailFailure() throws IOException, MessagingException {
    	
        String to = "test@example.com";
        String otp = "123456";
        String htmlBody = "<html>Your OTP is: " + otp + "</html>";

        when(emailBulider.buildEmailBody(otp)).thenReturn(htmlBody);

        doThrow(new RuntimeException("Mail server connection failed!"))
            .when(javaMailSender).send(any(MimeMessage.class));

        try {
            emailService.sendOtpEmail(to, otp);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Mail server connection failed!", e.getMessage());
        }

        verify(emailBulider, times(1)).buildEmailBody(otp);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        
    }
    
}
