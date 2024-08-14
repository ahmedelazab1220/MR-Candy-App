package com.luv2code.demo.service;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.NotificationMessage;

public interface IFirebaseMessagingService {

	ResponseEntity<String> sendNotificationByToken(NotificationMessage notificationMessage);
	
}
