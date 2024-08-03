package com.luv2code.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.dto.response.ProductBestSellerResponseDTO;
import com.luv2code.demo.dto.response.ProductCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;
import com.luv2code.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT new com.luv2code.demo.dto.response.ProductCompanyResponseDTO(p.id, p.name, p.imageUrl) "
			+ "FROM Product p")
	List<ProductCompanyResponseDTO> findAllProducts();

	@Query("SELECT new com.luv2code.demo.dto.response.ProductDetailsCompanyResponseDTO(p.id, p.description, p.discount, p.imageUrl, p.price , p.quantity) "
			+ "FROM Product p JOIN p.company c WHERE c.name = :companyName")
	List<ProductDetailsCompanyResponseDTO> findProductsByCompanyName(@Param("companyName") String companyName);

	@Query("SELECT new com.luv2code.demo.dto.response.ProductDetailsCategoryResponseDTO(p.id, p.description, p.discount, p.imageUrl, p.price, c.name) "
			+ "FROM Product p JOIN p.company c JOIN p.category ca WHERE ca.name = :categoryName")
	List<ProductDetailsCategoryResponseDTO> findProductsByCategoryName(@Param("categoryName") String categoryName);

	@Query("SELECT new com.luv2code.demo.dto.response.ProductBestSellerResponseDTO(p.id, p.name, p.description, p.discount, p.imageUrl) "
			+ "FROM Product p " + "ORDER BY p.salesCount DESC")
	List<ProductBestSellerResponseDTO> findTopBestSellers(Pageable pageable);

	@Query("SELECT new com.luv2code.demo.dto.response.ProductDetailsResponseDTO("
			+ "p.id, p.type, p.imageUrl, p.description, p.size, p.price, p.quantity, "
			+ "c.name, co.name, co.imageUrl) " + "FROM Product p " + "JOIN p.category c " + "JOIN p.company co "
			+ "WHERE p.id = :productId")
	ProductDetailsResponseDTO findProductDetailsById(Long productId);

}
