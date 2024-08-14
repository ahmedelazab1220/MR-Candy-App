/*package com.luv2code.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.service.ICartService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/carts")
@AllArgsConstructor
public class CartController {

    private final ICartService cartService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CartRequestDTO cartRequestDTO) {

        return cartService.createOrder(cartRequestDTO);

    }

    @DeleteMapping("/{theId}")
    public ResponseEntity<?> deleteOrder(@PathVariable(name = "theId") Long theId) {

        return cartService.deleteOrder(theId);

    }

}*/
