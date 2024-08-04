package com.luv2code.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.dto.CategorySetterDTO;
import com.luv2code.demo.dto.response.CategoryResponseDTO;
import com.luv2code.demo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("SELECT new com.luv2code.demo.dto.response.CategoryResponseDTO(c.name, c.imageUrl) " + "FROM Category c")
	List<CategoryResponseDTO> findAllCategories();
	
	@Query("SELECT new com.luv2code.demo.dto.CategorySetterDTO(c.id, c.name) " + "FROM Category c "
			+ "WHERE c.name = :name")
	Optional<CategorySetterDTO> findCategorySetterDTOByName(@Param("name") String name);

	Boolean existsByName(@Param("name") String name);

	void deleteByName(@Param("name") String name);

	Optional<Category> findByName(@Param("name") String name);

}
