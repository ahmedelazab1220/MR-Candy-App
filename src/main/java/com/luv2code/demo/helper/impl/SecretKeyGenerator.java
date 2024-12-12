package com.luv2code.demo.helper.impl;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.luv2code.demo.helper.ISecretKeyGenerator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SecretKeyGenerator implements ISecretKeyGenerator {

    @Override
    public String generateBase64SecretKey() {

        log.info("Generating Base64-encoded secret key...");

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String secretKey = Base64.getEncoder().encodeToString(bytes);

        log.debug("Generated secret key: {}", secretKey);

        return secretKey;
    }

}
