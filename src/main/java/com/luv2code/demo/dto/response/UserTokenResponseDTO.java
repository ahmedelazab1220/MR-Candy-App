package com.luv2code.demo.dto.response;

import com.luv2code.demo.entity.Address;
import com.luv2code.demo.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTokenResponseDTO {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String imageUrl;

    private Address address;

    private Role role;

}
