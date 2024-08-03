package com.luv2code.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "price", nullable = false , precision = 10 , scale = 2)
	private BigDecimal price;

	@Column(name = "quantity", nullable = false , precision = 10 , scale = 0)
	private Integer quantity;

	@Column(name = "size")
	private String size;

	@Column(name = "type")
	private String type;

	@Column(name = "discount")
	private String discount;

	@Column(name = "imageUrl", length = 1000, nullable = false)
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id")
	private Company company;
	
}
