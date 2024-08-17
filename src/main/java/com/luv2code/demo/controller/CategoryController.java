package com.luv2code.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.CategoryRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CategoryResponseDTO;
import com.luv2code.demo.service.ICategoryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/categories")
@AllArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("")
    public List<CategoryResponseDTO> getAllCategories() {

        return categoryService.getAllCategories();

    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO> deleteCategory(@RequestParam(required = true) String name)
            throws IOException {

        return categoryService.deleteCategory(name);

    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CategoryResponseDTO createCategory(@Valid @ModelAttribute CategoryRequestDTO categoryRequestDTO)
            throws IllegalStateException, IOException {

        return categoryService.createCategory(categoryRequestDTO);

    }

    @PutMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDTO updateCategory(@RequestParam(required = true) String categoryName,
            @Valid @ModelAttribute CategoryRequestDTO categoryRequestDTO) throws IllegalStateException, IOException {

        return categoryService.updateCategory(categoryName, categoryRequestDTO);

    }

}
