package com.luv2code.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.dto.ProductGetterDTO;
import com.luv2code.demo.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	@Override
	@EntityGraph(attributePaths = {"cartItem"})
	Optional<Cart> findById(Long id);
	
	@Modifying
    @Transactional
    @Query(value = "UPDATE products p " +
            "JOIN cart_items ci ON p.id = ci.product_id " +
            "SET p.quantity = COALESCE(p.quantity, 0) + :quantity " +
            "WHERE ci.cart_id = :cartId", nativeQuery = true)
    Integer updateProductQuantity(@Param("cartId") Long cartId , @Param("quantity") Integer quantity);
	
	
	@Query(value = "SELECT new com.luv2code.demo.dto.ProductGetterDTO(p.id, p.name, p.quantity, ci.quantity) " +
            "FROM Cart c " +
            "JOIN c.cartItem ci " +
            "JOIN ci.product p " +
            "WHERE c.id = :cartId")
    Optional<ProductGetterDTO> findProductGetterDTO(@Param("cartId") Long cartId);
		
	
	
}
