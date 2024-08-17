package com.luv2code.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.service.IOrderService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("${api.version}/orders")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestParam(required = true) String userEmail) {

        return orderService.createOrder(userEmail);

    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO> deleteOrder(@RequestParam(required = true) Long orderId) {

        return orderService.deleteOrder(orderId);

    }

}
