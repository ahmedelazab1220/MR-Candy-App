package com.luv2code.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.CategoryRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CategoryResponseDTO;
import com.luv2code.demo.entity.Category;

public interface ICategoryService {

    List<CategoryResponseDTO> getAllCategories();

    ResponseEntity<ApiResponseDTO> deleteCategory(String name) throws IOException;

    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) throws IllegalStateException, IOException;

    CategoryResponseDTO updateCategory(String name, CategoryRequestDTO categoryRequestDTO)
            throws IllegalStateException, IOException;

    Category getCategorySetter(String name);

}
