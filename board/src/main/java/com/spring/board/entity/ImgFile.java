package com.spring.board.entity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "file")
public class ImgFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fno;

	private Long pno ;

	private Long rno;

	private String fname;

}