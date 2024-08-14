package com.luv2code.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
