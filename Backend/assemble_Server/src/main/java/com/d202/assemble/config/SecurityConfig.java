package com.d202.assemble.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.d202.assemble.filter.CustomAccessDeniedHandler;
import com.d202.assemble.filter.CustomAuthenticationEntryPoint;
import com.d202.assemble.filter.ExceptionHandlerFilter;
import com.d202.assemble.filter.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final ExceptionHandlerFilter exceptionHandlerFilter;
	private final JwtRequestFilter jwtRequestFilter;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable()
		.csrf().disable()
		.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(exceptionHandlerFilter, JwtRequestFilter.class);
		http.exceptionHandling()
		.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
//		.accessDeniedHandler(new CustomAccessDeniedHandler());
		
		http.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/user").authenticated()
		.antMatchers(HttpMethod.POST, "**/macro/**").authenticated()
		.antMatchers(HttpMethod.GET, "/user/**").authenticated()
		.antMatchers(HttpMethod.GET, "/**/macro/**").authenticated()
		.antMatchers(HttpMethod.DELETE).authenticated()
		.antMatchers(HttpMethod.PUT).authenticated();

		// login. 해당 url로 요청할 시 로그인 과정을 거치게 된다.
		//http.formLogin().loginPage("/user/**/login").defaultSuccessUrl("/").permitAll(); // 모두 허용
	}

	@Override
	public void configure(WebSecurity web) {
		// 정적 리소스 spring security 대상에서 제외
		web.ignoring().antMatchers("/swagger-ui/**");
		web.httpFirewall(new DefaultHttpFirewall());// '//'허용..
	}


	//CORS 해결
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOriginPattern("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
