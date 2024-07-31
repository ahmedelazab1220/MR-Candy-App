package com.luv2code.demo.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.luv2code.demo.security.SecurityUser;
import com.luv2code.demo.service.IJwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService implements IJwtService {

	@Value("${security.jwt.secret-key}")
	private String secretKey;

	@Value("${security.jwt.expiration-time}")

	private long jwtExpiration;

	@Value("${security.jwt.refresh-token.expiration-time}")
	private long refreshTokenExpiration;

	@Override
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	@Override
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	@Override
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	@Override
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	@Override
	public String generateToken(String username, UserDetails userDetail) {
		Map<String, Object> claims = new HashMap<>();
		return buildToken(claims, username, jwtExpiration, userDetail);
	}

	@Override
	public String generateRefreshToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return buildToken(claims, username, refreshTokenExpiration, null);
	}

	private String buildToken(Map<String, Object> claims, String username, long expirationTime,
			UserDetails userDetail) {

		JwtBuilder jwtBuilder = Jwts.builder().setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime));

		if (userDetail != null) {
			jwtBuilder.claim("phoneNumber", ((SecurityUser) userDetail).getUser().getPhoneNumber())
					.claim("fullName", ((SecurityUser) userDetail).getUser().getFullName())
					.claim("role", ((SecurityUser) userDetail).getUser().getRole().getRole())
					.claim("email", ((SecurityUser) userDetail).getUser().getEmail())
					.claim("imageUrl", ((SecurityUser) userDetail).getUser().getImageUrl());
		}

		return jwtBuilder.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
