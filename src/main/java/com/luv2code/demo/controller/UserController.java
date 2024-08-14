package com.luv2code.demo.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.UpdateUserImageRequest;
import com.luv2code.demo.service.IUserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/users")
@AllArgsConstructor
public class UserController {

	private final IUserService userService;
	
	@PostMapping("/image")
	public ResponseEntity<Map<String,String>> updateUserImage(@Valid @ModelAttribute UpdateUserImageRequest upadImageRequest) throws IOException{
		
		return userService.updateUserImage(upadImageRequest);
		
	}
	
	@PatchMapping("")
	public ResponseEntity<Map<String, String>> updateUserProfile(@RequestParam(required = true) String email , @RequestBody Map<String,String> userUpdates) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		 return userService.updateUserProfile(email, userUpdates);
		 
	}
	
}
