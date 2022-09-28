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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.d202.assemble.dto.JwtToken;
import com.d202.assemble.dto.JwtTokenDto;
import com.d202.assemble.dto.User;
import com.d202.assemble.jwt.JwtUtils;
import com.d202.assemble.repo.JwtTokenRepo;
import com.d202.assemble.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepo userRepo;
	private final JwtTokenRepo jwtTokenRepo;
	
	@Transactional
	public JwtTokenDto loginUser(String email) {
		//가입된 유저인지 확인
		Optional<User> userOp = userRepo.findByEmail(email);
		User realUser = null;
		if(!userOp.isPresent()) {//가입안 된 user면 => DB save
			User user = new User();
			user.setEmail(email);
			realUser = userRepo.save(user);
			if(realUser == null) {
				return null;
			}
		}
		else {
			realUser = userOp.get();
		}
		JwtTokenDto jwtTokenDto = new JwtTokenDto(JwtUtils.createAccessToken(realUser), JwtUtils.createRefreshToken(realUser));
		//token저장
		
		JwtToken jwtToken = jwtTokenRepo.findByUserSeq(realUser.getSeq()).orElseGet(()->new JwtToken());
		jwtToken.setUserSeq(realUser.getSeq());
		jwtToken.setAccessToken(jwtTokenDto.getAccessToken());
		jwtToken.setRefreshToken(jwtTokenDto.getRefreshToken());
		jwtTokenRepo.save(jwtToken);
		return jwtTokenDto;
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
