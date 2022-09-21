package com.d202.assemble.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Server;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

//http://localhost:8090/swagger-ui/index.html#/
@Configuration
public class SwaggerConfig {
	@Bean
	public Docket api() {
		final ApiInfo apiInfo = new ApiInfoBuilder()
				.title("구미 특화2반 2팀")
				.description("<h3>김남희 배시현 배한용 서재형 정봉진 편예린</h3>")
				.contact(new Contact("D202", "https://edu.ssafy.com", "ssafy@ssafy.com"))
				.license("MIT License")
				.version("1.0")
				.build();

		Server serverLocal = new Server("local", "http://localhost:8090", "for local usages", Collections.emptyList(), Collections.emptyList());
		Server testServer = new Server("test", "https://d202.kro.kr:8090", "for testing", Collections.emptyList(), Collections.emptyList());
//		return new Docket(DocumentationType.SWAGGER_2)
		return new Docket(DocumentationType.OAS_30)
				.servers(serverLocal, testServer)
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.d202.assemble.controller"))
				.paths(PathSelectors.ant("/**"))
				.build();
	}

}
