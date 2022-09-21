package com.d202.assemble.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	@Autowired
	private UserRepo userRepo;
	
	@Transactional
	public boolean insertUser(User user) {
		User result = userRepo.save(user);
		return (result!=null);
	}

	public Optional<User> getUser(String email) {
		return userRepo.findByEmail(email);
	}
	
	//naver 회원가입
	public Map<String, Object> getNaverUserInfo(String token) {
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

	//kakao 회원가입
	public Map<String, Object> getKakaoUserInfo(String token) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		HttpEntity<MultiValueMap<String, String>> header = new HttpEntity<>(headers);
		ResponseEntity<String> res = rt.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.GET,
				header,
				String.class
				);
		
		Map<String, Object> test = new HashMap<>();
		test.put("email", res.getBody());
		System.out.println(res.getBody());
		
		return test;
	}
}
