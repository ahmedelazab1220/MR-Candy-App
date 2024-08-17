package com.luv2code.demo.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.luv2code.demo.dto.NotificationMessage;
import com.luv2code.demo.exc.StatusCode;
import com.luv2code.demo.service.IFirebaseMessagingService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class FirebaseMessagingService implements IFirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public ResponseEntity<String> sendNotificationByToken(NotificationMessage notificationMessage) {

        Notification notification = Notification.builder().setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage()).build();

        Message message = Message.builder()
                .setToken(notificationMessage.getRecipientToken())
                .setNotification(notification)
                .putAllData(notificationMessage.getData()).build();

        try {
            firebaseMessaging.send(message);
            log.info("Success Sending Notification.");
            return ResponseEntity.status(StatusCode.SUCCESS)
                    .body("Notification sent successfully.");
        } catch (FirebaseMessagingException e) {
            log.error("Error Sending Notification.");
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR)
                    .body("Error sending notification: " + e.getMessage());
        }

    }

}
