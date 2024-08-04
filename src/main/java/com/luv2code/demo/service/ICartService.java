package com.luv2code.demo.service;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartResponseDTO;

public interface ICartService {

	ResponseEntity<CartResponseDTO> createOrder(CartRequestDTO cartRequestDTO);

	ResponseEntity<ApiResponseDTO> deleteOrder(Long theId);
	
}
