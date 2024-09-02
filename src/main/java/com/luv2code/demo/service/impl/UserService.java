package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserImageRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserProfileRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.UpdateUserProfileResponseDTO;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.StatusCode;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.UserRepository;
import com.luv2code.demo.service.IUserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final IFileHelper fileHelper;
    private final PasswordEncoder passwordEncoder;
    private final SystemMapper mapper;

    @Override
    public User getUserTokenDetails(String email) {
        log.info("Entering getUserTokenDetails method with email: {}", email);

        if (email.isEmpty()) {
            log.error("Email is empty");
            throw new IllegalArgumentException("Email must not be empty");
        }

        Optional<User> user = userRepository.findUserTokenDetailsByEmail(email).map(mapper::userTokenResponseDTOTOUser);

        if (!user.isPresent()) {
            log.error("User not found with email: {}", email);
            throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
        }

        log.info("Successfully fetched user token details for email: {}", email);
        return user.get();
    }

    @Override
    public void createUser(User user) {
        log.info("Entering createUser method with email: {}", user.getEmail());

        if (user.getEmail() == null || user.getPassword() == null || user.getImageUrl() == null
                || user.getPhoneNumber() == null || user.getRole() == null || user.getAddress() == null) {
            log.error("Required fields are missing for user creation");
            throw new IllegalArgumentException("Required fields are missing!");
        }

        Boolean userIsExist = userRepository.existsByEmail(user.getEmail());

        if (userIsExist) {
            log.error("Email is already in use: {}", user.getEmail());
            throw new IllegalArgumentException("Email is already in use!");
        }

        log.info("User successfully created with email: {}", user.getEmail());
        userRepository.save(user);
    }

    @Override
    public User getUserSetterByEmail(String email) {
        log.info("Entering getUserSetterByEmail method with email: {}", email);

        Optional<User> user = userRepository.findUserSetterByEmail(email).map(mapper::userSetterDTOTOUser);

        if (user.isEmpty()) {
            log.error("User not found with email: {}", email);
            throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
        }

        log.info("Successfully fetched user setter by email: {}", email);
        return user.get();
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponseDTO> UpdatePassword(ChangePasswordRequestDTO changePasswordRequest) {
        log.info("Entering UpdatePassword method for email: {}", changePasswordRequest.getEmail());

        if (changePasswordRequest.getOldPassword() != null) {
            Optional<String> pass = userRepository.findUserPasswordByEmail(changePasswordRequest.getEmail());

            if (pass.isEmpty()) {
                log.error("User not found with email: {}", changePasswordRequest.getEmail());
                throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
            }

            if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), pass.get())) {
                log.error("Old password is incorrect for email: {}", changePasswordRequest.getEmail());
                throw new IllegalArgumentException("Old password is incorrect!");
            }
        }

        if (!Objects.equals(changePasswordRequest.getNewPassword(), changePasswordRequest.getNewRepeatedPassword())) {
            log.warn("New password and confirmation password do not match for email: {}",
                    changePasswordRequest.getEmail());
            return ResponseEntity.ok(
                    new ApiResponseDTO("Password not equal confirmation password,Please enter the password again!"));
        }

        Integer updateRows = userRepository.updatePasswordByEmail(changePasswordRequest.getEmail(),
                passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        if (updateRows == 0) {
            log.error("Password update failed for email: {}", changePasswordRequest.getEmail());
            throw new NotFoundException("password not change ,please try later!");
        }

        log.info("Password successfully changed for email: {}", changePasswordRequest.getEmail());
        return ResponseEntity.ok(new ApiResponseDTO("Password has been changed!"));
    }

    @Override
    public ResponseEntity<ApiResponseDTO> deleteUser(String email) {
        log.info("Entering deleteUser method with email: {}", email);

        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {
            log.error("User not found with email: {}", email);
            throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
        }

        userRepository.delete(user.get());
        log.info("User successfully deleted with email: {}", email);

        return ResponseEntity.ok(new ApiResponseDTO("Success Deleted User!"));
    }

    @Transactional
    @Override
    public ResponseEntity<Map<String, String>> updateUserImage(UpdateUserImageRequestDTO updateUserImageRequest)
            throws IOException {
        log.info("Entering updateUserImage method with email: {}", updateUserImageRequest.getEmail());

        String newImageUrl = null;

        if (updateUserImageRequest.getImage() != null) {
            log.info("Deleting old image from File System for user: {}", updateUserImageRequest.getEmail());
            fileHelper.deleteImageFromFileSystem(updateUserImageRequest.getOldImageUrl());

            newImageUrl = fileHelper.uploadFileToFileSystem(updateUserImageRequest.getImage());
        }

        if (newImageUrl != null) {
            log.info("Updating image URL for user: {}", updateUserImageRequest.getEmail());
            userRepository.updateImageByEmail(updateUserImageRequest.getEmail(), newImageUrl);
        }

        log.info("Update User Image successfully with email: {}", updateUserImageRequest.getEmail());
        return ResponseEntity.status(StatusCode.SUCCESS).body(Map.of("imageUrl", newImageUrl));
    }

    @Transactional
    @Override
    public UpdateUserProfileResponseDTO updateUserProfile(UpdateUserProfileRequestDTO updateUserProfileRequest)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        log.info("Entering updateUserProfile method with email: {}", updateUserProfileRequest.getEmail());

        Optional<User> user = userRepository.findByEmail(updateUserProfileRequest.getEmail());

        if (user.isEmpty()) {
            log.error("User not found with email: {}", updateUserProfileRequest.getEmail());
            throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
        }

        log.info("Updating user profile for email: {}", updateUserProfileRequest.getEmail());
        mapper.updateUserProfileRequestTOUser(updateUserProfileRequest, user.get());

        user.get().getAddress().setCity(updateUserProfileRequest.getCity());
        user.get().getAddress().setState(updateUserProfileRequest.getState());
        user.get().getAddress().setStreet(updateUserProfileRequest.getStreet());
        user.get().getAddress().setZipCode(updateUserProfileRequest.getZipCode());

        userRepository.save(user.get());
        log.info("User profile successfully updated for email: {}", updateUserProfileRequest.getEmail());

        return mapper.updateUserProfileRequestTOUpdateUserProfileResponse(updateUserProfileRequest);
    }

}
