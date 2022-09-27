package com.d202.assemble.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignMacroResponseDto {
    private Long seq;
    private String title;
    private String content;
    private String icon;
    private Long categorySeq;
    private Long count;

    private Long videoFileId;

    public SignMacroResponseDto(SignMacro entity) {
        this.seq = entity.getSeq();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.icon = entity.getIcon();
        this.categorySeq = entity.getCategory().getSeq();
        this.count = entity.getCount();
        this.videoFileId = entity.getVideoFileId();
    }
}
