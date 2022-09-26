package com.d202.assemble.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCategory {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    // 유저 FK
    @Column(name = "user_seq")
    private Long userSeq;

    // Category list
    @ElementCollection
    @Column(name = "category_list")
    private List<Long> categoryList;

    public UserCategory(Long userSeq, List<Long> categories){
        this.userSeq = userSeq;
        this.categoryList = categories;
    }
}
