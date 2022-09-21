package com.d202.assemble.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignMacroRequestDto {

    private String title;
    private String content;
    private String signSrc;
    private String icon;
    private Long categorySeq;

    private Long videoFileId;

    public SignMacro toEntity() {
        return SignMacro.builder()
                .title(title)
                .content(content)
                .signSrc(signSrc)
                .icon(icon)
                .videoFileId(videoFileId)
                .build();
    }

}
