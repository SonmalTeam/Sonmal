package com.d202.assemble.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VideoFileDto {
    private Long id;
    private String origFilename;
    private String filename;
    private String filePath;

    public VideoFile toEntity() {
        VideoFile build = VideoFile.builder()
                .id(id)
                .origFilename(origFilename)
                .filename(filename)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public VideoFileDto(Long id, String origFilename, String filename, String filePath) {
        this.id = id;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
    }
}
