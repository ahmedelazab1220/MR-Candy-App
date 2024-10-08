package com.luv2code.demo.helper.impl;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.luv2code.demo.helper.IOtpGenerator;
import com.luv2code.demo.helper.ISecretKeyGenerator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OtpGenerator implements IOtpGenerator {

    private final ISecretKeyGenerator secretKeyGenerator;

    @Override
    public String generateTOTP() {
        try {

            // Generate and decode the secret key
            String secret = secretKeyGenerator.generateBase64SecretKey();
            byte[] key = Base64.getDecoder().decode(secret);
            log.debug("Secret key decoded from base64: {}", secret);

            // Get the current time interval
            Long timeInterval = Instant.now().getEpochSecond() / 30;
            log.debug("Current time interval: {}", timeInterval);

            // Convert the time interval to a byte array
            byte[] data = ByteBuffer.allocate(8).putLong(timeInterval).array();

            // Create HMAC-SHA1 using the key and the data
            Mac hmacSha1 = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA1");
            hmacSha1.init(keySpec);
            byte[] hash = hmacSha1.doFinal(data);
            log.debug("HMAC-SHA1 hash: {}", Base64.getEncoder().encodeToString(hash));

            // Extract the dynamic binary code
            Integer offset = hash[hash.length - 1] & 0x0F;
            Integer binaryCode = ((hash[offset] & 0x7F) << 24) | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8) | (hash[offset + 3] & 0xFF);
            log.debug("Dynamic binary code: {}", binaryCode);

            // Generate the OTP
            Integer otp = binaryCode % 1000000;
            log.info("Generated OTP: {}", otp);

            // Format the OTP to be a 6-digit number
            return String.format("%06d", otp);
        } catch (GeneralSecurityException e) {
            log.error("Error generating TOTP", e);
            throw new RuntimeException("Error generating TOTP", e);
        }
    }

}
