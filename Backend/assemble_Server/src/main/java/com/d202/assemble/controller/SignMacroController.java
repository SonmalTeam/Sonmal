package com.d202.assemble.controller;

import com.d202.assemble.dto.*;
import com.d202.assemble.repo.VideoFileRepo;
import com.d202.assemble.service.FireBaseService;
import com.d202.assemble.service.SignMacroService;
import com.d202.assemble.service.VideoFileService;
import com.d202.assemble.utils.MD5Generator;
import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
@RequestMapping("/sign/macro")
@RequiredArgsConstructor
public class SignMacroController {

    private final SignMacroService signMacroService;
    private final VideoFileService videoFileService;
    private final VideoFileRepo videoFileRepo;
    private final FireBaseService fireBaseService;

    // 매크로 등록
    @ApiOperation(value = "매크로 등록")
    @PostMapping
    public void createSignMacro(@ApiIgnore Authentication auth, @RequestParam("file") MultipartFile file, SignMacroRequestDto request){
        long userSeq = (int)auth.getPrincipal();
        signMacroService.createSignMacro(userSeq, request, file);
    }

    // 비디오 없는 매크로 등록
    @ApiOperation(value = "Null Video 매크로 등록")
    @PostMapping("/videoNull")
    public void createSignMacroVideoNull(@ApiIgnore Authentication auth, SignMacroVideoNullDto request){
        long userSeq = (int)auth.getPrincipal();
        signMacroService.createSignMacroVideoNull(userSeq, request);
    }

    // 매크로 상세조회
    @ApiOperation(value = "매크로 상세조회")
    @GetMapping("/{signMacroSeq}")
    public SignMacroResponseDto getSignMacro(@ApiIgnore Authentication auth, @PathVariable Long signMacroSeq){
        long userSeq = (int)auth.getPrincipal();
        return signMacroService.getSignMacro(userSeq, signMacroSeq);
    }

    // 매크로 리스트 조회
    @ApiOperation(value = "매크로 리스트 조회")
    @GetMapping("/category/{categorySeq}")
    public ResponseEntity<?> getSignMacroList(@ApiIgnore Authentication auth, @PathVariable Long categorySeq, @PageableDefault Pageable pageable){
        long userSeq = (int)auth.getPrincipal();

        PagingResult result = signMacroService.getSignMacroList(pageable, userSeq, categorySeq);

        return new ResponseEntity<PagingResult<SignMacroResponseDto>>(result, HttpStatus.OK);
    }

    // 매크로 사용 순 정렬
    @ApiOperation(value = "매크로 사용 순 정렬")
    @GetMapping("/sort/{categorySeq}")
    public ResponseEntity<?> sortSignMacroList(@ApiIgnore Authentication auth, @PathVariable Long categorySeq, @PageableDefault Pageable pageable){
        long userSeq = (int)auth.getPrincipal();

        PagingResult result = signMacroService.sortSignMacroList(pageable, userSeq, categorySeq);

        return new ResponseEntity<PagingResult<SignMacroResponseDto>>(result, HttpStatus.OK);
    }

    // 매크로 사용횟수 카운트
    @ApiOperation(value = "매크로 사용횟수 카운트")
    @PutMapping("/count/{signMacroSeq}")
    public void countSignMacro(@ApiIgnore Authentication auth, @PathVariable Long signMacroSeq){
        long userSeq = (int)auth.getPrincipal();
        signMacroService.countSignMacro(userSeq, signMacroSeq);
    }

    // 매크로 삭제
    @ApiOperation(value = "매크로 삭제")
    @ApiImplicitParam(name = "signMacroSeq", value = "매크로 PK", example = "1", required = true)
    @DeleteMapping("/{signMacroSeq}")
    public void deleteSignMacro(@PathVariable Long signMacroSeq) {

        signMacroService.deleteSignMacro(signMacroSeq);
    }

    // 매크로 분류 수정
    @ApiOperation(value = "매크로 분류 수정")
    @ApiImplicitParam(name = "signMacroSeq", value = "매크로 PK", example = "1", required = true)
    @PutMapping("/{signMacroSeq}")
    public void updateSignMacro(@PathVariable Long signMacroSeq, @RequestParam Long categorySeq) {
        
        signMacroService.updateSignMacro(signMacroSeq, categorySeq);
    }

    // 매크로 동영상 다운로드
    @ApiOperation(value = "매크로 동영상 다운로드")
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {
        VideoFileDto videoFileDto = videoFileService.getFile(fileId);
        Path path = Paths.get(videoFileDto.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + videoFileDto.getOrigFilename() + "\"")
                .body(resource);
    }

    // 동영상 재생
    @ApiOperation(value = "매크로 동영상 재생")
    @RequestMapping(value = "/video/{videoFileId}", method = RequestMethod.GET)
    public ResponseEntity<ResourceRegion> videoRegion(@RequestHeader HttpHeaders headers, @PathVariable("videoFileId") long videoFileId) throws Exception {

        String fileName = videoFileRepo.findById(videoFileId).get().getFilename();
        String path = "/files/" + fileName + ".mp4";
        Resource resource = new FileSystemResource(path);

        long chunkSize = 1024 * 1024;
        long contentLength = resource.contentLength();

        ResourceRegion region;

        try {
            HttpRange httpRange = headers.getRange().stream().findFirst().get();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end -start + 1);

            log.info("start === {} , end == {}", start, end);

            region = new ResourceRegion(resource, start, rangeLength);
        } catch (Exception e) {
            long rangeLength = Long.min(chunkSize, contentLength);
            region = new ResourceRegion(resource, 0, rangeLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header("Accept-Ranges", "bytes")
                .eTag(path)
                .body(region);
    }

    // firebase test
    @ApiOperation(value = "파이어 베이스 업로드")
    @PostMapping("/files")
    public String uploadFile(@RequestParam("file") MultipartFile file, String nameFile)
            throws IOException, FirebaseAuthException {
        if (file.isEmpty()) {
            return "is empty";
        }
        return fireBaseService.uploadFiles(file, nameFile);
    }
}