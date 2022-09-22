package com.d202.assemble.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String jwt = request.getHeader(JwtProperties.JWT_ACESS_NAME);
		
		//prefix확인
		if(jwt == null || !jwt.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
		
		//prefix제거
		String token = jwt.replace(JwtProperties.TOKEN_PREFIX, "");
		if(JwtUtils.validateToken(token)) {
			System.out.println("인증완료");
			System.out.println(JwtUtils.getUserSeq(token));
		}
	}

}
