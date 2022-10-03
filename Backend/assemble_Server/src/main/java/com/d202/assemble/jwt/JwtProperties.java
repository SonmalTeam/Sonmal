package com.d202.assemble.jwt;

//jwt 기본 설정
//public static final => interface 사용
public interface JwtProperties {
	//String SECRET ="{}";//secret key는 random을 쓸거임
//	int ACCESS_EXP_TIME = 1800000; //만료시간 30분
	int ACCESS_EXP_TIME = 30000;//테스트 30초
//	int REFRESH_EXP_TIME = 1_209_600_000; //만료시간 2주
	int REFRESH_EXP_TIME = 50000; //테스트 50초
	//String TOKEN_PREFIX = "Bearer ";//토큰 앞에 붙이는 것, 전달자명(없어도 되는듯)
	String JWT_ACCESS_NAME = "JWT-AUTHENTICATION";
}
