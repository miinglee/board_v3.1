package com.spring.board.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "reply")
//@EqualsAndHashCode(of = "rno") // hascode와 equals를 생성해줌. of (포함시킬 변수명)
@ToString(exclude = "posting")
public class Reply {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rno;

	private String replytext;

	private String replyer;

	private int parentno;
	
	private int grouporder;
	
	private int grouplayer;
	
	
	@CreationTimestamp
	private Timestamp createdate;
	@UpdateTimestamp
	private Timestamp updatedate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Posting posting;
}
