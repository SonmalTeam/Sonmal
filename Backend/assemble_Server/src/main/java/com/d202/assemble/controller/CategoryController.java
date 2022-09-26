package com.d202.assemble.controller;

import com.d202.assemble.dto.Category;
import com.d202.assemble.dto.SignMacroResponseDto;
import com.d202.assemble.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/user/category")
@RequiredArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    // 카테고리 리스트 조회
    @ApiOperation(value = "카테고리 리스트 조회")
    public List<Category> getCategoryList(@ApiIgnore Authentication auth){
        Long userSeq = (Long)auth.getPrincipal();
        return categoryService.getCategoryList(userSeq);
    }

    // 카테고리 순서 수정
    @ApiOperation(value = "카테고리 순서 수정")
    public List<Category> getCategoryList(@ApiIgnore Authentication auth, @RequestParam List<Long> categories){
        Long userSeq = (Long)auth.getPrincipal();
        return categoryService.updateCategoryList(userSeq, categories);
    }
}
