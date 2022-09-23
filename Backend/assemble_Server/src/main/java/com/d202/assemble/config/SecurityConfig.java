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

import com.d202.assemble.jwt.JwtRequestFilter;
import com.google.api.client.http.HttpMethods;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable()
		.csrf().disable()
		.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(new JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
		
		http.authorizeRequests()
		.antMatchers(HttpMethod.POST).authenticated()
		.antMatchers(HttpMethod.GET, "/user/**").authenticated()
		.antMatchers(HttpMethod.GET, "/**/macro/**").authenticated()
		.antMatchers(HttpMethod.DELETE).authenticated()
		.antMatchers(HttpMethod.PUT).authenticated(); 

		
		// login. 해당 url로 요청할 시 로그인 과정을 거치게 된다.
		//http.formLogin().loginPage("/user/**/login").defaultSuccessUrl("/").permitAll(); // 모두 허용

//		http.authorizeRequests()
//		        .antMatchers(FRONT_URL+"/main/**")
//		        .authenticated()
//		        .anyRequest().permitAll()
//		
//		        .and()
//		        //(1)
//		        .exceptionHandling()
//		        .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
		
	}
	
	@Override
	public void configure(WebSecurity web) {
		// 정적 리소스 spring security 대상에서 제외
//        web.ignoring().antMatchers("/images/**", "/css/**"); // 아래 코드와 같은 코드입니다.
		//web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
		//web.ignoring().antMatchers("/api/swagger-ui/**");
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
