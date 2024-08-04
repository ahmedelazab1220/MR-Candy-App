package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.dto.ProductSetterDTO;
import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.ProductRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.ProductBestSellerResponseDTO;
import com.luv2code.demo.dto.response.ProductCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;
import com.luv2code.demo.entity.Category;
import com.luv2code.demo.entity.Company;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.repository.ProductRepository;
import com.luv2code.demo.service.impl.ProductService;
import com.luv2code.demo.helper.IFileHelper;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SystemMapper mapper;

    @Mock
    private IFileHelper fileHelper;

    @Mock
    private ICategoryService categoryService;

    @Mock
    private ICompanyService companyService;

    @InjectMocks
    private ProductService productService;

    private ProductRequestDTO productRequestDTO;

    private ProductSetterDTO productSetterDTO;

    private Company company;

    private Category category;

    private Product product;

    private String imageUrl;

    private Long productId;

    private Pageable pageable;

    private MultipartFile multipartFile;

    /**
     * Initializes the mock objects and sets up the test data before each test
     * case.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        multipartFile = new MockMultipartFile("image", "image.png", "image/png", "imageContent".getBytes());

        imageUrl = "http://example.com/image.png";
        productId = 1L;

        productSetterDTO = new ProductSetterDTO();
        productSetterDTO.setId(productId);
        productSetterDTO.setName("Test Product");
        productSetterDTO.setImageUrl(imageUrl);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Test Product");
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setPrice(BigDecimal.valueOf(100.00));
        productRequestDTO.setQuantity(10);
        productRequestDTO.setCategoryName("Test Category");
        productRequestDTO.setCompanyName("Test Company");

        company = new Company();
        company.setId(1L);
        company.setImageUrl(imageUrl);
        company.setName("Test Company");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        product = new Product();
        product.setId(productId);
        product.setImageUrl(imageUrl);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setQuantity(10);
        product.setCategory(category);
        product.setCompany(company);
    }

    /**
     * Tests the successful creation of a product. Verifies that the correct
     * methods are called and the expected response is returned.
     *
     * @throws IllegalStateException if any illegal state occurs during
     * execution
     * @throws IOException if an I/O error occurs during file operations
     */
    @Test
    void shouldCreateProductSuccessfully() throws IllegalStateException, IOException {

        when(companyService.getCompanySetter(productRequestDTO.getCompanyName())).thenReturn(company);
        when(categoryService.getCategorySetter(productRequestDTO.getCategoryName())).thenReturn(category);
        when(fileHelper.uploadFileToFileSystem(productRequestDTO.getImage())).thenReturn(imageUrl);
        when(mapper.productRequestDTOTOProduct(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        ProductDetailsResponseDTO expectedResponse = new ProductDetailsResponseDTO();
        when(mapper.ProductTOproductDetailsResponseDTO(product)).thenReturn(expectedResponse);

        ProductDetailsResponseDTO actualResponse = productService.createProduct(productRequestDTO);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(companyService, times(1)).getCompanySetter(productRequestDTO.getCompanyName());
        verify(categoryService, times(1)).getCategorySetter(productRequestDTO.getCategoryName());
        verify(fileHelper, times(1)).uploadFileToFileSystem(productRequestDTO.getImage());
        verify(mapper, times(1)).productRequestDTOTOProduct(productRequestDTO);
        verify(productRepository, times(1)).save(product);
        verify(mapper, times(1)).ProductTOproductDetailsResponseDTO(product);

    }

    /**
     * Tests the scenario where an IOException is thrown during the creation of
     * a product. Verifies that the exception is properly handled and thrown.
     *
     * @throws IOException if an I/O error occurs during file operations
     */
    @Test
    void shouldThrowsIOExceptionWhenCreatingProduct() throws IOException {

        when(companyService.getCompanySetter(productRequestDTO.getCompanyName())).thenReturn(company);
        when(categoryService.getCategorySetter(productRequestDTO.getCategoryName())).thenReturn(category);
        when(fileHelper.uploadFileToFileSystem(productRequestDTO.getImage())).thenThrow(IOException.class);

        assertThrows(IOException.class, () -> productService.createProduct(productRequestDTO));

        verify(companyService, times(1)).getCompanySetter(productRequestDTO.getCompanyName());
        verify(categoryService, times(1)).getCategorySetter(productRequestDTO.getCategoryName());
        verify(fileHelper, times(1)).uploadFileToFileSystem(productRequestDTO.getImage());

    }

    /**
     * Tests the scenario where a RuntimeException is thrown when the mapper
     * fails to map ProductRequestDTO to Product during the creation of a
     * product. Verifies that the exception is properly handled and thrown.
     *
     * @throws IOException if an I/O error occurs during file operations
     */
    @Test
    void shouldThrowRunTimeExceptionWhenMapperFailsOnRequestDTOToProductWhenCreatingProduct() throws IOException {

        when(companyService.getCompanySetter(productRequestDTO.getCompanyName())).thenReturn(company);
        when(categoryService.getCategorySetter(productRequestDTO.getCategoryName())).thenReturn(category);
        when(fileHelper.uploadFileToFileSystem(productRequestDTO.getImage())).thenReturn("test-image-url");
        when(mapper.productRequestDTOTOProduct(productRequestDTO)).thenThrow(new RuntimeException("Failed to map ProductRequestDTO to Product entity"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productRequestDTO);
        });

        assertEquals("Failed to map ProductRequestDTO to Product entity", exception.getMessage());

        verify(companyService, times(1)).getCompanySetter(productRequestDTO.getCompanyName());
        verify(categoryService, times(1)).getCategorySetter(productRequestDTO.getCategoryName());
        verify(fileHelper, times(1)).uploadFileToFileSystem(productRequestDTO.getImage());
        verify(mapper, times(1)).productRequestDTOTOProduct(productRequestDTO);

    }

    /**
     * Tests the scenario where a RuntimeException is thrown when the mapper
     * fails to map Product to ProductDetailsResponseDTO during the creation of
     * a product. Verifies that the exception is properly handled and thrown.
     *
     * @throws IOException if an I/O error occurs during file operations
     */
    @Test
    void shouldThrowRunTimeExceptionWhenMapperFailsOnProductToResponseDTOWhenCreatingProduct() throws IOException {

        when(companyService.getCompanySetter(productRequestDTO.getCompanyName())).thenReturn(company);
        when(categoryService.getCategorySetter(productRequestDTO.getCategoryName())).thenReturn(category);
        when(fileHelper.uploadFileToFileSystem(productRequestDTO.getImage())).thenReturn("test-image-url");
        when(mapper.productRequestDTOTOProduct(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(mapper.ProductTOproductDetailsResponseDTO(product)).thenThrow(new RuntimeException("Failed to map Product entity to ProductDetailsResponseDTO"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productRequestDTO);
        });

        assertEquals("Failed to map Product entity to ProductDetailsResponseDTO", exception.getMessage());

        verify(companyService, times(1)).getCompanySetter(productRequestDTO.getCompanyName());
        verify(categoryService, times(1)).getCategorySetter(productRequestDTO.getCategoryName());
        verify(fileHelper, times(1)).uploadFileToFileSystem(productRequestDTO.getImage());
        verify(mapper, times(1)).productRequestDTOTOProduct(productRequestDTO);
        verify(productRepository, times(1)).save(product);
        verify(mapper, times(1)).ProductTOproductDetailsResponseDTO(product);

    }

    /**
     * Tests the successful deletion of a product by ID. Verifies that the image
     * is deleted, the product is removed from the repository, and the
     * appropriate response is returned.
     *
     * @throws IOException if an I/O error occurs during file operations
     */
    @Test
    void shouldDeleteProductByIdSuccessfully() throws IOException {

        when(productRepository.findProductSetterDTOById(productId)).thenReturn(Optional.of(productSetterDTO));
        when(mapper.productSetterDTOTOProduct(productSetterDTO)).thenReturn(product);

        ResponseEntity<ApiResponseDTO> response = productService.deleteProductById(productId);

        assertNotNull(response);
        assertEquals("Success Deleted Product.", response.getBody().getMessage());
        assertEquals(200, response.getStatusCode().value());

        verify(fileHelper, times(1)).deleteImageFromFileSystem(product.getImageUrl());
        verify(productRepository, times(1)).delete(product);
    }

    /**
     * Tests the scenario where a product is not found by ID. Verifies that a
     * NotFoundException is thrown with the appropriate message.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldThrowNotFoundExceptionWhenProductByIdNotFound() throws IOException {

        when(productRepository.findProductSetterDTOById(productId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.deleteProductById(productId);
        });

        assertEquals("PRODUCT Not Found!", exception.getMessage());

        verify(fileHelper, never()).deleteImageFromFileSystem(anyString());
        verify(productRepository, never()).delete(any(Product.class));
    }

    /**
     * Tests the scenario where an IOException is thrown while deleting the
     * product image. Verifies that the exception is propagated.
     *
     * @throws IOException if an I/O error occurs during file operations
     */
    @Test
    void shouldThrowIOExceptionWhenDeletingProductImageFails() throws IOException {

        when(productRepository.findProductSetterDTOById(productId)).thenReturn(Optional.of(productSetterDTO));
        when(mapper.productSetterDTOTOProduct(productSetterDTO)).thenReturn(product);
        doThrow(IOException.class).when(fileHelper).deleteImageFromFileSystem(product.getImageUrl());

        assertThrows(IOException.class, () -> {
            productService.deleteProductById(productId);
        });

        verify(fileHelper, times(1)).deleteImageFromFileSystem(product.getImageUrl());
        verify(productRepository, never()).delete(product);

    }

    /**
     * Tests the scenario where a product is successfully updated by ID.
     * Verifies that the product is updated correctly and returns the expected
     * response.
     *
     * @throws IllegalStateException if the update fails
     * @throws IOException if file operations fail
     */
    @Test
    void shouldUpdateProductSuccessfully() throws IllegalStateException, IOException {

        productRequestDTO.setCategoryName("Updated Category");
        productRequestDTO.setCompanyName("Updated Company");
        productRequestDTO.setImage(multipartFile);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.getCategorySetter("Updated Category")).thenReturn(category);
        when(companyService.getCompanySetter("Updated Company")).thenReturn(company);
        when(fileHelper.deleteImageFromFileSystem(imageUrl)).thenReturn(true);
        when(fileHelper.uploadFileToFileSystem(productRequestDTO.getImage())).thenReturn(imageUrl);
        doNothing().when(mapper).updateProductFromRequestDTO(productRequestDTO, product);
        when(productRepository.save(product)).thenReturn(product);

        ProductDetailsResponseDTO expectedResponse = new ProductDetailsResponseDTO();
        when(mapper.ProductTOproductDetailsResponseDTO(product)).thenReturn(expectedResponse);

        ProductDetailsResponseDTO actualResponse = productService.updateProductById(1L, productRequestDTO);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(categoryService, times(1)).getCategorySetter("Updated Category");
        verify(companyService, times(1)).getCompanySetter("Updated Company");
        verify(fileHelper, times(1)).uploadFileToFileSystem(productRequestDTO.getImage());
        verify(fileHelper, times(1)).deleteImageFromFileSystem(imageUrl);
        verify(mapper, times(1)).updateProductFromRequestDTO(productRequestDTO, product);
        verify(productRepository, times(1)).save(product);
        verify(mapper, times(1)).ProductTOproductDetailsResponseDTO(product);

    }

    /**
     * Tests the scenario where the product is not found by ID. Verifies that a
     * NotFoundException is thrown with the appropriate message.
     *
     * @throws IllegalStateException if the update fails
     * @throws IOException if file operations fail
     */
    @Test
    void shouldThrowNotFoundExceptionWhenUpdateProductById() throws IllegalStateException, IOException {

        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setCategoryName("Nonexistent Category");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.updateProductById(1L, productRequestDTO);
        });

        assertEquals("PRODUCT Not Found!", exception.getMessage());

        verify(categoryService, never()).getCategorySetter(anyString());
        verify(companyService, never()).getCompanySetter(anyString());
        verify(fileHelper, never()).uploadFileToFileSystem(any());
        verify(fileHelper, never()).deleteImageFromFileSystem(anyString());
        verify(mapper, never()).updateProductFromRequestDTO(any(), any());
        verify(productRepository, never()).save(any());
        verify(mapper, never()).ProductTOproductDetailsResponseDTO(any());

    }

    /**
     * Tests the scenario where the product's category is updated. Verifies that
     * the category is updated correctly.
     *
     * @throws IllegalStateException if the update fails
     * @throws IOException if file operations fail
     */
    @Test
    void shouldUpdateCategoryWhenCategoryNameIsProvided() throws IllegalStateException, IOException {

        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("New Category");

        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setCategoryName("New Category");

        Product product = new Product();
        product.setId(1L);
        product.setCategory(new Category());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.getCategorySetter("New Category")).thenReturn(newCategory);

        productService.updateProductById(1L, productRequestDTO);

        assertEquals(newCategory.getId(), product.getCategory().getId());
        assertEquals(newCategory.getName(), product.getCategory().getName());

        verify(categoryService, times(1)).getCategorySetter("New Category");
        verify(productRepository, times(1)).save(product);

    }

    /**
     * Tests the scenario where the product's company is updated. Verifies that
     * the company is updated correctly.
     *
     * @throws IllegalStateException if the update fails
     * @throws IOException if file operations fail
     */
    @Test
    void shouldUpdateCompanyWhenCompanyNameIsProvided() throws IllegalStateException, IOException {

        Company newCompany = new Company();
        company.setId(2L);
        company.setName("New Category");

        productRequestDTO.setCompanyName("New Company");

        product.setCompany(newCompany);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(companyService.getCompanySetter("New Company")).thenReturn(newCompany);

        productService.updateProductById(1L, productRequestDTO);

        assertEquals(newCompany.getId(), product.getCompany().getId());
        assertEquals(newCompany.getName(), product.getCompany().getName());

        verify(companyService, times(1)).getCompanySetter("New Company");
        verify(productRepository, times(1)).save(product);

    }

    /**
     * Tests the scenario where the product's image is updated. Verifies that
     * the old image is deleted and the new image is set correctly.
     *
     * @throws IllegalStateException if the update fails
     * @throws IOException if file operations fail
     */
    @Test
    void shouldUpdateImageWhenImageIsProvided() throws IllegalStateException, IOException {

        productRequestDTO.setImage(multipartFile);

        product.setImageUrl("old-image-url-" + imageUrl);

        String newImageUrl = "new-image-url-" + imageUrl;

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(fileHelper.deleteImageFromFileSystem("old-image-url-" + imageUrl)).thenReturn(true);
        when(fileHelper.uploadFileToFileSystem(multipartFile)).thenReturn(newImageUrl);

        productService.updateProductById(1L, productRequestDTO);

        assertEquals(newImageUrl, product.getImageUrl());

        verify(fileHelper, times(1)).deleteImageFromFileSystem("old-image-url-" + imageUrl);
        verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);
        verify(productRepository, times(1)).save(product);

    }

    /**
     * Tests the scenario where a product exists. Verifies that the
     * productService returns true when the product exists.
     *
     * @return void
     */
    @Test
    void shouldReturnTrueWhenProductExists() {

        when(productRepository.existsById(productId)).thenReturn(true);

        Boolean result = productService.existProductById(productId);

        assertTrue(result);
        verify(productRepository, times(1)).existsById(productId);

    }

    /**
     * Tests the scenario where a product does not exist. Verifies that a
     * NotFoundException is thrown with the appropriate message.
     *
     * @throws NotFoundException if the product does not exist
     */
    @Test
    void shouldThrowNotFoundExceptionWhenProductDoesNotExist() {

        when(productRepository.existsById(productId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.existProductById(productId);
        });

        assertEquals("PRODUCT Not Found!", exception.getMessage());
        verify(productRepository, times(1)).existsById(productId);

    }

    /**
     * Test case to verify that a page of products is returned when a company
     * exists.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void shouldReturnPageOfProductsWhenCompanyExists() {

        String companyName = "Acme Corp";

        pageable = PageRequest.of(0, 10);

        ProductCompanyResponseDTO productDTO = new ProductCompanyResponseDTO();
        List<ProductCompanyResponseDTO> productList = List.of(productDTO);

        Page<ProductCompanyResponseDTO> productPage = new PageImpl<>(productList);

        when(productRepository.findAllProducts(companyName, pageable)).thenReturn(productPage);

        Page<ProductCompanyResponseDTO> result = productService.getAllProductsInCompany(companyName, 0, 10);

        assertEquals(productPage, result);
        verify(productRepository, times(1)).findAllProducts(companyName, pageable);

    }

    /**
     * Test case to verify that an empty page of products is returned when there
     * are no products associated with a given company.
     *
     * @return void
     */
    @Test
    void shouldReturnEmptyPageWhenNoProductsExistForCompany() {

        String companyName = "Pepsi";

        pageable = PageRequest.of(0, 10);

        Page<ProductCompanyResponseDTO> emptyPage = new PageImpl<>(List.of());

        when(productRepository.findAllProducts(companyName, pageable)).thenReturn(emptyPage);

        Page<ProductCompanyResponseDTO> result = productService.getAllProductsInCompany(companyName, 0, 10);

        assertEquals(emptyPage, result);
        verify(productRepository, times(1)).findAllProducts(companyName, pageable);

    }

    /**
     * Test case to verify that an IllegalArgumentException is thrown when the
     * page or size parameter is negative.
     *
     * @throws IllegalArgumentException if the page or size parameter is
     * negative
     */
    @Test
    void shouldThrowIllegalArgumentExceptionWhenPageOrSizeIsNegativeWhenGettingCompanyProducts() {

        String companyName = "Pepsi";

        IllegalArgumentException pageException = assertThrows(IllegalArgumentException.class, () -> {
            productService.getAllProductsInCompany(companyName, -1, 10);
        });
        assertEquals("Page index and page size must be non-negative integers.", pageException.getMessage());

        IllegalArgumentException sizeException = assertThrows(IllegalArgumentException.class, () -> {
            productService.getAllProductsInCompany(companyName, 0, -1);
        });

        assertEquals("Page index and page size must be non-negative integers.", sizeException.getMessage());

    }

    /**
     * Test case to verify that the method
     * shouldReturnProductsDetailsWhenPaginationParametersAreValid returns the
     * expected page of product details for a given company.
     *
     * @param companyName the name of the company
     * @return void
     */
    @Test
    void shouldReturnProductsDetailsWhenPaginationParametersAreValid() {

        String companyName = "Pepsi";

        pageable = PageRequest.of(0, 10);

        Page<ProductDetailsCompanyResponseDTO> expectedPage = new PageImpl<>(List.of(new ProductDetailsCompanyResponseDTO()));

        when(productRepository.findProductsByCompanyName(companyName, pageable)).thenReturn(expectedPage);

        Page<ProductDetailsCompanyResponseDTO> result = productService.getAllProductsDetailsInCompany(companyName, 0, 10);

        assertEquals(expectedPage, result);
        verify(productRepository, times(1)).findProductsByCompanyName(companyName, pageable);

    }

    /**
     * Test case to verify that an IllegalArgumentException is thrown when the
     * page or size parameter is negative.
     *
     * @throws IllegalArgumentException if the page or size parameter is
     * negative
     */
    @Test
    void shouldThrowIllegalArgumentExceptionWhenPageOrSizeIsNegative() {

        String companyName = "Pepsi";

        IllegalArgumentException pageException = assertThrows(IllegalArgumentException.class, () -> {
            productService.getAllProductsDetailsInCompany(companyName, -1, 10);
        });

        assertEquals("Page index and page size must be non-negative integers.", pageException.getMessage());

        IllegalArgumentException sizeException = assertThrows(IllegalArgumentException.class, () -> {
            productService.getAllProductsDetailsInCompany(companyName, 0, -1);
        });

        assertEquals("Page index and page size must be non-negative integers.", sizeException.getMessage());

    }

    /**
     * Test case to verify that the method
     * shouldReturnProductsDetailsWhenPaginationParametersAreValidForCategory
     * returns the expected page of product details for a given category.
     *
     * @param categoryName the name of the category
     * @return void
     */
    @Test
    void shouldReturnProductsDetailsWhenPaginationParametersAreValidForCategory() {

        String categoryName = "Electronics";

        pageable = PageRequest.of(0, 10);

        Page<ProductDetailsCategoryResponseDTO> expectedPage = new PageImpl<>(List.of(new ProductDetailsCategoryResponseDTO()));

        when(productRepository.findProductsByCategoryName(categoryName, pageable)).thenReturn(expectedPage);

        Page<ProductDetailsCategoryResponseDTO> result = productService.getAllProductsDetailsInCategory(categoryName, 0, 10);

        assertEquals(expectedPage, result);
        verify(productRepository, times(1)).findProductsByCategoryName(categoryName, pageable);

    }

    /**
     * Test case to verify that an IllegalArgumentException is thrown when the
     * page or size parameter is negative for a category.
     *
     * @throws IllegalArgumentException if the page or size parameter is
     * negative
     */
    @Test
    void shouldThrowIllegalArgumentExceptionWhenPageOrSizeIsNegativeForCategory() {

        String categoryName = "Electronics";

        IllegalArgumentException pageException = assertThrows(IllegalArgumentException.class, () -> {
            productService.getAllProductsDetailsInCategory(categoryName, -1, 10);
        });

        assertEquals("Page index and page size must be non-negative integers.", pageException.getMessage());

        IllegalArgumentException sizeException = assertThrows(IllegalArgumentException.class, () -> {
            productService.getAllProductsDetailsInCategory(categoryName, 0, -1);
        });

        assertEquals("Page index and page size must be non-negative integers.", sizeException.getMessage());

    }

    /**
     * Test case to verify that the method
     * shouldReturnTopSevenBestSellingProducts returns the expected list of best
     * selling products.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    void shouldReturnTopSevenBestSellingProducts() {

        pageable = PageRequest.of(0, 7);

        List<ProductBestSellerResponseDTO> expectedBestSellers = List.of(
                new ProductBestSellerResponseDTO(),
                new ProductBestSellerResponseDTO(),
                new ProductBestSellerResponseDTO()
        );

        when(productRepository.findTopBestSellers(pageable)).thenReturn(expectedBestSellers);

        List<ProductBestSellerResponseDTO> result = productService.getTopSevenProductsWithBestSeller();

        assertEquals(expectedBestSellers, result);
        verify(productRepository, times(1)).findTopBestSellers(pageable);

    }

    /**
     * Tests the scenario where product details exist. Verifies that the
     * getProductDetailsById method returns the expected product details and
     * verifies that the productRepository's findProductDetailsById method is
     * called once.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void shouldReturnProductDetailsWhenProductExists() {

        ProductDetailsResponseDTO expectedProductDetails = new ProductDetailsResponseDTO();

        when(productRepository.findProductDetailsById(productId)).thenReturn(Optional.of(expectedProductDetails));

        ProductDetailsResponseDTO result = productService.getProductDetailsById(productId);

        assertEquals(expectedProductDetails, result);
        verify(productRepository, times(1)).findProductDetailsById(productId);

    }

    /**
     * Tests the scenario where product details do not exist. Verifies that a
     * NotFoundException is thrown with the appropriate message.
     *
     * @throws NotFoundException if the product details do not exist
     */
    @Test
    void shouldThrowNotFoundExceptionWhenProductDetailsDoesNotExist() {

        when(productRepository.findProductDetailsById(productId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.getProductDetailsById(productId);
        });

        assertEquals("PRODUCT Not Found!", exception.getMessage());
        verify(productRepository, times(1)).findProductDetailsById(productId);

    }

}
