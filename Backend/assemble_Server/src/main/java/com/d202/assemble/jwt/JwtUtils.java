package com.d202.assemble.jwt;

import java.security.Key;
import java.util.Date;

import com.d202.assemble.dto.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtils {
	/**
     * user로 토큰 생성
     * HEADER : alg, kid
     * PAYLOAD : sub, iat, exp
     * SIGNATURE : JwtKey.getRandomKey로 구한 Secret Key로 HS512 해시
     *
     * @param user 유저
     * @return jwt token
     */
    public static String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(Integer.toString(user.getSeq())); // subject
        Date now = new Date(); // 현재 시간
        Pair<String, Key> key = JwtKey.getRandomKey();
        //key 이름 , key값
        
        // JWT Token 생성
        return Jwts.builder()
        		.setHeaderParam(JwsHeader.KEY_ID, key.getFirst()) // keyId를 header정보로
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                //setExpiration(new Date(now.getTime() + JwtProperties.EXPIRATION_TIME)) // 토큰 만료 시간 설정
                .signWith(key.getSecond(), SignatureAlgorithm.HS512) // signature
                .compact();
    }
}
