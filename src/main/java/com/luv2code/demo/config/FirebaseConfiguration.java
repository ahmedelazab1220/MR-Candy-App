package com.luv2code.demo.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfiguration {

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
	   GoogleCredentials googelCredentials = GoogleCredentials.fromStream(
		 	   new ClassPathResource("firebase-service-account.json").getInputStream());	
	   
	   FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googelCredentials)
			   .build();
	   
	   FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "mr-candy-app");
	  
	   return FirebaseMessaging.getInstance(app);
	}
	
}
