package com.d202.assemble.service;

import com.d202.assemble.dto.Category;
import com.d202.assemble.dto.SignMacro;
import com.d202.assemble.dto.SignMacroRequestDto;
import com.d202.assemble.dto.SignMacroResponseDto;
import com.d202.assemble.repo.CategoryRepo;
import com.d202.assemble.repo.SignMacroRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignMacroService {

    private SignMacroRepo signMacroRepo;
    private CategoryRepo categoryRepo;


    @Transactional
    public void createSignMacro(Long userSeq, SignMacroRequestDto request){
        SignMacro signMacro = request.toEntity();

        Category category = categoryRepo.findBySeq(request.getCategorySeq());
        signMacro.setCategory(category);
        signMacro.setUserSeq(userSeq);
        signMacro.setRegDttm(LocalDateTime.now());

        signMacroRepo.save(signMacro);
    }

    @Transactional
    public List<SignMacroResponseDto> getSignMacroList(long userSeq, long categorySeq) {

        List<SignMacroResponseDto> result = new ArrayList<>();
        List<SignMacro> signMacros = signMacroRepo.findBySeqAndCategorySeq(userSeq, categorySeq);

        for (SignMacro signMacro : signMacros) {
            result.add(new SignMacroResponseDto(signMacro));
        }

        return result;
    }
}
