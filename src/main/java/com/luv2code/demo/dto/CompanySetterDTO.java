package com.luv2code.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySetterDTO {

	private Long id;
	
	private String name;
	
	private String imageUrl;
	
}
