package com.luv2code.demo.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

	private Long productId;

    private String name;
    
    private String description;
    
    private String companyName;

    private Integer quantity;
    
    private BigDecimal totalPrice;
    
    private Long cartId;
	
}
