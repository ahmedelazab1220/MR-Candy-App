package com.luv2code.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCompanyResponseDTO {

    private Long id;

    private String name;

    private String imageUrl;

}
