package com.luv2code.demo.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderItemRequestDTO {

	private Long product_id;

	private Integer quantity;

	private BigDecimal price;

}
