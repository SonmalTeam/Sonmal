package com.d202.assemble.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.d202.assemble.dto.JwtToken;
import com.d202.assemble.dto.JwtTokenDto;
import com.d202.assemble.dto.User;
import com.d202.assemble.jwt.JwtUtils;
import com.d202.assemble.repo.JwtTokenRepo;
import com.d202.assemble.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
	
	private final JwtTokenRepo jwtTokenRepo;
	private final UserRepo userRepo;
	
	@Transactional
	public JwtTokenDto refreshToken(String accessToken, String refreshToken) {
		//---
		//1. access token만료 확인
		boolean expired = false;
		try {
			JwtUtils.validateToken(accessToken);
		} catch (Exception e) {//만료됨
			expired = true;
		}
		//만료 안됐으면 refresh 불가
		if(!expired) {
			return null;
		}
		
		//2. refresh token 유효성 확인
		try{
			JwtUtils.validateToken(refreshToken);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JwtToken jwtToken = jwtTokenRepo.findByRefreshToken(refreshToken).get();
		if(jwtToken==null || !accessToken.equals(jwtToken.getAccessToken())) {
			return null;
		}
		
		//3. access token & refresh token 재발급 후, 저장
		int seq = Integer.parseInt(JwtUtils.getUserSeq(refreshToken));
		Optional<User> userOp = userRepo.findById(seq);
		if(!userOp.isPresent()) {
			return null;
		}
		
		User user = userOp.get();
		String newAt = JwtUtils.createAccessToken(user);
		String newRt = JwtUtils.createRefreshToken(user);
		jwtToken.setAccessToken(newAt);
		jwtToken.setRefreshToken(newRt);
		jwtTokenRepo.save(jwtToken);
		
		//4. return
		JwtTokenDto jwtTokenDto = new JwtTokenDto(newAt, newRt);
		return jwtTokenDto;
	}
	
	public Optional<JwtToken> getJwtTokenByUserSeq(Integer userSeq) {
		return jwtTokenRepo.findByUserSeq(userSeq);
	}
	
//	public JwtToken getJwtTokenByRT(String refreshToken){
//		Optional<JwtToken> token = jwtTokenRepo.findByRefreshToken(refreshToken);
//		if(token.isPresent()) {
//			return token.get();
//		}
//		return null;
//	}
	
	@Transactional
	public JwtToken changeToken(JwtToken jwt) {
		return jwtTokenRepo.save(jwt);
	}
}
