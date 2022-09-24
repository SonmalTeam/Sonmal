package com.d202.assemble.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class JwtToken {
	
	@Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int seq;
	@Column(name = "user_seq", nullable = false)
	private int userSeq;
	@Column(name = "access_token", nullable = false)
	private String accessToken;
	@Column(name = "refresh_token", nullable = false)
	private String refreshToken;
}
