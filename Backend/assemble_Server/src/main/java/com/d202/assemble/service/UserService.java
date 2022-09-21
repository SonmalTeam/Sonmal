package com.d202.assemble.service;

import java.util.Map;

import com.d202.assemble.dto.User;

public interface UserService {
	public boolean insertUser(User user);
	public Map<String, Object> getUserInfo(String token);
}
