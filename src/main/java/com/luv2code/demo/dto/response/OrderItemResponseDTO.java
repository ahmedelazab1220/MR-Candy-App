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
public class OrderItemResponseDTO {

	private String productName;
	
	private String productCompanyName;
	
	private Integer orderItemQuantity;
	
	private BigDecimal OrderItemPrice;
	
}
