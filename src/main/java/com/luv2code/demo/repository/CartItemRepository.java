package com.luv2code.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.entity.CartItem;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	@Modifying
	@Transactional
	@Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.cart.id = :cartId")
	Integer updateCartItemQuantity(Integer quantity, Long cartId);
	
}
