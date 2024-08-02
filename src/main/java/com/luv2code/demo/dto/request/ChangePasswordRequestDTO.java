package com.luv2code.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequestDTO {

	private String email;

	private String oldPassword;

	private String newPassword;

	private String newRepeatedPassword;

}
