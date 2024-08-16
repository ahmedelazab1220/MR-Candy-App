package com.luv2code.demo.service;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;

public interface ICartService {

    ResponseEntity<CartItemResponseDTO> addCartItem(CartRequestDTO cartRequestDTO);
/*
    ResponseEntity<ApiResponseDTO> deleteCartItem(Long theId);
    
    ResponseEntity<Map<String,Integer>> updateCartItem(Integer newQuantity,Long theId);
    
    List<CartItemResponseDTO> getAllCartItems();
*/
}
