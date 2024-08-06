package com.luv2code.demo.service.impl;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.repository.UserRepository;
import com.luv2code.demo.security.SecurityUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SystemMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) {

        log.info("Attempting to load user by username: {}", username);

        Optional<User> user
                = userRepository.findUserAuthenticationDetailsByEmail(username).map(mapper::userAuthenticationResponseDTOTOUser);

        if (user.isEmpty()) {
            log.error("User not found with username: {}", username);
            throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
        }

        log.info("Successfully loaded user: {}", username);

        return user.map(SecurityUser::new).get();
    }

}
