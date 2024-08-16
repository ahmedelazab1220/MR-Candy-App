package com.luv2code.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCartSetterDTO {

    private Long id;

    private String name;
    
    private String description;
    
    private String companyName;

    private Integer quantity;

}
