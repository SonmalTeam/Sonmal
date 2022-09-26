package com.d202.assemble.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d202.assemble.dto.JwtToken;

public interface JwtTokenRepo extends JpaRepository<JwtToken, Integer>{
	public Optional<JwtToken> findByRefreshToken(String refreshToken);
}
