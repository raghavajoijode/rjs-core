package com.subra.aem.rjs.account.services;

import org.subra.commons.dtos.account.User;

public interface UserService {

	boolean isExistingUser(String email);
	
	String createUser(String email, String password, String name);
	
	String authenticateUser(String email, String password);

	User getUser(String userId);

}
