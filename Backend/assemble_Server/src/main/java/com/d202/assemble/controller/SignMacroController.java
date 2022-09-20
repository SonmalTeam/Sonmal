package com.d202.assemble.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/sign/macro")
@RequiredArgsConstructor
public class SignMacroController {

    // 매크로 조회
    @ApiOperation(value = "매크로 조회")
    @GetMapping
    public String getSignMacroList(){
        return "test ~!";
    }

    // 매크로 등록
    @ApiOperation(value = "매크로 등록")
    @PostMapping
    public String createSignMacro(){
        return "test ~!";
    }

    // 매크로 재생
    @ApiOperation(value = "매크로 재생")
    @ApiImplicitParam(name = "signMacroSeq", value = "매크로 PK", example = "1", required = true)
    @GetMapping("/{sign_macro_seq}")
    public String playSignMacro(@PathVariable Long signMacroSeq){
        return "test ~!";
    }
}
