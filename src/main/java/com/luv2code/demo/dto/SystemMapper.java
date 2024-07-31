package com.luv2code.demo.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.luv2code.demo.dto.request.RegisterRequestDTO;
import com.luv2code.demo.dto.response.UserAuthenticationResponseDTO;
import com.luv2code.demo.dto.response.UserTokenResponseDTO;
import com.luv2code.demo.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemMapper {

	User userAuthenticationResponseDTOTOUser(UserAuthenticationResponseDTO userAuthenticationResponseDTO);

	User registerRequestDTOTOUser(RegisterRequestDTO registerRequestDTO);

	User userTokenResponseDTOTOUser(UserTokenResponseDTO userTokenResponseDTO);
}
