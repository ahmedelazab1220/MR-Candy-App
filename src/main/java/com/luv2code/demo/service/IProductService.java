package com.luv2code.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.ProductRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.ProductBestSellerResponseDTO;
import com.luv2code.demo.dto.response.ProductCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;

public interface IProductService {

    ProductDetailsResponseDTO createProduct(ProductRequestDTO productRequestDTO) throws IllegalStateException, IOException;

    ResponseEntity<ApiResponseDTO> deleteProductById(Long theId) throws IOException;

    ProductDetailsResponseDTO updateProductById(Long theId, ProductRequestDTO productRequestDTO)
            throws IllegalStateException, IOException;

    Boolean existProductById(Long theId);

    Page<ProductCompanyResponseDTO> getAllProductsInCompany(String companyName, Integer page, Integer size);

    Page<ProductDetailsCompanyResponseDTO> getAllProductsDetailsInCompany(String companyName, Integer page, Integer size);

    Page<ProductDetailsCategoryResponseDTO> getAllProductsDetailsInCategory(String categoryName, Integer page, Integer size);

    List<ProductBestSellerResponseDTO> getTopSevenProductsWithBestSeller();

    ProductDetailsResponseDTO getProductDetailsById(Long theId);

}
