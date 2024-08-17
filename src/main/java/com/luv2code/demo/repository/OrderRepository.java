package com.luv2code.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.entity.Order;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Override
	@EntityGraph(attributePaths = { "orderItems" })
	Optional<Order> findById(@Param("id") Long id);

	@Modifying
    @Transactional
    @Query("DELETE FROM Order o WHERE o.id = :orderId")
	void deleteOrderById(@Param("orderId") Long orderId);	
	

}
