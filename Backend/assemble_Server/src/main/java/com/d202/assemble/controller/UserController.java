package com.d202.assemble.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.d202.assemble.dto.JwtToken;
import com.d202.assemble.dto.JwtTokenDto;
import com.d202.assemble.dto.User;
import com.d202.assemble.jwt.JwtUtils;
import com.d202.assemble.service.JwtTokenService;
import com.d202.assemble.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@Api(value="User")
@RestController
@RequestMapping("/user")
//lombok 생성자 주입 => 초기화되지않은 final 변수에 주입시킴 (생성자가 1개일때 가능)
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	private final JwtTokenService jwtTokenService;
	
	@ApiOperation(value="회원정보 상세 조회")
	@GetMapping("/detail")
	public ResponseEntity<?> getUserDetail(@ApiIgnore Authentication auth){
//	public ResponseEntity<?> getUserDetail(){
		
		int seq = 1;//(int)auth.getPrincipal();
		Optional<User> userOp = userService.findUserBySeq(seq);
		if(userOp.isPresent()) {
			return new ResponseEntity<User>(userOp.get(), HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}
	
	@ApiOperation(value="네이버 로그인")
	@PostMapping("/naver/login")
	public ResponseEntity<?> naverLogin(@RequestBody String token){
		Map<String, Object> userInfo = userService.getNaverUserInfo(token);
		if(userInfo!=null) {
			String email = userInfo.get("email").toString();
			//가입된 유저인지 확인
			Optional<User> userOp = userService.findUserByEmail(email);
			User realUser = userOp.get();
			if(realUser==null) {//가입안 된 user면 => DB save
				User user = new User();
				user.setEmail(userInfo.get("email").toString());
				realUser = userService.insertUser(user);
				if(realUser == null) {
					return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
				}
			}
			JwtTokenDto jwtTokenDto = new JwtTokenDto(JwtUtils.createAccessToken(realUser), JwtUtils.createRefreshToken(realUser));
			//token저장
			JwtToken jwtToken = new JwtToken();
			jwtToken.setUserSeq(realUser.getSeq());
			jwtToken.setAccessToken(jwtTokenDto.getAccessToken());
			jwtToken.setRefreshToken(jwtTokenDto.getRefreshToken());
			jwtTokenService.changeToken(jwtToken);
			return new ResponseEntity<JwtTokenDto>(jwtTokenDto, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}
	
	@ApiOperation(value="카카오 로그인")
	@PostMapping("/kakao/login")
	public ResponseEntity<?> kakaoLogin(@RequestBody String token){
		Map<String, Object> userInfo = userService.getKakaoUserInfo(token);
		if(userInfo!=null) {
			String email = userInfo.get("email").toString();
			//가입된 유저인지 확인
			Optional<User> userOp = userService.findUserByEmail(email);
			User realUser = userOp.get();
			if(realUser==null) {//가입안 된 user면 => DB save
				User user = new User();
				user.setEmail(userInfo.get("email").toString());
				realUser = userService.insertUser(user);
				if(realUser == null) {
					return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
				}
			}
			JwtTokenDto jwtTokenDto = new JwtTokenDto(JwtUtils.createAccessToken(realUser), JwtUtils.createRefreshToken(realUser));
			//token저장
			JwtToken jwtToken = new JwtToken();
			jwtToken.setUserSeq(realUser.getSeq());
			jwtToken.setAccessToken(jwtTokenDto.getAccessToken());
			jwtToken.setRefreshToken(jwtTokenDto.getRefreshToken());
			jwtTokenService.changeToken(jwtToken);
			return new ResponseEntity<JwtTokenDto>(jwtTokenDto, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}
	
	@ApiOperation(value="회원탈퇴")
	@DeleteMapping()
	public ResponseEntity<?> disJoin(@ApiIgnore Authentication auth){
		int seq = (int)auth.getPrincipal();
		try {
			userService.deleteUser(seq);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	
	
	//-------------------------------------------------
	//user정보넣기
//	@ApiOperation(value="user Post테스트")
//	@PostMapping("/jwt/user")
//	public ResponseEntity<?> insert(@RequestBody String email){
//		User user = new User();
//		user.setEmail(email);
//		//userService.insertUser(user);
//		return new ResponseEntity<Void>(HttpStatus.OK);
//	}
	
	
}
