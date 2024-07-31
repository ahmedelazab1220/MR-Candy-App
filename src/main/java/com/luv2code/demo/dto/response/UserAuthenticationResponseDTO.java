package com.luv2code.demo.dto.response;

import com.luv2code.demo.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationResponseDTO {

	private Long id;

	private String email;

	private String password;

	private String phoneNumber;

	private Role role;

}
