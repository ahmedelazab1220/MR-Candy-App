package com.luv2code.demo.dto.setter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSetterDTO {

    private Long id;

    private String name;

    private String imageUrl;

}
