package com.d202.assemble.service;

import com.d202.assemble.dto.*;
import com.d202.assemble.repo.CategoryRepo;
import com.d202.assemble.repo.SignMacroRepo;
import com.d202.assemble.repo.VideoFileRepo;
import com.d202.assemble.utils.MD5Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private final VideoFileService videoFileService;

    @Value("${resource.path}")
    private String resourcePath;
    private final String uploadURL = "/home/ubuntu/files/";

    // video 매크로 등록
    @Transactional
    public void createSignMacro(Long userSeq, SignMacroRequestDto request, MultipartFile file){
        try {
            String origFilename = file.getOriginalFilename();
            String filename = new MD5Generator(origFilename).toString();
//            String savePath = resourcePath;

//            if (!new File(savePath).exists()) {
//                try{
//                    new File(savePath).mkdir();
//                }
//                catch(Exception e){
//                    e.getStackTrace();
//                }
//            }

            String filePathSt = resourcePath + filename + ".mp4";
            Path filePath = Paths.get(filePathSt);
            try {
                file.transferTo(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Path fileResource = Paths.get(resourcePath + filename + ".mp4");
//            try {
//                Files.copy(file.getInputStream(), fileResource, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new RuntimeException("Could not store file : " + file.getOriginalFilename());
//            }

            VideoFileDto videoFileDto = new VideoFileDto();
            videoFileDto.setOrigFilename(origFilename);
            videoFileDto.setFilename(filename);
            videoFileDto.setFilePath(filePathSt);

            Long videoFileId = videoFileService.saveFile(videoFileDto);
            request.setVideoFileId(videoFileId);
        } catch(Exception e) {
            e.printStackTrace();
        }
        SignMacro signMacro = request.toEntity();

        Category category = categoryRepo.findBySeq(request.getCategorySeq());
        signMacro.setCategory(category);
        signMacro.setUserSeq(userSeq);
        signMacro.setRegDttm(LocalDateTime.now());
        signMacro.setCount(new Long(0));
        signMacroRepo.save(signMacro);
    }

    // 비디오 재생
    public String videoRegion(long videoFileId) {
        String fileName = videoFileRepo.findById(videoFileId).get().getFilename();
        String path = resourcePath + fileName + ".mp4";

        return path;
    }

    // 매크로 등록
    public void createSignMacroVideoNull(Long userSeq, SignMacroVideoNullDto request){
        SignMacro signMacro = request.toEntity();

        Category category = categoryRepo.findBySeq(request.getCategorySeq());
        signMacro.setCategory(category);
        signMacro.setUserSeq(userSeq);
        signMacro.setRegDttm(LocalDateTime.now());
        signMacro.setCount(new Long(0));
        signMacroRepo.save(signMacro);
    }

    // 매크로 상세 조회
    @Transactional
    public SignMacroResponseDto getSignMacro(long userSeq, long signMacroSeq) {

        SignMacro signMacro = signMacroRepo.findBySeq(signMacroSeq).get();
        SignMacroResponseDto result = new SignMacroResponseDto(signMacro);

        return result;
    }

    // 매크로 리스트 조회
    @Transactional
    public PagingResult<SignMacroResponseDto> getSignMacroList(Pageable pageable, long userSeq, long categorySeq) {

        Page<SignMacro> signMacroPage = signMacroRepo.findAllByUserSeqAndCategorySeq(userSeq, categorySeq, pageable);
        List<SignMacroResponseDto> signMacroList = new ArrayList<>();

        for (SignMacro signMacro : signMacroPage) {
            signMacroList.add(new SignMacroResponseDto(signMacro));
        }

        PagingResult result = new PagingResult<SignMacroResponseDto>(pageable.getPageNumber(), signMacroPage.getTotalPages() - 1, signMacroList);
        return result;
    }

    // 매크로 사용 순 정렬
    @Transactional
    public PagingResult<SignMacroResponseDto> sortSignMacroList(Pageable pageable, long userSeq, long categorySeq) {

        Page<SignMacro> signMacroPage = signMacroRepo.findAllByUserSeqAndCategorySeqOrderByCountDesc(userSeq, categorySeq, pageable);
        List<SignMacroResponseDto> signMacroList = new ArrayList<>();

        for (SignMacro signMacro : signMacroPage) {
            signMacroList.add(new SignMacroResponseDto(signMacro));
        }

        PagingResult result = new PagingResult<SignMacroResponseDto>(pageable.getPageNumber(), signMacroPage.getTotalPages() - 1, signMacroList);
        return result;
    }

    // 매크로 사용횟수 카운트
    @Transactional
    public void countSignMacro(long userSeq, long signMacroSeq) {

        SignMacro signMacro = signMacroRepo.findBySeq(signMacroSeq).get();
        signMacro.setCount(signMacro.getCount() + 1);

    }

    // 매크로 삭제
    @Transactional
    public void deleteSignMacro(long signMacroSeq) {
        SignMacro signMacro = signMacroRepo.findBySeq(signMacroSeq).get();
        videoFileRepo.deleteById(signMacro.getVideoFileId());
        signMacroRepo.delete(signMacro);
    }

    // 매크로 분류 수정
    @Transactional
    public void updateSignMacro(long singMacroSeq, long categorySeq) {
        SignMacro signMacro = signMacroRepo.findBySeq(singMacroSeq).get();
        Category category = categoryRepo.findBySeq(categorySeq);
        signMacro.setCategory(category);
    }
}
