package com.d202.assemble.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@DynamicInsert
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer seq;
	@Column(nullable = false)
	private String email; 
	@Column(columnDefinition = "boolean default false not null")
	private boolean type;
	@Enumerated(EnumType.STRING)
	@Column(name="social_type")
	private SocialType socialType;
	@Column(name="reg_time", nullable = false)
	@CreationTimestamp
	private LocalDateTime regTime;
}