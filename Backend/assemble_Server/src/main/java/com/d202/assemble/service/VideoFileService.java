package com.d202.assemble.service;

import com.d202.assemble.dto.VideoFile;
import com.d202.assemble.dto.VideoFileDto;
import com.d202.assemble.repo.VideoFileRepo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class VideoFileService {
    private VideoFileRepo videoFileRepo;

    public VideoFileService(VideoFileRepo videoFileRepo) {
        this.videoFileRepo = videoFileRepo;
    }

    @Transactional
    public Long saveFile(VideoFileDto videoFileDto) {
        return videoFileRepo.save(videoFileDto.toEntity()).getId();
    }

    @Transactional
    public VideoFileDto getFile(Long id) {
        VideoFile file = videoFileRepo.findById(id).get();

        VideoFileDto videoFileDto = VideoFileDto.builder()
                                    .id(id)
                                    .origFilename(file.getOrigFilename())
                                    .filename(file.getFilename())
                                    .filePath(file.getFilePath())
                                    .build();
        return videoFileDto;
    }
}
