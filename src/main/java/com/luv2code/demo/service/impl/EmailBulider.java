package com.luv2code.demo.service.impl;

import org.springframework.stereotype.Component;

import com.luv2code.demo.service.IEmailBulider;

@Component
public class EmailBulider implements IEmailBulider {

	@Override
	public String buildEmailBody(String otp) {

		return "<!DOCTYPE html>\r\n" + "<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">\r\n" + "<head>\r\n"
				+ "    <meta charset=\"UTF-8\">\r\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
				+ "    <title>Email Template</title>\r\n" + "    <style>\r\n" + "        body, html {\r\n"
				+ "            margin: 0;\r\n" + "            padding: 0;\r\n"
				+ "            font-family: Arial, sans-serif;\r\n" + "            background-color: #f4f4f4;\r\n"
				+ "        }\r\n" + "        .container {\r\n" + "            max-width: 600px;\r\n"
				+ "            margin: 0 auto;\r\n" + "            background-color: #ffffff;\r\n"
				+ "            padding: 20px;\r\n" + "            border-radius: 10px;\r\n"
				+ "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\r\n"
				+ "            animation: fadeInScale 0.5s ease-out;\r\n" + "        }\r\n" + "        .header {\r\n"
				+ "            text-align: center;\r\n" + "            padding: 10px 0;\r\n" + "        }\r\n"
				+ "        .header h1 {\r\n" + "            margin: 0;\r\n" + "            font-size: 24px;\r\n"
				+ "            color: #333333;\r\n" + "        }\r\n" + "        .content {\r\n"
				+ "            padding: 20px 0;\r\n" + "            text-align: center;\r\n" + "        }\r\n"
				+ "        .content p {\r\n" + "            font-size: 18px;\r\n" + "            color: #666666;\r\n"
				+ "        }\r\n" + "        .content .otp {\r\n" + "            font-size: 24px;\r\n"
				+ "            font-weight: bold;\r\n" + "            color: #333333;\r\n"
				+ "            background-color: #f0f0f0;\r\n" + "            padding: 10px;\r\n"
				+ "            border-radius: 5px;\r\n" + "            display: inline-block;\r\n"
				+ "            margin: 10px 0;\r\n" + "        }\r\n" + "        .footer {\r\n"
				+ "            text-align: center;\r\n" + "            padding: 10px 0;\r\n"
				+ "            font-size: 14px;\r\n" + "            color: #999999;\r\n" + "        }\r\n"
				+ "        @keyframes fadeInScale {\r\n" + "            from {\r\n" + "                opacity: 0;\r\n"
				+ "                transform: scale(0.8);\r\n" + "            }\r\n" + "            to {\r\n"
				+ "                opacity: 1;\r\n" + "                transform: scale(1);\r\n" + "            }\r\n"
				+ "        }\r\n" + "    </style>\r\n" + "</head>\r\n" + "<body>\r\n"
				+ "    <div class=\"container\">\r\n" + "        <div class=\"header\">\r\n"
				+ "            <h1>Verification Code</h1>\r\n" + "        </div>\r\n"
				+ "        <div class=\"content fadeInScale\">\r\n" + "            <p>Dear Friend,</p>\r\n"
				+ "            <p>Your One-Time Password (OTP) is:</p>\r\n" + "            <p class=\"otp\"> " + otp
				+ " </p>\r\n"
				+ "            <p>Please use this code to complete your verification. The code is valid for 5 minutes.</p>\r\n"
				+ "        </div>\r\n" + "        <div class=\"footer\">\r\n"
				+ "            <p>If you did not request this code, please ignore this email.</p>\r\n"
				+ "        </div>\r\n" + "    </div>\r\n" + "</body>\r\n" + "</html>";
	}
}
