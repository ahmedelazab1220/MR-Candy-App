package com.luv2code.demo.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.response.JwtResponseDTO;
import com.luv2code.demo.entity.RefreshToken;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.exc.custom.TokenExpiredException;
import com.luv2code.demo.repository.RefreshTokenRepository;
import com.luv2code.demo.security.SecurityUser;
import com.luv2code.demo.service.IJwtService;
import com.luv2code.demo.service.IRefreshTokenService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final IJwtService jwtService;

	@Override
	public RefreshToken save(RefreshToken refreshToken) {

		return refreshTokenRepository.save(refreshToken);

	}

	@Override
	public RefreshToken findByToken(String token) {

		return getToken(token);

	}

	@Override
	public void deleteByEntity(RefreshToken refreshToken) {

		refreshTokenRepository.delete(refreshToken);

	}

	@Transactional
	@Override
	public JwtResponseDTO generateNewToken(String token) {

		RefreshToken refreshToken = getToken(token);

		Optional<User> user = Optional.ofNullable(refreshToken.getUser());

		refreshToken.setExpireDate(Instant.now());

		String accessToken = jwtService.generateToken(user.get().getEmail(), user.map(SecurityUser::new).get());
		String newRefreshToken = jwtService.generateRefreshToken(user.get().getEmail());

		refreshToken.setToken(newRefreshToken);
		refreshTokenRepository.save(refreshToken);
		
		return createJwtResponse(accessToken, newRefreshToken);

	}

	private RefreshToken getToken(String token) {

		Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);

		if (refreshToken.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.TOKEN + " Not Found OR Revoked!");
		}

		if (refreshToken.get().getExpireDate().compareTo(Instant.now()) < 0) {
			throw new TokenExpiredException("Token is expired. Please make a new login..!");
		}

		return refreshToken.get();

	}

	private JwtResponseDTO createJwtResponse(String accessToken, String refreshToken) {

		return new JwtResponseDTO(accessToken, refreshToken);

	}

}
