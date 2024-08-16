package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.ProductRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.DiscountedProductsResponse;
import com.luv2code.demo.dto.response.ProductBestSellerResponseDTO;
import com.luv2code.demo.dto.response.ProductCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;
import com.luv2code.demo.entity.Category;
import com.luv2code.demo.entity.Company;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.helper.IPaginationHelper;
import com.luv2code.demo.repository.ProductRepository;
import com.luv2code.demo.service.ICategoryService;
import com.luv2code.demo.service.ICompanyService;
import com.luv2code.demo.service.IProductService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final SystemMapper mapper;
    private final IFileHelper fileHelper;
    private final IPaginationHelper paginationHelper;
    private final ICategoryService categoryService;
    private final ICompanyService companyService;

    @Transactional
    @Override
    public ProductDetailsResponseDTO createProduct(ProductRequestDTO productRequestDTO)
            throws IllegalStateException, IOException {

        log.info("Starting product creation process for product: {}", productRequestDTO.getName());

        Company company = companyService.getCompanySetter(productRequestDTO.getCompanyName());
        log.debug("Company retrieved: {}", company.getName());

        Category category = categoryService.getCategorySetter(productRequestDTO.getCategoryName());
        log.debug("Category retrieved: {}", category.getName());

        String imageUrl = fileHelper.uploadFileToFileSystem(productRequestDTO.getImage());
        log.debug("Image uploaded with URL: {}", imageUrl);

        Product product = mapper.productRequestDTOTOProduct(productRequestDTO);

        product.setImageUrl(imageUrl);

        product.setCategory(category);
        product.setCompany(company);

        log.info("Product created successfully with name: {}", product.getName());

        return mapper.ProductTOproductDetailsResponseDTO(productRepository.save(product));

    }

    @Override
    public ResponseEntity<ApiResponseDTO> deleteProductById(Long theId) throws IOException {

        log.info("Starting product deletion process for product ID: {}", theId);

        Optional<Product> product = productRepository.findProductSetterDTOById(theId)
                .map(mapper::productSetterDTOTOProduct);

        if (product.isEmpty()) {
            log.warn("Product with ID {} not found", theId);
            throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
        }

        fileHelper.deleteImageFromFileSystem(product.get().getImageUrl());
        log.debug("Image deleted for product ID: {}", theId);

        productRepository.delete(product.get());
        log.info("Product with ID {} deleted successfully", theId);

        return ResponseEntity.ok(new ApiResponseDTO("Success Deleted Product."));

    }

    @Transactional
    @Override
    public ProductDetailsResponseDTO updateProductById(Long theId, ProductRequestDTO productRequestDTO)
            throws IllegalStateException, IOException {

        log.info("Starting product update process for product ID: {}", theId);

        Optional<Product> product = productRepository.findById(theId);

        if (product.isEmpty()) {
            log.warn("Product with ID {} not found", theId);
            throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
        }

        if (productRequestDTO.getCategoryName() != null) {

            Category category = categoryService.getCategorySetter(productRequestDTO.getCategoryName());

            if (product.get().getCategory() != null & category != product.get().getCategory()) {

                product.get().setCategory(category);
                log.debug("Updated category for product ID: {}", theId);

            }

        }

        if (productRequestDTO.getCompanyName() != null) {

            Company company = companyService.getCompanySetter(productRequestDTO.getCompanyName());

            product.get().setCompany(company);
            log.debug("Updated company for product ID: {}", theId);

        }

        if (productRequestDTO.getImage() != null) {

            fileHelper.deleteImageFromFileSystem(product.get().getImageUrl());

            String imageUrl = fileHelper.uploadFileToFileSystem(productRequestDTO.getImage());

            product.get().setImageUrl(imageUrl);
            log.debug("Updated image for product ID: {}", theId);

        }

        mapper.updateProductFromRequestDTO(productRequestDTO, product.get());

        log.info("Product with ID {} updated successfully", product.get().getId());

        return mapper.ProductTOproductDetailsResponseDTO(productRepository.save(product.get()));

    }

    @Override
    public Page<ProductCompanyResponseDTO> getAllProductsInCompany(String companyName, Integer page, Integer size) {

        log.info("Fetching all products in company: {}", companyName);

        paginationHelper.validatePageParameters(page, size);

        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findAllProducts(companyName, pageable);

    }

    @Override
    public Page<ProductDetailsCompanyResponseDTO> getAllProductsDetailsInCompany(String companyName, Integer page, Integer size) {

        log.info("Fetching all product details in company: {}", companyName);
        paginationHelper.validatePageParameters(page, size);

        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findProductsByCompanyName(companyName, pageable);

    }

    @Override
    public Page<ProductDetailsCategoryResponseDTO> getAllProductsDetailsInCategory(String categoryName, Integer page, Integer size) {

        log.info("Fetching all product details in category: {}", categoryName);
        paginationHelper.validatePageParameters(page, size);

        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findProductsByCategoryName(categoryName, pageable);

    }

    @Override
    public List<ProductBestSellerResponseDTO> getTopSevenProductsWithBestSeller() {

        log.info("Fetching top seven best-selling products");
        Pageable pageable = PageRequest.of(0, 7);

        return productRepository.findTopBestSellers(pageable);

    }

    @Override
    public ProductDetailsResponseDTO getProductDetailsById(Long theId) {

        log.info("Fetching product details for product ID: {}", theId);

        Optional<ProductDetailsResponseDTO> productDto = productRepository.findProductDetailsById(theId);

        if (productDto.isEmpty()) {
            log.warn("Product with ID {} not found", theId);
            throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
        }

        return productDto.get();
    }

    @Override
    public Product getProductCartSetter(Long theId) {

        log.info("Fetching product for cart with product ID: {}", theId);

        Optional<Product> product = productRepository.findProductCartSetterDTOById(theId).map(mapper::productCartSetterDTOTOProduct);;

        if (product.isEmpty()) {
            log.warn("Product with ID {} not found", theId);
            throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
        }

        return product.get();

    }

    @Override
    public Integer updateProductQuantityById(Long theId, Integer quantity) {

        log.info("Starting update process for product ID: {} with new quantity: {}", theId, quantity);

        Integer updateProduct = productRepository.updateProductQuantity(theId, quantity);

        if (updateProduct == 0) {
            log.error("Failed to update product with ID: {}", theId);
            throw new RuntimeException("Update Product With ID: {}" + theId);
        }

        log.info("Successfully updated product with ID: {} to new quantity: {}", theId, quantity);

        return updateProduct;
    }

	@Override
	public List<DiscountedProductsResponse> getAllProductsWithDiscount() {
		
		log.info("Fetching All Products With Discount.");
		
		return productRepository.findAllProductsWithDiscount();
	}

}
