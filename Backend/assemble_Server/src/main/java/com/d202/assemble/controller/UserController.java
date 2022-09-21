package com.d202.assemble.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.d202.assemble.dto.User;
import com.d202.assemble.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(value="User")
@RestController
@RequestMapping("/user")
//lombok 생성자 주입 => 초기화되지않은 final 변수에 주입시킴 (생성자가 1개일때 가능)
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@ApiOperation(value="네이버 회원가입")
	@PostMapping("/naver/join")
	public ResponseEntity<?> naverJoin(@RequestBody String token){
		Map<String, Object> userInfo = userService.getNaverUserInfo(token);
		if(userInfo!=null) {
			User user = new User();
			user.setEmail(userInfo.get("email").toString());
			if(userService.insertUser(user)) {
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value="카카오 회원가입")
	@PostMapping("/kakao/join")
	public ResponseEntity<?> kakaoJoin(@RequestBody String token){
		token = "FOP0N5Z6cfTh54e6z70uzNWJm29F8T2Je8KUg7GBCilv1QAAAYNfP0t-";
		Map<String, Object> userInfo = userService.getKakaoUserInfo(token);
		if(userInfo!=null) {
			User user = new User();
			user.setEmail(userInfo.get("email").toString());
			//test
			return new ResponseEntity<User>(user, HttpStatus.OK);
//			if(userService.insertUser(user)) {
//				return new ResponseEntity<Void>(HttpStatus.OK);
//			}
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value="네이버 로그인")
	@PostMapping("/naver/login")
	public ResponseEntity<?> naverLogin(@RequestBody String token){
		Map<String, Object> userInfo = userService.getNaverUserInfo(token);
		if(userInfo!=null) {
			Optional<User> res = userService.getUser((String)userInfo.get("email"));
			if(res.isPresent()) {
				return new ResponseEntity<User>(res.get(), HttpStatus.OK);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
}
