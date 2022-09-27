package com.d202.assemble.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.d202.assemble.dto.JwtToken;
import com.d202.assemble.repo.JwtTokenRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
	
	private final JwtTokenRepo jwtTokenRepo;
	
	public Optional<JwtToken> getJwtTokenByUserSeq(Integer userSeq) {
		return jwtTokenRepo.findByUserSeq(userSeq);
	}
	
	public JwtToken getJwtTokenByRT(String refreshToken){
		Optional<JwtToken> token = jwtTokenRepo.findByRefreshToken(refreshToken);
		if(token.isPresent()) {
			return token.get();
		}
		return null;
	}
	
	@Transactional
	public JwtToken changeToken(JwtToken jwt) {
		System.out.println(jwt);
		return jwtTokenRepo.save(jwt);
	}
}
