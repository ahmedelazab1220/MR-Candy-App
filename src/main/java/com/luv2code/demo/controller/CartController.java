package com.luv2code.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;
import com.luv2code.demo.service.ICartService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/carts")
@AllArgsConstructor
public class CartController {

	private final ICartService cartService;
	
	@PostMapping("")
	public CartItemResponseDTO addCartItem(@Valid @RequestBody CartRequestDTO cartRequestDTO) {
		
		return cartService.addCartItem(cartRequestDTO);
		
	}
	
	@DeleteMapping("/{cartId}")
	public ResponseEntity<ApiResponseDTO> deleteCartItem(@PathVariable(name="cartId" , required = true) Long cartId){
		
		return cartService.deleteCartItem(cartId);
		
	}
	
	@PutMapping("")
	public ResponseEntity<Map<String, Integer>> updateCartItemQuantity(@RequestParam(required = true) Integer newQuantity ,@RequestParam(required = true) Long cartId){
		
		return cartService.updateCartItem(newQuantity, cartId);
		
	}
	
	@GetMapping("")
	public ResponseEntity<Map<String, Object>> getAllCartItem(@RequestParam(required = true) String userEmail){
		
		return cartService.getAllCartItemsForUserEmail(userEmail);
		
	}
	
}
