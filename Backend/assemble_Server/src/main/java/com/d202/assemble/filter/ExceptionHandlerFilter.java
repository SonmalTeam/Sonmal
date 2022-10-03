package com.d202.assemble.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try{
	          filterChain.doFilter(request,response);
	      } catch (ExpiredJwtException e){
	          log.error("jwt expired exception handler filter");
	          response.setContentType("application/json");
	          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	      }catch (JwtException e2){
	          log.error("JWT exception handler filter");
	          response.setContentType("application/json");
	          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	      }
	}

}
