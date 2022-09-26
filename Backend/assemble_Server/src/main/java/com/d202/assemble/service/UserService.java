package com.d202.assemble.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
	
	private final UserRepo userRepo;
	
	@Transactional
	public User insertUser(User user) {
		return userRepo.save(user);
	}
	
	public Optional<User> findUserBySeq(int seq){
		return userRepo.findById(seq);
	}

	public Optional<User> findUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}
	
	@Transactional
	public void deleteUser(int seq) throws Exception {
		userRepo.deleteById(seq);
	}
	
	//naver 회원정보 받기
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
		
		//결과 parsing
		Map<String, Object> userInfo = null;
		JSONParser jsonParser = new JSONParser();
		try {
			System.out.println(res.getBody());
			JSONObject jsonObj = (JSONObject)jsonParser.parse(res.getBody());
			jsonObj = (JSONObject)jsonParser.parse(jsonObj.get("response").toString());
			userInfo = new HashMap<>();
			userInfo.put("email", jsonObj.get("email"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return userInfo;
	}

	//kakao 회원정보 받기
	public Map<String, Object> getKakaoUserInfo(String token) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		HttpEntity<MultiValueMap<String, String>> header = new HttpEntity<>(headers);
		System.out.println("here");
		ResponseEntity<String> res = rt.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.GET,
				header,
				String.class
				);
		
		//결과 parsing
		Map<String, Object> userInfo = null;
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObj = (JSONObject)jsonParser.parse(res.getBody());
			jsonObj = (JSONObject)jsonParser.parse(jsonObj.get("kakao_account").toString());
			userInfo = new HashMap<>();
			userInfo.put("email", jsonObj.get("email"));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return userInfo;
	}
}
