package com.luv2code.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.entity.OrderItem;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	@Modifying
	@Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteOrderItemsByOrderId(@Param("orderId") Long orderId);
		
	
}
