package com.d202.assemble.controller;

import com.d202.assemble.dto.SignMacroRequestDto;
import com.d202.assemble.dto.SignMacroResponseDto;
import com.d202.assemble.dto.VideoFile;
import com.d202.assemble.dto.VideoFileDto;
import com.d202.assemble.service.FireBaseService;
import com.d202.assemble.service.SignMacroService;
import com.d202.assemble.service.VideoFileService;
import com.d202.assemble.utils.MD5Generator;
import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/sign/macro")
@RequiredArgsConstructor
public class SignMacroController {

    private final SignMacroService signMacroService;
    private final VideoFileService videoFileService;
    private final FireBaseService fireBaseService;

    // test
    @PostMapping("/files")
    public String uploadFile(@RequestParam("file") MultipartFile file, String nameFile)
        throws IOException, FirebaseAuthException {
        if (file.isEmpty()) {
            return "is empty";
        }
        return fireBaseService.uploadFiles(file, nameFile);
    }

    // 매크로 등록
    @ApiOperation(value = "매크로 등록")
    @PostMapping
    public void createSignMacro(@RequestParam("file") MultipartFile file, SignMacroRequestDto request){
        try {
            String origFilename = file.getOriginalFilename();
            String filename = new MD5Generator(origFilename).toString();
            String savePath = System.getProperty("user.dir") + "\\files";
            if (!new File(savePath).exists()) {
                try{
                    new File(savePath).mkdir();
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }
            String filePath = savePath + "\\" + filename;
            file.transferTo(new File(filePath));

            VideoFileDto videoFileDto = new VideoFileDto();
            videoFileDto.setOrigFilename(origFilename);
            videoFileDto.setFilename(filename);
            videoFileDto.setFilePath(filePath);

            Long videoFileId = videoFileService.saveFile(videoFileDto);
            request.setVideoFileId(videoFileId);
            signMacroService.createSignMacro(new Long(1), request);
        } catch(Exception e) {
            e.printStackTrace();
        }
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