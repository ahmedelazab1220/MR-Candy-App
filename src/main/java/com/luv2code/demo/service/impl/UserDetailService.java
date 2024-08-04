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

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SystemMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = Optional.of(mapper.userAuthenticationResponseDTOTOUser(
                userRepository.findUserAuthenticationDetailsByEmail(username).get()));

        if (!user.isPresent()) {
            throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
        }

        return user.map(SecurityUser::new).get();
    }

}
