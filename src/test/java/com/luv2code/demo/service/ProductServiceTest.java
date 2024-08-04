package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.ProductRequestDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;
import com.luv2code.demo.entity.Category;
import com.luv2code.demo.entity.Company;
import com.luv2code.demo.entity.Product;
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
    
    private Company company;
    
    private Category category;
    
    private Product product;
    
    private String imageUrl;

    /**
     * Initializes the mock objects and sets up the test data before each test case.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        imageUrl = "http://example.com/image.png";
        
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
        product.setId(1L);
        product.setImageUrl(imageUrl);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setQuantity(10);
        product.setCategory(category);
        product.setCompany(company);
    }

    /**
     * Tests the successful creation of a product.
     * Verifies that the correct methods are called and the expected response is returned.
     * 
     * @throws IllegalStateException if any illegal state occurs during execution
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
     * Tests the scenario where an IOException is thrown during the creation of a product.
     * Verifies that the exception is properly handled and thrown.
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
     * Tests the scenario where a RuntimeException is thrown when the mapper fails to map
     * ProductRequestDTO to Product during the creation of a product.
     * Verifies that the exception is properly handled and thrown.
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
     * Tests the scenario where a RuntimeException is thrown when the mapper fails to map
     * Product to ProductDetailsResponseDTO during the creation of a product.
     * Verifies that the exception is properly handled and thrown.
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
    
}

