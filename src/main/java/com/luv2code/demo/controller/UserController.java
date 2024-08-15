package com.luv2code.demo.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.UpdateUserImageRequest;
import com.luv2code.demo.dto.request.UpdateUserProfileRequest;
import com.luv2code.demo.dto.response.UpdateUserProfileResponse;
import com.luv2code.demo.service.IUserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/users")
@AllArgsConstructor
public class UserController {

	private final IUserService userService;
	
	@PutMapping("/image")
	public ResponseEntity<Map<String,String>> updateUserImage(@Valid @ModelAttribute UpdateUserImageRequest upadImageRequest) throws IOException{
		
		return userService.updateUserImage(upadImageRequest);
		
	}
	
	@PutMapping("")
	public UpdateUserProfileResponse updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		 return userService.updateUserProfile(updateUserProfileRequest);
		 
	}
	
}
