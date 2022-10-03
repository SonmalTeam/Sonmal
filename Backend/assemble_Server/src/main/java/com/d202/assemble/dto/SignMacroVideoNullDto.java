package com.d202.assemble.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignMacroVideoNullDto {

    private String title;
    private String content;
    private String icon;
    private Long categorySeq;


    public SignMacro toEntity() {
        return SignMacro.builder()
                .title(title)
                .content(content)
                .icon(icon)
                .build();
    }
}
