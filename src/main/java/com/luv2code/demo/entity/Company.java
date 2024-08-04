package com.luv2code.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "companies")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@DynamicInsert
@DynamicUpdate
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank
    private String name;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "imageUrl", length = 1000, nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Product> products;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
