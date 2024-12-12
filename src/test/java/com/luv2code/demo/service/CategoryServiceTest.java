package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.CategoryRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CategoryResponseDTO;
import com.luv2code.demo.entity.Category;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.CategoryRepository;
import com.luv2code.demo.service.impl.CategoryService;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private IFileHelper fileHelper;

    @Mock
    private SystemMapper mapper;

    private Category category;

    private CategoryRequestDTO categoryRequestDTO;

    private CategoryResponseDTO categoryResponseDTO;

    private MultipartFile multipartFile;

    private String imageUrl;

    private String categoryName;

    /**
     * Sets up the necessary mocks and initializes the role and user objects
     * before each test case.
     *
     * @throws Exception if there is an error with the mocks initialization.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        imageUrl = "http://example.com/image.png";
        categoryName = "Category";

        category = new Category();
        category.setId(1L);
        category.setCreatedAt(LocalDateTime.now());
        category.setName(categoryName);
        category.setImageUrl(imageUrl);

        multipartFile = new MockMultipartFile("image", "image.png", "image/png", "imageContent".getBytes());

    }

    CategoryRequestDTO getCategoryRequestDTO() {
        return new CategoryRequestDTO(categoryName, multipartFile);
    }

    CategoryResponseDTO getCategoryResponseDTO() {
        return new CategoryResponseDTO(categoryName, imageUrl);
    }

    /**
     * Test case to verify that the getAllCategories method of the
     * CategoryService returns a list of CategoryResponseDTO objects
     * successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void shouldGetAllCategoriesSuccessfully() {

        List<CategoryResponseDTO> categoryResponseDTOs = List.of(new CategoryResponseDTO(categoryName, imageUrl),
                new CategoryResponseDTO(categoryName, imageUrl));

        when(categoryRepository.findAllCategories()).thenReturn(categoryResponseDTOs);

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertEquals(categoryResponseDTOs, result);

        verify(categoryRepository, times(1)).findAllCategories();

    }

    /**
     * Test case to verify that the getAllCategories() method returns an empty
     * list when there are no categories.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void shouldGetAllCategoriesWhenNoCategories() {

        List<CategoryResponseDTO> categories = List.of(); // Empty list

        when(categoryRepository.findAllCategories()).thenReturn(categories);

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertTrue(result.isEmpty());

        verify(categoryRepository, times(1)).findAllCategories();

    }

    /**
     * Test case to verify that the createCategory method of the CategoryService
     * creates a category successfully.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldCreateCategorySuccessfully() throws IOException {

        categoryRequestDTO = getCategoryRequestDTO();
        categoryResponseDTO = getCategoryResponseDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.categoryRequestDTOTOCategory(categoryRequestDTO)).thenReturn(category);
        when(mapper.categoryTOCategoryResponseDTO(any(Category.class))).thenReturn(categoryResponseDTO);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.createCategory(categoryRequestDTO);

        assertEquals(categoryResponseDTO.getName(), result.getName());
        assertEquals(categoryResponseDTO.getImageUrl(), result.getImageUrl());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).categoryRequestDTOTOCategory(categoryRequestDTO);
        verify(mapper, times(1)).categoryTOCategoryResponseDTO(any(Category.class));
        verify(categoryRepository, times(1)).save(any(Category.class));

    }

    /**
     * Test case to verify that an exception is thrown when creating a category
     * with invalid data.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldThrowExceptionWhenCreatingCategoryWhenSentInvalidData() throws IOException {

        CategoryRequestDTO invalidRequest = new CategoryRequestDTO("", null);

        assertThrows(NullPointerException.class, () -> {
            categoryService.createCategory(invalidRequest);
        });

        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(categoryRepository, times(0)).save(any(Category.class));
    }

    /**
     * Test case to verify that an exception is thrown when saving a category.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldThrowExceptionWhenSaveCategory() throws IOException {

        categoryRequestDTO = getCategoryRequestDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.categoryRequestDTOTOCategory(categoryRequestDTO)).thenReturn(category);
        doThrow(new RuntimeException("Failed to save category")).when(categoryRepository).save(any(Category.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(categoryRequestDTO);
        });

        assertEquals("Failed to save category", exception.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(categoryRepository, times(1)).save(category);
        verify(mapper, times(1)).categoryRequestDTOTOCategory(categoryRequestDTO);
        verify(mapper, times(0)).categoryTOCategoryResponseDTO(any(Category.class));

    }

    /**
     * Test case to verify that an exception is thrown when file upload fails
     * when creating a category.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldThrowExceptionWhenFileUploadFailsWhenCreatingCategory() throws IOException {

        categoryRequestDTO = getCategoryRequestDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class)))
                .thenThrow(new IOException("File upload failed"));

        IOException exception = assertThrows(IOException.class, () -> {
            categoryService.createCategory(categoryRequestDTO);
        });

        assertEquals("File upload failed", exception.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(categoryRepository, times(0)).save(any(Category.class));
        verify(mapper, times(0)).categoryRequestDTOTOCategory(any(CategoryRequestDTO.class));
        verify(mapper, times(0)).categoryTOCategoryResponseDTO(any(Category.class));

    }

    /**
     * Test case to verify that an exception is thrown when the mapping from
     * CategoryRequestDTO to Category fails during the creation of a category.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldHandleCategoryRequestDTOTOCategoryFailureWhenCreatingCategory() throws IOException {

        CategoryRequestDTO categoryRequestDTO = getCategoryRequestDTO();
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        doThrow(new RuntimeException("Mapping to Category failed")).when(mapper)
                .categoryRequestDTOTOCategory(categoryRequestDTO);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(categoryRequestDTO);
        });

        assertEquals("Mapping to Category failed", thrown.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).categoryRequestDTOTOCategory(categoryRequestDTO);
        verify(mapper, times(0)).categoryTOCategoryResponseDTO(any(Category.class));
        verify(categoryRepository, times(0)).save(any(Category.class));
    }

    /**
     * Test case to verify that an exception is thrown when the mapping from
     * Category to CategoryResponseDTO fails during the creation of a category.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldHandleCategoryTOCategoryResponseDTOFailureWhenCreatingCategory() throws IOException {

        CategoryRequestDTO categoryRequestDTO = getCategoryRequestDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.categoryRequestDTOTOCategory(categoryRequestDTO)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        doThrow(new RuntimeException("Mapping to ResponseDTO failed")).when(mapper)
                .categoryTOCategoryResponseDTO(category);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(categoryRequestDTO);
        });

        assertEquals("Mapping to ResponseDTO failed", thrown.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).categoryRequestDTOTOCategory(categoryRequestDTO);
        verify(mapper, times(1)).categoryTOCategoryResponseDTO(category);
        verify(categoryRepository, times(1)).save(any(Category.class));

    }

    /**
     * Test case to verify that a NotFoundException is thrown when the category
     * is not found when deleting a category.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldThrowNotFoundExceptionWhenCategoryNotFoundWhenDeletingCategory() throws IOException {

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoryService.deleteCategory(categoryName);
        });

        assertEquals(NotFoundTypeException.CATEGORY + " Not Found!", exception.getMessage());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(categoryRepository, times(0)).delete(any(Category.class));

    }

    /**
     * Test case to verify that the deleteCategory method handles image deletion
     * failure when deleting a category.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldHandleImageDeletionFailureWhenDeletingCategory() throws IOException {

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        doThrow(new IOException("Failed to delete image")).when(fileHelper).deleteImageFromFileSystem(category.getImageUrl());
        doNothing().when(categoryRepository).delete(category);

        IOException exception = assertThrows(IOException.class, () -> {
            categoryService.deleteCategory(categoryName);
        });

        assertEquals("Failed to delete image", exception.getMessage());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(category.getImageUrl());
        verify(categoryRepository, times(0)).delete(category);

    }

    /**
     * This test method verifies that the deleteCategory method successfully
     * deletes a category.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldDeleteCategorySuccessfully() throws IOException {

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(fileHelper.deleteImageFromFileSystem(category.getImageUrl())).thenReturn(true);
        doNothing().when(categoryRepository).delete(any(Category.class));

        ResponseEntity<ApiResponseDTO> response = categoryService.deleteCategory(categoryName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success Delete Category.", response.getBody().getMessage());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(category.getImageUrl());
        verify(categoryRepository, times(1)).delete(category);

    }

    /**
     * Test case to verify that the deleteCategory method handles exceptions
     * during deletion of a category.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldHandleExceptionDuringDeleteWhenDeletingCategory() throws IOException {

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(fileHelper.deleteImageFromFileSystem(category.getImageUrl())).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(categoryRepository).delete(category);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(categoryName);
        });

        assertEquals("Database error", exception.getMessage());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(category.getImageUrl());
        verify(categoryRepository, times(1)).delete(category);

    }

    /**
     * Test case to verify that the updateCategory method of the CategoryService
     * updates a category successfully.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldUpdateCategorySuccessfully() throws IOException {
        categoryRequestDTO = getCategoryRequestDTO();
        categoryResponseDTO = getCategoryResponseDTO();

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.categoryTOCategoryResponseDTO(any(Category.class))).thenReturn(categoryResponseDTO);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.updateCategory(categoryName, categoryRequestDTO);

        assertEquals(categoryResponseDTO.getName(), result.getName());
        assertEquals(categoryResponseDTO.getImageUrl(), result.getImageUrl());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(category.getImageUrl());
        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(mapper, times(1)).categoryTOCategoryResponseDTO(any(Category.class));
    }

    /**
     * Test case to verify that a NotFoundException is thrown when the category
     * does not exist during the updateCategory method.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldThrowNotFoundExceptionWhenUpdateCategoryDoesNotExist() throws IOException {
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoryService.updateCategory(categoryName, getCategoryRequestDTO());
        });

        assertEquals(NotFoundTypeException.CATEGORY + " Not Found!", exception.getMessage());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(categoryRepository, times(0)).save(any(Category.class));
    }

    /**
     * Test case to verify that an IOException is thrown when the image deletion
     * fails during the updateCategory method.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldThrowIOExceptionWhenImageDeletionFailsWhenUpdateCategory() throws IOException {
        categoryRequestDTO = getCategoryRequestDTO();

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        doThrow(new IOException("Failed to delete image")).when(fileHelper)
                .deleteImageFromFileSystem(category.getImageUrl());

        IOException exception = assertThrows(IOException.class, () -> {
            categoryService.updateCategory(categoryName, categoryRequestDTO);
        });

        assertEquals("Failed to delete image", exception.getMessage());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(category.getImageUrl());
        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(categoryRepository, times(0)).save(any(Category.class));
    }

    /**
     * Test case to verify that an IOException is thrown when the image upload
     * fails during the updateCategory method.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldThrowIOExceptionWhenImageUploadFailsWhenUpdateCategory() throws IOException {
        categoryRequestDTO = getCategoryRequestDTO();

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(fileHelper.deleteImageFromFileSystem(category.getImageUrl())).thenReturn(true);
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class)))
                .thenThrow(new IOException("File upload failed"));

        IOException exception = assertThrows(IOException.class, () -> {
            categoryService.updateCategory(categoryName, categoryRequestDTO);
        });

        assertEquals("File upload failed", exception.getMessage());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(category.getImageUrl());
        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(categoryRepository, times(0)).save(any(Category.class));
    }

    /**
     * This test method verifies that the category service does not update the
     * category when no changes are provided.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldNotUpdateCategoryWhenNoChangesProvided() throws IOException {

        CategoryRequestDTO emptyRequestDTO = new CategoryRequestDTO(null, null);

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(mapper.categoryTOCategoryResponseDTO(any(Category.class))).thenReturn(getCategoryResponseDTO());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.updateCategory(categoryName, emptyRequestDTO);

        assertEquals(categoryName, result.getName());
        assertEquals(imageUrl, result.getImageUrl());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(mapper, times(1)).categoryTOCategoryResponseDTO(any(Category.class));

    }

}
