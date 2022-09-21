package com.d202.assemble.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.d202.assemble.dto.User;
import com.d202.assemble.repo.UserRepo;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public boolean insertUser(User user) {
		User result = userRepo.save(user);
		return (result!=null);
	}

	//naver 회원가입
	@Override
	public Map<String, Object> getUserInfo(String token) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		HttpEntity<MultiValueMap<String, String>> header = new HttpEntity<>(headers);
		ResponseEntity<String> res = rt.exchange(
				"https://openapi.naver.com/v1/nid/me",
				HttpMethod.GET,
				header,
				String.class
				);
		System.out.println(res.getBody());
		
		return null;
	}

	@Override
	public Optional<User> getUser(String email) {
		return userRepo.findByEmail(email);
	}

	
	
	
}
