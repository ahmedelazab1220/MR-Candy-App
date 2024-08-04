package com.luv2code.demo.service;

import com.luv2code.demo.dto.response.JwtResponseDTO;
import com.luv2code.demo.entity.RefreshToken;

public interface IRefreshTokenService {

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken findByToken(String token);

    void deleteByEntity(RefreshToken refreshToken);

    JwtResponseDTO generateNewToken(String token);

}
