package com.luv2code.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	@Query("SELECT rt FROM RefreshToken rt "
		       + "JOIN FETCH rt.user u "
		       + "JOIN FETCH u.address "
		       + "JOIN FETCH u.role "
		       + "WHERE rt.token = :token")
	Optional<RefreshToken> findByToken(@Param("token") String token);

}
