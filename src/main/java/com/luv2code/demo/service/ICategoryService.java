package com.luv2code.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.CategoryRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CategoryResponseDTO;

public interface ICategoryService {

	List<CategoryResponseDTO> getAllCategories();

	ResponseEntity<ApiResponseDTO> deleteCategoryByName(String name);

	CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) throws IllegalStateException, IOException;

	Boolean existCategoryByName(String name);
	
	CategoryResponseDTO updateCategory(String name , CategoryRequestDTO categoryRequestDTO) throws IllegalStateException, IOException;
	
}
