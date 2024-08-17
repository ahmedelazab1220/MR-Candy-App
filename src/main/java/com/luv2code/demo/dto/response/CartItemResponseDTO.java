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

    private String productName;
    
    private String productDescription;
    
    private String productCompanyName;

    private Integer cartItemQuantity;
    
    private BigDecimal cartItemPrice;
    
    private Long cartId;
	
}
