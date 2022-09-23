package com.d202.assemble.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.d202.assemble.dto.User;
import com.d202.assemble.jwt.JwtUtils;
import com.d202.assemble.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(value="JWT")
@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class JwtController {
	
	private final UserService userService;
	//seq아니면 email 중에 뭐로 jwt발급할지
		@ApiOperation(value="jwt발급 테스트")
		@GetMapping("/jwt")
		public ResponseEntity<?> getToken(){
			Optional<User> userOp = userService.findUserByEmail("test");
			if(userOp.isPresent()) {
				String token = JwtUtils.createToken(userOp.get());
				return new ResponseEntity<String>(token, HttpStatus.OK);
			}
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		
		@ApiOperation(value="jwt유효성 테스트")
		@PostMapping("/jwt")
		public ResponseEntity<?> validateToken(@RequestBody String jwt){
			//prefix제거
			String token = jwt;//.replace(JwtProperties.TOKEN_PREFIX, "");
			if(JwtUtils.validateToken(token)) {
				System.out.println("인증완료");
				System.out.println(JwtUtils.getUserSeq(token));
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
}
