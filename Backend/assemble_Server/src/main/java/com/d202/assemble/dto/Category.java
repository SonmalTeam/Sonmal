package com.d202.assemble.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(length = 20)
    private String name;
}
