package com.luv2code.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductGetterDTO {

    private Long id;

    private String name;

    private Integer quantity;

    private Integer cartItemQuantity;

}
