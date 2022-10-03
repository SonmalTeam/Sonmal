package com.d202.assemble.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.security.access.AccessDeniedException;

import com.d202.assemble.dto.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JwtUtils {
	//유효성 검사
	public static boolean validateToken(String token) {
		// jwtToken에서 seq를 찾습니다.
//		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
	                .setSigningKeyResolver(SigningKeyResolver.instance) //키에 맞는 키값을 가져오는 역할
	                .build()
	                .parseClaimsJws(token); //키를 통해 검증,만료확인 부적절시 익셉션 발생
//		}
//		catch(ExpiredJwtException e1){
//			log.info("만료예외처리");
//			throw new AccessDeniedException("");
//		}
//		catch(JwtException e2) {
//			log.info("jwt예외처리");
//			throw new AccessDeniedException("");
//		}
    	
    	return true;
	}
	
	/**
     * 토큰에서 seq 찾기
     *
     * @param token 토큰
     * @return seq
     */
    public static String getUserSeq(String token) {
    	
        // jwtToken에서 seq를 찾습니다.
//    	try {
    		 return Jwts.parserBuilder()
 	                .setSigningKeyResolver(SigningKeyResolver.instance) //키에 맞는 키값을 가져오는 역할
 	                .build()
 	                .parseClaimsJws(token) //키를 통해 검증,만료확인 부적절시 익셉션 발생
 	                .getBody()
 	                .getSubject(); // username
//    	}catch(ExpiredJwtException e) {
//    		System.out.println("만료됨!!");
//    		return e.getClaims().get                                                                                                                                                                                                                                                                                                                           Subject();
//    	} catch(JwtException e) {
//    		throw new RuntimeException("유효하지 않은 토큰입니다.");
//    	}
    }

	
	/**
     * user로 토큰 생성
     * HEADER : alg, kid
     * PAYLOAD : sub, iat, exp
     * SIGNATURE : JwtKey.getRandomKey로 구한 Secret Key로 HS512 해시
     *
     * @param user 유저
     * @return jwt token
     */
    public static String createAccessToken(User user) {
        Claims claims = Jwts.claims().setSubject(Integer.toString(user.getSeq())); // subject
        Date now = new Date(); // 현재 시간
        Pair<String, Key> key = JwtKey.getRandomKey();
        //key 이름 , key값
        
        // JWT Token 생성
        return Jwts.builder()
        		.setHeaderParam(JwsHeader.KEY_ID, key.getFirst()) // keyId를 header정보로
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtProperties.ACCESS_EXP_TIME)) // 토큰 만료 시간 설정
                .signWith(key.getSecond(), SignatureAlgorithm.HS512) // signature
                .compact();
    }
    
    //refresh token(기간만 다름)
    public static String createRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(Integer.toString(user.getSeq())); // subject
        Date now = new Date(); // 현재 시간
        Pair<String, Key> key = JwtKey.getRandomKey();
        //key 이름 , key값
        
        // JWT Token 생성
        return Jwts.builder()
        		.setHeaderParam(JwsHeader.KEY_ID, key.getFirst()) // keyId를 header정보로
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtProperties.REFRESH_EXP_TIME)) // 토큰 만료 시간 설정
                .signWith(key.getSecond(), SignatureAlgorithm.HS512) // signature
                .compact();
    }
}
