package com.luv2code.demo.service;

import com.luv2code.demo.dto.response.JwtResponseDTO;
import com.luv2code.demo.entity.RefreshToken;

public interface IRefreshTokenService {

    RefreshToken save(RefreshToken refreshToken);

    JwtResponseDTO generateNewToken(String token);

}
