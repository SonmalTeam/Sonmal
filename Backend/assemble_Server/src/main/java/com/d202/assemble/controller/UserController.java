package com.d202.assemble.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="User")
@RestController
@RequestMapping("/user")
public class UserController {
	
	@ApiOperation(value="네이버 회원가입")
	@PostMapping("/naver/join")
	public ResponseEntity<?> naverJoin(@RequestBody String token){
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value="네이버 로그인")
	@PostMapping("/naver/login")
	public ResponseEntity<?> naverLogin(@RequestBody String token){
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
}
