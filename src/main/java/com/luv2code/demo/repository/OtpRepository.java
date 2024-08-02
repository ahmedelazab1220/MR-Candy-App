package com.luv2code.demo.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.entity.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

	@Query("SELECT o.expirationTime FROM Otp o JOIN o.user u WHERE o.otp = :otp AND u.email = :email")
	Optional<Instant> findExpirationTimeByOtpAndUserEmail(@Param("otp") String otp, @Param("email") String email);
	
}
