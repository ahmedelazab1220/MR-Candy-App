package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.CategoryRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CategoryResponseDTO;
import com.luv2code.demo.entity.Category;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.CategoryRepository;
import com.luv2code.demo.service.ICategoryService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {

	private final CategoryRepository categoryRepository;
	private final IFileHelper fileHelper;
	private final SystemMapper mapper;

	@Override
	public List<CategoryResponseDTO> getAllCategories() {

		return categoryRepository.findAllCategories();

	}

	@Override
	public ResponseEntity<ApiResponseDTO> deleteCategoryByName(String name) {

		categoryRepository.deleteByName(name);

		return ResponseEntity.ok(new ApiResponseDTO("Success Delete Category."));

	}

	@Override
	public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO)
			throws IllegalStateException, IOException {

		String imageUrl = fileHelper.uploadFileToFileSystem(categoryRequestDTO.getImage());

		Category category = mapper.categoryRequestDTOTOCategory(categoryRequestDTO);

		category.setImageUrl(imageUrl);

		CategoryResponseDTO categoryDto = mapper.categoryTOCategoryResponseDTO(categoryRepository.save(category));

		return categoryDto;

	}

	@Override
	public Boolean existCategoryByName(String name) {

        Boolean categoryExist = categoryRepository.existsByName(name);
        
        if(!categoryExist) {
        	throw new NotFoundException(NotFoundTypeException.CATEGORY + " Not Found!");
        }
		
		return categoryExist;
		
	}

	@Override
	public CategoryResponseDTO updateCategory(String name, CategoryRequestDTO categoryRequestDTO) throws IllegalStateException, IOException {
		
		Optional<Category> category = categoryRepository.findByName(name); 
		
		if(category.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.CATEGORY + " Not Found!");
		}
		
		if(categoryRequestDTO.getName() != null) {
			category.get().setName(categoryRequestDTO.getName());
		}
		
		if(categoryRequestDTO.getImage() != null) {
			
			String imageUrl = fileHelper.uploadFileToFileSystem(categoryRequestDTO.getImage());
			
			category.get().setImageUrl(imageUrl);
			
		}
		
		return mapper.categoryTOCategoryResponseDTO(categoryRepository.save(category.get()));
		
	}

}
