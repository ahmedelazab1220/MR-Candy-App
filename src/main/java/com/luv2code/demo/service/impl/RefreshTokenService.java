package com.luv2code.demo.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.dto.response.JwtResponseDTO;
import com.luv2code.demo.entity.RefreshToken;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.exc.custom.ExpiredException;
import com.luv2code.demo.repository.RefreshTokenRepository;
import com.luv2code.demo.security.SecurityUser;
import com.luv2code.demo.service.IJwtService;
import com.luv2code.demo.service.IRefreshTokenService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final IJwtService jwtService;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {

        log.info("Saving refresh token for user: {}", refreshToken.getUser().getEmail());

        return refreshTokenRepository.save(refreshToken);

    }

    private RefreshToken findByToken(String token) {

        log.info("Finding refresh token: {}", token);

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);

        if (refreshToken.isEmpty()) {
            log.error("Refresh token not found or revoked: {}", token);
            throw new NotFoundException(NotFoundTypeException.TOKEN + " Not Found OR Revoked!");
        }

        if (refreshToken.get().getExpireDate().compareTo(Instant.now()) < 0) {
            log.error("Refresh token expired: {}", token);
            throw new ExpiredException("Token is expired. Please make a new login..!");
        }

        log.info("Refresh token is valid: {}", token);
        return refreshToken.get();

    }

    @Transactional
    @Override
    public JwtResponseDTO generateNewToken(String token) {

        log.info("Generating new token pair for refresh token: {}", token);

        RefreshToken refreshToken = findByToken(token);

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user.getEmail(), new SecurityUser(user));
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshToken.setExpireDate(jwtService.extractExpiration(newRefreshToken).toInstant());

        refreshToken.setToken(newRefreshToken);
        refreshTokenRepository.save(refreshToken);

        log.info("Generated new access token and refresh token for user: {}", user.getEmail());

        return createJwtResponse(accessToken, newRefreshToken);

    }

    private JwtResponseDTO createJwtResponse(String accessToken, String refreshToken) {

        log.info("Creating JWT response DTO with access token and refresh token");

        return new JwtResponseDTO(accessToken, refreshToken);

    }

	@Override
	public void deleteToken(String token) {

	   RefreshToken refreshToken = findByToken(token);
		
       log.info("Delete Token :{} " + token + "for user with email: {}" + refreshToken.getUser().getEmail());
		
       refreshTokenRepository.delete(refreshToken);
       
	}

}
