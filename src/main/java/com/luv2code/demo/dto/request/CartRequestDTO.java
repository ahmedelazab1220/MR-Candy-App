package com.luv2code.demo.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartRequestDTO {

    @NotBlank
    private String email;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    @Valid
    private List<CartItemRequestDTO> orderItems;

}
