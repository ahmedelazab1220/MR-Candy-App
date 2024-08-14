package com.luv2code.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.ProductRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.DiscountedProductsResponse;
import com.luv2code.demo.dto.response.ProductBestSellerResponseDTO;
import com.luv2code.demo.dto.response.ProductCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;
import com.luv2code.demo.service.IProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/products")
@AllArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping("/bestSeller")
    public List<ProductBestSellerResponseDTO> getAllTopBestSellerProducts() {

        return productService.getTopSevenProductsWithBestSeller();

    }
    
    @GetMapping("/discount")
    public List<DiscountedProductsResponse> getAllProductsWithDiscount() {

        return productService.getAllProductsWithDiscount();

    }

    @GetMapping("/company")
    public Page<ProductCompanyResponseDTO> getProductsInCompany(@RequestParam(required = true) String companyName,
            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {

        return productService.getAllProductsInCompany(companyName, page, size);

    }

    @GetMapping("/details/category")
    public Page<ProductDetailsCategoryResponseDTO> getProductsInCategory(
            @RequestParam(required = true) String categoryName, @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        return productService.getAllProductsDetailsInCategory(categoryName, page, size);

    }

    @GetMapping("/details/company")
    public Page<ProductDetailsCompanyResponseDTO> getProductsDetailsInCompany(
            @RequestParam(required = true) String companyName, @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        return productService.getAllProductsDetailsInCompany(companyName, page, size);

    }

    @GetMapping("/{theId}")
    public ProductDetailsResponseDTO getProductDetails(@PathVariable(name = "theId", required = true) Long theId) {

        return productService.getProductDetailsById(theId);

    }

    @DeleteMapping("/{theId}")
    public ResponseEntity<ApiResponseDTO> deleteProduct(@PathVariable(name = "theId", required = true) Long theId) throws IOException {

        return productService.deleteProductById(theId);

    }

    @PostMapping("")
    public ProductDetailsResponseDTO createProduct(@Valid @ModelAttribute ProductRequestDTO productRequestDTO) throws IllegalStateException, IOException {

        return productService.createProduct(productRequestDTO);

    }

    @PutMapping("/{theId}")
    public ProductDetailsResponseDTO updateProduct(@PathVariable(name = "theId", required = true) Long theId, @Valid @ModelAttribute ProductRequestDTO productRequestDTO) throws IllegalStateException, IOException {

        return productService.updateProductById(theId, productRequestDTO);

    }

}
