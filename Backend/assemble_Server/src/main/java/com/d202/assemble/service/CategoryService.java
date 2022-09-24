package com.d202.assemble.service;

import com.d202.assemble.dto.Category;
import com.d202.assemble.dto.UserCategory;
import com.d202.assemble.repo.CategoryRepo;
import com.d202.assemble.repo.UserCategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final UserCategoryRepo userCategoryRepo;
    private final CategoryRepo categoryRepo;

    // 카테고리 리스트 조회
    public List<Category> getCategoryList(Long userSeq) {

        List<Category> result = new ArrayList<>();
        List<Long> userCategory = userCategoryRepo.findByUserSeq(userSeq).orElseThrow().getCategoryList();

        for(long categorySeq : userCategory) {
            result.add(categoryRepo.findBySeq(categorySeq));
        }

        return result;
    }

    // 카테고리 순서 수정
    public List<Category> updateCategoryList(Long userSeq, List<Long> categories) {

        List<Category> result = new ArrayList<>();

//        List<Long> defaultList = new ArrayList<>() {1, 2, 3, 4, 5, 6};
        userCategoryRepo.findByUserSeq(userSeq).orElseThrow().setCategoryList(categories);
        List<Long> userCategory = userCategoryRepo.findByUserSeq(userSeq).orElseThrow().getCategoryList();

        for(long categorySeq : userCategory) {
            result.add(categoryRepo.findBySeq(categorySeq));
        }

        return result;
    }
}
