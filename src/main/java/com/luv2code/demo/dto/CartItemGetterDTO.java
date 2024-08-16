package com.luv2code.demo.dto;

import com.luv2code.demo.entity.CartItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemGetterDTO {

	private CartItem cartItem;
	
	private Integer productQuantity;
	
}
