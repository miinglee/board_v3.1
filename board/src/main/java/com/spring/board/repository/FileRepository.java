package com.spring.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.spring.board.entity.ImgFile;

public interface FileRepository extends CrudRepository<ImgFile,Long>{

	public Optional<ImgFile> findByPno(Long pno);

	// 파일 삭제
	@Transactional
	@Modifying
	public void deleteByPno(Long pno);
}