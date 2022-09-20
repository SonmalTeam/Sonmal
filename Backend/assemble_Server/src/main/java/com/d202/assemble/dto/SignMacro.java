package com.d202.assemble.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sign_macro")
public class SignMacro {

    // PK, AI
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    // 유저 FK
    @Column(name = "user_seq")
    private Long userSeq;

    // 매크로 제목
    @Column(name = "title")
    private String title;

    // 카테고리 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorySeq")
    private Category category;

    // 수어 영상주소
    @Lob
    @Column(name = "sign_src")
    private String signSrc;

    // 아이콘
    @Lob
    @Column(name = "icon")
    private String icon;

    // 번역된 텍스트
    @Lob
    @Column(name = "content")
    private String content;

    // 매크로 사용횟수
    @Column(name = "count")
    private Long count;

    // 등록시간
    @CreatedDate
    @Column(name = "reg_dttm")
    private LocalDateTime regDttm;


    @Column
    private Long videoFileId;

    @Builder
    public SignMacro(String title, String content, String signSrc, String icon, Long videoFileId){
        this.title = title;
        this.content = content;
        this.signSrc = signSrc;
        this.icon = icon;

        this.videoFileId = videoFileId;
    }
}
