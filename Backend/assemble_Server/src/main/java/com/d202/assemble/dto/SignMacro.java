package com.d202.assemble.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sign_macro")
public class SignMacro {

    // PK, AI
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    // 유저 FK
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userSeq")
//    private User user;

    // 카테고리 FK
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "categorySeq")
//    private Category category;

    // 수어 영상주소
    @Lob
    @Column(name = "sign_src")
    private String signScr;

    // 썸네일


    // 아이콘


    // 번역된 텍스트
    @Lob
    @Column(name = "content")
    private String content;

    // 매크로 사용횟수
    @Lob
    @Column(name = "count")
    private Long count;

    // 등록시간
    @CreatedDate
    @Column(name = "reg_dttm")
    private LocalDateTime regDttm;
}
