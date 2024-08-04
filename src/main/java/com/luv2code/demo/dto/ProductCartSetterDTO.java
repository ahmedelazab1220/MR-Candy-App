package com.luv2code.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCartSetterDTO {

	private Long id;

    private String name;
    
    private Integer quantity;
	
}
