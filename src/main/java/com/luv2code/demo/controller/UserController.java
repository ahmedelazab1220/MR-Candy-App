package com.luv2code.demo.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserImageRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserProfileRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.UpdateUserProfileResponseDTO;
import com.luv2code.demo.service.IUserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/users")
@AllArgsConstructor
public class UserController {

    private final IUserService userService;

    @PutMapping("/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> updateUserImage(@Valid @ModelAttribute UpdateUserImageRequestDTO upadImageRequest) throws IOException {

        return userService.updateUserImage(upadImageRequest);

    }

    @PutMapping("")
    @PreAuthorize("hasRole('USER')")
    public UpdateUserProfileResponseDTO updateUserProfile(@Valid @RequestBody UpdateUserProfileRequestDTO updateUserProfileRequest) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        return userService.updateUserProfile(updateUserProfileRequest);

    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO> deleteUserAccount(@RequestParam String email) {

        return userService.deleteUser(email);

    }

    @PutMapping("/pass")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO> updateUserPassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {

        if (changePasswordRequestDTO.getOldPassword() == null) {
            throw new IllegalArgumentException("Old password must not be null");
        }

        return userService.UpdatePassword(changePasswordRequestDTO);

    }

}
