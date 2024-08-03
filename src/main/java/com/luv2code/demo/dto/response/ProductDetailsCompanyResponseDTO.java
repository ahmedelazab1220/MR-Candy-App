package com.luv2code.demo.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsCompanyResponseDTO {

	private Long id;
	
	private String description;
	
	private String discount;
		
	private String imageUrl;
	
	private BigDecimal price;
	
	private Integer quantity;
	
}
