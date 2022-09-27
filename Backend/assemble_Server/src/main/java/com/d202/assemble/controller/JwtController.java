package com.d202.assemble.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(value="JWT")
@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class JwtController {
	
	private final UserService userService;
	private final JwtTokenService jwtTokenService;
	
	//만료되면, refresh 요청
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody JwtTokenDto requestToken){
		//0. 요청 확인
		String accessToken = requestToken.getAccessToken();
		String refreshToken = requestToken.getRefreshToken();
		if(requestToken.getAccessToken()==null || requestToken.getRefreshToken()==null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		//1. access token만료 확인
		boolean expired = false;
		try {
			JwtUtils.validateToken(accessToken);
		} catch (Exception e) {//만료됨
			expired = true;
		}
		//만료 안됐으면 refresh 불가
		if(!expired) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		//2. refresh token 유효성 확인
		try{
			JwtUtils.validateToken(refreshToken);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		JwtToken jwtToken = jwtTokenService.getJwtTokenByRT(refreshToken);
		if(jwtToken==null || !accessToken.equals(jwtToken.getAccessToken())) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		//3. access token & refresh token 재발급 후, 저장
		int seq = Integer.parseInt(JwtUtils.getUserSeq(refreshToken));
		Optional<User> userOp = userService.findUserBySeq(seq);
		if(!userOp.isPresent()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		User user = userOp.get();
		String newAt = JwtUtils.createAccessToken(user);
		String newRt = JwtUtils.createRefreshToken(user);
		jwtToken.setAccessToken(newAt);
		jwtToken.setRefreshToken(newRt);
		jwtTokenService.changeToken(jwtToken);
		
		//4. return
		JwtTokenDto jwtTokenDto = new JwtTokenDto(newAt, newRt);
		
		return new ResponseEntity<JwtTokenDto>(jwtTokenDto, HttpStatus.OK);
	}
	
	//--------------------테스트 코드---------------------//
	//seq아니면 email 중에 뭐로 jwt발급할지
	@ApiOperation(value="jwt발급 테스트")
	@GetMapping("/{email}")
	public ResponseEntity<?> getToken(@PathVariable String email){
		Optional<User> userOp = userService.findUserByEmail(email);
		if(userOp.isPresent()) {
			User user = userOp.get();
			JwtTokenDto jwtTokenDto = new JwtTokenDto(JwtUtils.createAccessToken(user), JwtUtils.createRefreshToken(user));
			JwtToken jwt = jwtTokenService.getJwtTokenByUserSeq(user.getSeq()).orElseGet(()->new JwtToken());
			jwt.setUserSeq(user.getSeq());
			jwt.setAccessToken(jwtTokenDto.getAccessToken());
			jwt.setRefreshToken(jwtTokenDto.getRefreshToken());
			jwtTokenService.changeToken(jwt);
			return new ResponseEntity<JwtTokenDto>(jwtTokenDto, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}
		
	@ApiOperation(value="jwt유효성 테스트")
	@PostMapping()
	public ResponseEntity<?> validateToken(@RequestBody String jwt){
		//prefix제거
		String token = jwt;//.replace(JwtProperties.TOKEN_PREFIX, "");
		try {
			JwtUtils.validateToken(token);
		} catch (ExpiredJwtException e) {
			//만료됨
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		} catch(JwtException e2) {
			//유효하지 않은 토큰
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
//	@ApiOperation(value="jwt무효화 테스트")
//	@PostMapping()
//	public ResponseEntity<?> invalidateToken(@RequestBody String jwt){
//		
//		return new ResponseEntity<Void>(HttpStatus.OK);
//	}
}
