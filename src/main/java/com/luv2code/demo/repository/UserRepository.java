package com.luv2code.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.dto.response.UserAuthenticationResponseDTO;
import com.luv2code.demo.dto.response.UserTokenResponseDTO;
import com.luv2code.demo.dto.setter.UserSetterDTO;
import com.luv2code.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.luv2code.demo.dto.response.UserAuthenticationResponseDTO("
            + "u.id, u.email, u.password, u.phoneNumber, u.role) " + "FROM User u " + "LEFT JOIN u.role r "
            + "WHERE u.email = :email")
    Optional<UserAuthenticationResponseDTO> findUserAuthenticationDetailsByEmail(@Param("email") String email);

    @Query("SELECT new com.luv2code.demo.dto.response.UserTokenResponseDTO("
            + "u.id, u.fullName, u.email, u.phoneNumber, u.imageUrl, " + "a, r) " + "FROM User u "
            + "LEFT JOIN u.address a " + "LEFT JOIN u.role r " + "WHERE u.email = :email")
    Optional<UserTokenResponseDTO> findUserTokenDetailsByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"role", "address"})
    Optional<User> findByEmail(@Param("email") String email);

    Boolean existsByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    Integer updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.imageUrl = :imageUrl WHERE u.email = :email")
    Integer updateImageByEmail(@Param("email") String email, @Param("imageUrl") String imageUrl);

    @Query("SELECT u.password FROM User u WHERE u.email = :email")
    Optional<String> findUserPasswordByEmail(@Param("email") String email);

    @Query("SELECT new com.luv2code.demo.dto.UserSetterDTO(u.id, u.email) FROM User u WHERE u.email = :email")
    Optional<UserSetterDTO> findUserSetterByEmail(@Param("email") String email);

}
