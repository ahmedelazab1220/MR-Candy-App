package com.luv2code.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBestSellerResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String discount;

    private String imageUrl;

}
