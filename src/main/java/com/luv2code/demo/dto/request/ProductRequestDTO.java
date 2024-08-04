package com.luv2code.demo.dto.request;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

	@NotBlank
	private String name;
	
	@NotBlank
	private String description;
	
	@NotBlank
	private BigDecimal price;

	@NotBlank
	private Integer quantity;
	
	private String discount;
	
	private String size;
	
	private String type;
	
	@NotBlank
	private Long salesCount; 
	
	@NotNull
	private MultipartFile image;
	
	@NotBlank
	private String categoryName;
	
	@NotBlank
	private String companyName;
}
