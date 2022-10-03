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

        List<Category> Categories = categoryRepo.findAll();
        List<Long> defaultList = new ArrayList<>();
        for(Category category : Categories) {
            defaultList.add(category.getSeq());
        }
        UserCategory defaultUser = new UserCategory(new Long(0), defaultList);

        UserCategory userCategory = userCategoryRepo.findByUserSeq(userSeq).orElseGet(()->defaultUser);

        for(long categorySeq : userCategory.getCategoryList()) {
            result.add(categoryRepo.findBySeq(categorySeq));
        }

        return result;
    }

    // 카테고리 순서 수정
    public List<Category> updateCategoryList(Long userSeq, List<Long> categories) {

        List<Category> result = new ArrayList<>();
        userCategoryRepo.deleteByUserSeq(userSeq);
        UserCategory userCategory = new UserCategory(userSeq, categories);
        userCategoryRepo.save(userCategory);
        UserCategory CategorySeq = userCategoryRepo.findByUserSeq(userSeq).orElseThrow();

        for(long categorySeq : CategorySeq.getCategoryList()) {
            result.add(categoryRepo.findBySeq(categorySeq));
        }

        return result;
    }
}
