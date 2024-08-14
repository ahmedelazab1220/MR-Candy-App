package com.luv2code.demo.helper.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.luv2code.demo.entity.User;
import com.luv2code.demo.helper.IUserPatcher;

@Component
public class UserPatcher implements IUserPatcher {

	@Override
	public void userPatcher(User user, Map<String, String> userUpdates)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Class<?> userClass = User.class;

		for (var entry : userUpdates.entrySet()) {

			String fieldName = entry.getKey();
			String newValue = entry.getValue();
			
			if(newValue.trim().isEmpty()) {
				throw new IllegalArgumentException("The value for " + fieldName + " cannot be empty or null");
			}

			if (updateAddressField(user, fieldName, newValue)) {
				continue;
			}

			updateUserField(user, userClass, fieldName, newValue);

		}

	}
	
	private boolean updateAddressField(User user, String fieldName, String newValue) {
		if (newValue == null) {
			return false;
		}

		switch (fieldName) {
			case "state":
				user.getAddress().setState(newValue);
				return true;
			case "city":
				user.getAddress().setCity(newValue);
				return true;
			case "street":
				user.getAddress().setStreet(newValue);
				return true;
			case "zipCode":
				user.getAddress().setZipCode(newValue);
				return true;
			default:
				return false;
		}
	}
	
	private void updateUserField(User user, Class<?> userClass, String fieldName, String newValue)
			throws NoSuchFieldException, IllegalAccessException {

		if (newValue == null) {
			return;
		}

		Field field = userClass.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(user, newValue);
		field.setAccessible(false);
	}

}
