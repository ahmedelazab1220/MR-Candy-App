package com.luv2code.demo.helper.impl;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.luv2code.demo.helper.ISecretKeyGenerator;

@Service
public class SecretKeyGenerator implements ISecretKeyGenerator {

	@Override
	public String generateBase64SecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		return Base64.getEncoder().encodeToString(bytes);
	}

}
