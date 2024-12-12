package com.luv2code.demo.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;

public interface ICartService {

    CartItemResponseDTO addCartItem(CartRequestDTO cartRequestDTO);

    ResponseEntity<ApiResponseDTO> deleteCartItem(Long theId);
    
    ResponseEntity<Map<String,Integer>> updateCartItem(Integer newQuantity,Long theId);
    
    ResponseEntity<Map<String,Object>> getAllCartItemsForUserEmail(String userEmail);

}
