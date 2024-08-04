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
import com.luv2code.demo.repository.ProductRepository;
import com.luv2code.demo.service.ICategoryService;
import com.luv2code.demo.service.ICompanyService;
import com.luv2code.demo.service.IProductService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

	private final ProductRepository productRepository;
	private final SystemMapper mapper;
	private final IFileHelper fileHelper;
	private final ICategoryService categoryService;
	private final ICompanyService companyService;

	@Transactional
	@Override
	public ProductDetailsResponseDTO createProduct(ProductRequestDTO productRequestDTO)
			throws IllegalStateException, IOException {

		Company company = companyService.getCompanySetter(productRequestDTO.getCompanyName());

		Category category = categoryService.getCategorySetter(productRequestDTO.getCategoryName());

		String imageUrl = fileHelper.uploadFileToFileSystem(productRequestDTO.getImage());

		Product product = mapper.productRequestDTOTOProduct(productRequestDTO);

		product.setImageUrl(imageUrl);

		product.setCategory(category);
		product.setCompany(company);

		return mapper.ProductTOproductDetailsResponseDTO(productRepository.save(product));
	}

	@Override
	public ResponseEntity<ApiResponseDTO> deleteProductById(Long theId) throws IOException {

		Optional<Product> product = productRepository.findProductSetterDTOById(theId)
		        .map(mapper::productSetterDTOTOProduct);

		if (product.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
		}

		fileHelper.deleteImageFromFileSystem(product.get().getImageUrl());

		productRepository.delete(product.get());

		return ResponseEntity.ok(new ApiResponseDTO("Success Deleted Product."));
	}

	@Transactional
	@Override
	public ProductDetailsResponseDTO updateProductById(Long theId, ProductRequestDTO productRequestDTO)
			throws IllegalStateException, IOException {

		Optional<Product> product = productRepository.findById(theId);

		if (product.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
		}

		if (productRequestDTO.getCategoryName() != null) {

			Category category = categoryService.getCategorySetter(productRequestDTO.getCategoryName());
			
			if(category != product.get().getCategory()) {
			  
				product.get().setCategory(category);
				
			}
			
		}
		
		if (productRequestDTO.getCompanyName() != null
				&& product.get().getCompany().getName() != productRequestDTO.getCompanyName()) {

			Company company = companyService.getCompanySetter(productRequestDTO.getCompanyName());
			
			product.get().setCompany(company);
			
		}
		
		if(productRequestDTO.getImage() != null) {
			
			fileHelper.deleteImageFromFileSystem(product.get().getImageUrl());

			String imageUrl = fileHelper.uploadFileToFileSystem(productRequestDTO.getImage());
			
			product.get().setImageUrl(imageUrl);
			
		}
		
		mapper.updateProductFromRequestDTO(productRequestDTO, product.get());
		
		return mapper.ProductTOproductDetailsResponseDTO(productRepository.save(product.get()));
	}

	@Override
	public Boolean existProductById(Long theId) {
	    
		Boolean productIsExist = productRepository.existsById(theId);
		
		if(!productIsExist) {
			throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
		}
		
		return productIsExist;
	}

	@Override
	public Page<ProductCompanyResponseDTO> getAllProductsInCompany(String companyName , Integer page , Integer size) {
		
		Pageable pageable = PageRequest.of(page, size);
				
		return productRepository.findAllProducts(companyName, pageable);
		
	}

	@Override
	public Page<ProductDetailsCompanyResponseDTO> getAllProductsDetailsInCompany(String companyName , Integer page , Integer size) {

		Pageable pageable = PageRequest.of(page, size);
		
		return productRepository.findProductsByCompanyName(companyName, pageable);
		
	}

	@Override
	public Page<ProductDetailsCategoryResponseDTO> getAllProductsDetailsInCategory(String categoryName , Integer page , Integer size) {

		Pageable pageable = PageRequest.of(page, size);
		
		return productRepository.findProductsByCategoryName(categoryName, pageable);
		
	}

	@Override
	public List<ProductBestSellerResponseDTO> getTopSevenProductsWithBestSeller() {

		Pageable pageable = PageRequest.of(0, 7);
		
		return productRepository.findTopBestSellers(pageable);
		
	}

	@Override
	public ProductDetailsResponseDTO getProductDetailsById(Long theId) {

        Optional<ProductDetailsResponseDTO> productDto = productRepository.findProductDetailsById(theId); 
		
        if(productDto.isEmpty()) {
        	throw new NotFoundException(NotFoundTypeException.PRODUCT + " Not Found!");
        }
        
		return productDto.get();
	}

}
