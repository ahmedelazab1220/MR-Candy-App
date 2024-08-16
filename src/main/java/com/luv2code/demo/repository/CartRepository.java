package com.luv2code.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {	
		
	
}
