package com.luv2code.demo.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
import jakarta.validation.constraints.NotBlank;
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
@DynamicInsert
@DynamicUpdate
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	@NotBlank
	private String name;

	@Column(name = "description" , nullable = false)
	@NotBlank
	private String description;

	@Column(name = "price", nullable = false , precision = 10 , scale = 2)
	@NotBlank
	private BigDecimal price;

	@Column(name = "quantity", nullable = false)
	@NotBlank
	private Integer quantity;

	@Column(name = "size")
	private String size;

	@Column(name = "type")
	private String type;

	@Column(name = "discount")
	private String discount;
	
	@Column(name = "sales_count", nullable = false)
	@NotBlank
	private Long salesCount; 

	@Column(name = "imageUrl", length = 1000, nullable = false)
	@NotBlank
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_name" , nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.REMOVE)
	@JoinColumn(name = "company_name" , nullable = false)
	private Company company;
	
}
