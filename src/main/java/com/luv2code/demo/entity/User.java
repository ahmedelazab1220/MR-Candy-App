package com.luv2code.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "full_name")
	@Size(min = 3, message = "Name lenght must greater than or equal 3")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only alphabetic characters")
	private String fullName;

	@Column(name = "email", nullable = false, unique = true)
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Invalid email address")
	private String email;

	@Column(name = "phone_number", nullable = false)
	@NotBlank
	@Pattern(regexp = "^01[0-2,5]{1}[0-9]{8}$", message = "Invalid phone number")
	private String phoneNumber;

	@Column(name = "password", nullable = false)
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed.")
	private String password;

	@Column(name = "createdAt", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "imageUrl", length = 1000, nullable = false)
	private String imageUrl;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<RefreshToken> refreshTokens;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

}
