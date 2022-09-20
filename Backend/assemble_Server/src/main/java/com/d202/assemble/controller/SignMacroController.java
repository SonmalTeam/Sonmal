package com.d202.assemble.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/sign/macro")
@RequiredArgsConstructor
public class SignMacroController {

    // 매크로 조회
//    @ApiOperation(value = "매크로 조회")
//    @GetMapping
//    public void findSignMacro(){
//        return "test ~!";
//    }
}
