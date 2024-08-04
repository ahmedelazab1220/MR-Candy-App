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
public class ProductDetailsResponseDTO {

    private Long id;

    private String type;

    private String imageUrl;

    private String description;

    private String size;

    private BigDecimal price;

    private Integer quantity;

    private String categoryName;

    private String companyName;

    private String companyImageUrl;

}
