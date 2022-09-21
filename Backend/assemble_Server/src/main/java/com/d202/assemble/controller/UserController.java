package com.d202.assemble.controller;

import java.util.Map;

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

@Api(value="User")
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@ApiOperation(value="네이버 회원가입")
	@PostMapping("/naver/join")
	public ResponseEntity<?> naverJoin(@RequestBody String token){
		Map<String, Object> userInfo = userService.getUserInfo(token);
		User user = new User();
		user.setEmail(userInfo.get("email").toString());
		if(userService.insertUser(user)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value="네이버 로그인")
	@PostMapping("/naver/login")
	public ResponseEntity<?> naverLogin(@RequestBody String token){
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
}
