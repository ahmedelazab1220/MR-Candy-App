package com.luv2code.demo.service;

import com.luv2code.demo.entity.User;

public interface IUserService {

	User getUserTokenDetails(String email);

	void createUser(User user);
	
	User getUserSetterByEmail(String email);
}
