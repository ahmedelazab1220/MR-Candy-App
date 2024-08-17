package com.luv2code.demo.dto;

import com.luv2code.demo.entity.Company;

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

    private Company company;

    private Integer quantity;

}
