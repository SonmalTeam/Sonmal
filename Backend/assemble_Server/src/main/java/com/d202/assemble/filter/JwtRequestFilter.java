package com.d202.assemble.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.d202.assemble.jwt.JwtProperties;
import com.d202.assemble.jwt.JwtUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, ExpiredJwtException, JwtException {
		String jwt = request.getHeader(JwtProperties.JWT_ACCESS_NAME);
//		Enumeration<?> headerNames = request.getHeaderNames();
//		while(headerNames.hasMoreElements()) {
//			String name = (String)headerNames.nextElement();
//			String value = request.getHeader(name);
//			System.out.println(name+" : "+value);
//			}
//		System.out.println("here1"+jwt);
		//prefix확인
		if(jwt == null || "".equals(jwt)){//|| !jwt.startsWith(JwtProperties.TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
            return;
        }
		
		//prefix제거
		String token = jwt;//.replace(JwtProperties.TOKEN_PREFIX, "");
		if(JwtUtils.validateToken(token)) {//인증안되면 exception발생
			//아래 코드가 인가하는 코드
			String userSeq = JwtUtils.getUserSeq(token);
			Authentication auth = new UsernamePasswordAuthenticationToken(Integer.parseInt(userSeq), null, null);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		filterChain.doFilter(request, response);
	}

}
