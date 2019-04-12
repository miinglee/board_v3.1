package com.spring.board.entity;

import lombok.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter @Setter
@Entity
@Table(name = "post")
@ToString(exclude="replies") // 참조하는 객체를 출력하지 않도록
public class Posting {
	@Id
	@Column(name = "pno")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pno;
	
	@Column(name = "writer")
	private String writer;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "content")
	private String content;
	
	@CreationTimestamp	
	@Column(name = "createDate")
	private Timestamp  createDate;
	
	@UpdateTimestamp
	@Column(name = "updateDate")
	private Timestamp updateDate;
	
	@Column(name = "parentNo")
	private Integer parentNo;
	
	@Column(name = "groupOrder")
	private Integer groupOrder;
	
	@Column(name = "groupLayer")
	private Integer groupLayer;
	
	@Column(name = "file")
	private String file;
	
	@Column(name = "replycnt")
	private int replycnt;	

	@JsonIgnore // 특정한 속성은 JSON으로 변환되지 않도록
	@OneToMany(mappedBy="posting", fetch=FetchType.LAZY)
	private List<Reply> replies;
	
//	@JsonIgnore
//	@OneToMany(mappedBy="posting", fetch=FetchType.LAZY)
//	private List<ImgFile> files;
	
	public Posting() {}
}