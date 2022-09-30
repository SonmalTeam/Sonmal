package com.d202.assemble.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class WebController {
	
	@GetMapping(value="/*privacy")
	public String showPolicy() {
		log.info("policy출력해라좀");
		return "policy";
	}
}
