package com.luv2code.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.NotificationMessage;
import com.luv2code.demo.service.IFirebaseMessagingService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/notification")
@AllArgsConstructor
public class NotificationController {

    private final IFirebaseMessagingService firebaseMessagingService;

    @PostMapping("")
    public ResponseEntity<String> sendNotificationByToken(@RequestBody NotificationMessage notificationMessage) {

        return firebaseMessagingService.sendNotificationByToken(notificationMessage);

    }

}
