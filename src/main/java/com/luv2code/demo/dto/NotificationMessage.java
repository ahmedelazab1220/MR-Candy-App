package com.luv2code.demo.dto;

import java.util.Map;

import lombok.Data;

@Data
public class NotificationMessage {

    private String recipientToken;
    private String title;
    private String image;
    private String body;
    private Map<String, String> data;

}
