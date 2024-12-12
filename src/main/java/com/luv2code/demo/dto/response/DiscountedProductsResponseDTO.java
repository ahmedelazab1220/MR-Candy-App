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
public class DiscountedProductsResponseDTO {

    private Long id;

    private String description;

    private String discount;

    private String imageUrl;

    private BigDecimal price;

    private String companyName;

}
