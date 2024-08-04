package com.luv2code.demo.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CartRequestDTO {

	@NotBlank
	private String user_email;

	@NotNull
	private BigDecimal totalPrice;

	@NotNull
	private List<OrderItemRequestDTO> orderItems;
	
}
