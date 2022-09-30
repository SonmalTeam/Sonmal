package com.d202.assemble.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class WebController {
	
	@GetMapping(value="/")
	public String showPolicy() {
		log.info("policy출력해라좀");
		return "policy";
	}
}
