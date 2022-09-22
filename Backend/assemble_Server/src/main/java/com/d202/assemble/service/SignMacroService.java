package com.d202.assemble.service;

import com.d202.assemble.dto.*;
import com.d202.assemble.repo.CategoryRepo;
import com.d202.assemble.repo.SignMacroRepo;
import com.d202.assemble.repo.VideoFileRepo;
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

    private final SignMacroRepo signMacroRepo;
    private final CategoryRepo categoryRepo;
    private final VideoFileRepo videoFileRepo;

    @Transactional
    public void createSignMacro(Long userSeq, SignMacroRequestDto request){
        SignMacro signMacro = request.toEntity();

        Category category = categoryRepo.findBySeq(request.getCategorySeq());
        signMacro.setCategory(category);
        signMacro.setUserSeq(userSeq);
        signMacro.setRegDttm(LocalDateTime.now());
        signMacro.setCount(new Long(0));
        signMacroRepo.save(signMacro);
    }

    @Transactional
    public List<SignMacroResponseDto> getSignMacroList(long userSeq, long categorySeq) {

        List<SignMacroResponseDto> result = new ArrayList<>();
        List<SignMacro> signMacros = signMacroRepo.findAllByUserSeqAndCategorySeq(userSeq, categorySeq);

        for (SignMacro signMacro : signMacros) {
            result.add(new SignMacroResponseDto(signMacro));
        }

        return result;
    }

    @Transactional
    public void deleteSignMacro(long signMacroSeq) {
        SignMacro signMacro = signMacroRepo.findBySeq(signMacroSeq).get();
        videoFileRepo.deleteById(signMacro.getVideoFileId());
        signMacroRepo.delete(signMacro);
    }

    @Transactional
    public void updateSignMacro(long singMacroSeq, long categorySeq) {
        SignMacro signMacro = signMacroRepo.findBySeq(singMacroSeq).get();
        Category category = categoryRepo.findBySeq(categorySeq);
        signMacro.setCategory(category);
    }
}
