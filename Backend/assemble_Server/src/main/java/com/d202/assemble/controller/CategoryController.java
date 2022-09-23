package com.d202.assemble.controller;

import com.d202.assemble.dto.Category;
import com.d202.assemble.dto.SignMacroResponseDto;
import com.d202.assemble.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/user/category")
@RequiredArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    // 카테고리 리스트 조회
    @ApiOperation(value = "카테고리 리스트 조회")
    public List<Category> getCategoryList(){

        return categoryService.getCategoryList(new Long(1));
    }

    // 카테고리 순서 수정
    @ApiOperation(value = "카테고리 순서 수정")
    public List<Category> getCategoryList(@RequestParam List<Long> categories){

        return categoryService.updateCategoryList(new Long(1), categories);
    }
}
