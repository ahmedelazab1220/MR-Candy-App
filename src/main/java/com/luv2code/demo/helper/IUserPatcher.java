package com.luv2code.demo.helper;

import java.util.Map;

import com.luv2code.demo.entity.User;

public interface IUserPatcher {

	void userPatcher(User user , Map<String, String> userUpdates) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException;
	
}
