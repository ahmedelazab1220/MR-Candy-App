package com.luv2code.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.dto.ProductSetterDTO;
import com.luv2code.demo.dto.response.ProductBestSellerResponseDTO;
import com.luv2code.demo.dto.response.ProductCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;
import com.luv2code.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT new com.luv2code.demo.dto.response.ProductCompanyResponseDTO(p.id, p.name, p.imageUrl) "
            + "FROM Product p " + "WHERE p.company.name = :companyName")
    Page<ProductCompanyResponseDTO> findAllProducts(@Param("companyName") String companyName, Pageable pageable);

    @Query("SELECT new com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO("
            + "p.id, p.description, p.discount, p.imageUrl, p.price, p.quantity) " + "FROM Product p "
            + "WHERE p.company.name = :companyName")
    Page<ProductDetailsCompanyResponseDTO> findProductsByCompanyName(@Param("companyName") String companyName,
            Pageable pageable);

    @Query("SELECT new com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO("
            + "p.id, p.description, p.discount, p.imageUrl, p.price, p.company.name) " + "FROM Product p "
            + "WHERE p.category.name = :categoryName")
    Page<ProductDetailsCategoryResponseDTO> findProductsByCategoryName(@Param("categoryName") String categoryName,
            Pageable pageable);

    @Query("SELECT new com.luv2code.demo.dto.response.ProductBestSellerResponseDTO(p.id, p.name, p.description, p.discount, p.imageUrl) "
            + "FROM Product p " + "ORDER BY p.salesCount DESC")
    List<ProductBestSellerResponseDTO> findTopBestSellers(Pageable pageable);

    @Query("SELECT new com.luv2code.demo.dto.response.ProductDetailsResponseDTO("
            + "p.id, p.type, p.imageUrl, p.description, p.size, p.price, p.quantity, "
            + "p.category.name, p.company.name, p.company.imageUrl) " + "FROM Product p " + "JOIN p.company c "
            + "WHERE p.id = :productId")
    Optional<ProductDetailsResponseDTO> findProductDetailsById(@Param("productId") Long productId);

    @Query("SELECT new com.luv2code.demo.dto.ProductSetterDTO(p.id, p.name, p.imageUrl) " + "FROM Product p " + "WHERE p.id = :id")
    Optional<ProductSetterDTO> findProductSetterDTOById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"category", "company"})
    @Override
    Optional<Product> findById(@Param("id") Long id);

}
