package com.d202.assemble.jwt;

//jwt 기본 설정
//public static final => interface 사용
public interface JwtProperties {
	//String SECRET ="{}";//secret key는 random을 쓸거임
	int EXPIRATION_TIME = 600000; //만료시간 10분
	//String TOKEN_PREFIX = "Bearer ";//토큰 앞에 붙이는 것, 전달자명(없어도 되는듯)
	String JWT_ACCESS_NAME = "JWT-AUTHENTICATION";
}
