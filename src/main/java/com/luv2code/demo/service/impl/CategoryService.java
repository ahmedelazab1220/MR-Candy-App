package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final IFileHelper fileHelper;
    private final SystemMapper mapper;

    @Override
    public List<CategoryResponseDTO> getAllCategories() {

        log.info("Fetching all categories");

        return categoryRepository.findAllCategories();

    }

    @Override
    public ResponseEntity<ApiResponseDTO> deleteCategory(String name) throws IOException {

        log.info("Attempting to delete category with name: {}", name);

        Optional<Category> category = categoryRepository.findByName(name);

        if (category.isEmpty()) {
            log.warn("Category with name: {} not found", name);
            throw new NotFoundException(NotFoundTypeException.CATEGORY + " Not Found!");
        }

        log.info("Deleting image from file system for category: {}", name);
        fileHelper.deleteImageFromFileSystem(category.get().getImageUrl());

        log.info("Deleting category from repository: {}", name);
        categoryRepository.delete(category.get());

        return ResponseEntity.ok(new ApiResponseDTO("Success Delete Category."));

    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO)
            throws IllegalStateException, IOException {

        log.info("Creating new category with name: {}", categoryRequestDTO.getName());

        String imageUrl = fileHelper.uploadFileToFileSystem(categoryRequestDTO.getImage());

        Category category = mapper.categoryRequestDTOTOCategory(categoryRequestDTO);

        category.setImageUrl(imageUrl);

        CategoryResponseDTO categoryDto = mapper.categoryTOCategoryResponseDTO(categoryRepository.save(category));

        log.info("Category created successfully with name: {}", categoryDto.getName());

        return categoryDto;

    }

    @Transactional
    @Override
    public CategoryResponseDTO updateCategory(String name, CategoryRequestDTO categoryRequestDTO)
            throws IllegalStateException, IOException {

        log.info("Updating category with name: {}", name);

        Optional<Category> category = categoryRepository.findByName(name);

        if (category.isEmpty()) {

            log.warn("Category with name: {} not found", name);

            throw new NotFoundException(NotFoundTypeException.CATEGORY + " Not Found!");
        }

        if (categoryRequestDTO.getName() != null) {

            log.info("Updating category name to: {}", categoryRequestDTO.getName());

            category.get().setName(categoryRequestDTO.getName());
        }

        if (categoryRequestDTO.getImage() != null) {

            log.info("Updating category image for: {}", name);

            fileHelper.deleteImageFromFileSystem(category.get().getImageUrl());

            String imageUrl = fileHelper.uploadFileToFileSystem(categoryRequestDTO.getImage());

            category.get().setImageUrl(imageUrl);

        }

        log.info("Category updated successfully with ID: {}", category.get().getId());

        return mapper.categoryTOCategoryResponseDTO(categoryRepository.save(category.get()));

    }

    @Override
    public Category getCategorySetter(String name) {

        log.info("Fetching category setter for name: {}", name);

        Optional<Category> category = categoryRepository.findCategorySetterDTOByName(name).map(mapper::categorySetterDTOTOcaCategory);

        if (category.isEmpty()) {
            log.warn("Category with name: {} not found", name);
            throw new NotFoundException(NotFoundTypeException.CATEGORY + " Not Found!");
        }

        log.info("Category found with ID: {}", category.get().getId());

        return category.get();

    }

}
