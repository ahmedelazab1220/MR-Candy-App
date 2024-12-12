package com.luv2code.demo.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.response.ApiResponseDTO;

public interface IOrderService {

	ResponseEntity<Map<String , Object>> createOrder(String userEmail);
	
	ResponseEntity<ApiResponseDTO> deleteOrder(Long orderId);
	
}
