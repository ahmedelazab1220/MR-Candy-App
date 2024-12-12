package com.luv2code.demo.service;

import java.io.IOException;

import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendOtpEmail(String to, String otp) throws MessagingException, IOException;

}
