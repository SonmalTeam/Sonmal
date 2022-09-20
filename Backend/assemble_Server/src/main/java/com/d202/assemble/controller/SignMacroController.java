package com.d202.assemble.controller;

import com.d202.assemble.dto.SignMacroRequestDto;
import com.d202.assemble.dto.SignMacroResponseDto;
import com.d202.assemble.service.SignMacroService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/sign/macro")
@RequiredArgsConstructor
public class SignMacroController {

    private final SignMacroService signMacroService;

    // 매크로 등록
    @ApiOperation(value = "매크로 등록")
    @PostMapping
    public void createSignMacro(@RequestBody final SignMacroRequestDto request){

        signMacroService.createSignMacro(new Long(1), request);
    }

    // 매크로 리스트 조회
    @ApiOperation(value = "매크로 리스트 조회")
    @GetMapping("/category/{categorySeq}")
    public List<SignMacroResponseDto> getSignMacroList(@PathVariable Long categorySeq){

        return signMacroService.getSignMacroList(new Long(1), categorySeq);
    }

    // 매크로 재생
    @ApiOperation(value = "매크로 재생")
    @ApiImplicitParam(name = "signMacroSeq", value = "매크로 PK", example = "1", required = true)
    @GetMapping("/{signMacroSeq}")
    public String playSignMacro(@PathVariable Long signMacroSeq){
        return "test ~!";
    }
}
