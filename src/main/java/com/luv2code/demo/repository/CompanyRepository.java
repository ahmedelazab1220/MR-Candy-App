package com.luv2code.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.dto.response.CompanyResponseDTO;
import com.luv2code.demo.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

	@Query("SELECT new com.luv2code.demo.dto.response.CompanyResponseDTO(c.name, c.imageUrl) " + "FROM Company c")
	List<CompanyResponseDTO> findAllCompanies();
	
	Boolean existsByName(@Param("name") String name);

	void deleteByName(@Param("name") String name);

	Optional<Company> findByName(@Param("name") String name);
	
}
