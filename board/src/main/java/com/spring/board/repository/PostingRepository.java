package com.spring.board.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import com.spring.board.entity.Posting;

public interface PostingRepository extends CrudRepository<Posting,Long>{

	public List<Posting> findAllByOrderByPnoDesc();
	public Optional<Posting> findByPno(Long pno);
	public Page<Posting> findAll(Pageable page);
	public Page<Posting> findAllByOrderByParentNoDescGroupOrderAsc(Pageable page);
	public Posting findFirstByOrderByCreateDateDesc();
	
	// Post 테이블의 시퀀스 값 가져오기
	@Query("SELECT COUNT(*) FROM Posting")
	public int getMaxSeq();
	
	// 답글 생성 시, 기존 데이터의 groupOrder 변수 값 ++
	@Modifying
	@Query("UPDATE Posting p SET p.groupOrder = p.groupOrder + 1 "
	 		+ "WHERE p.parentNo = :parentno AND p.groupOrder >= :grouporder")
	public void updateGroupOrder(@Param("parentno")int parentno, @Param("grouporder")int grouporder);
	 
	// 해당 글의 답글 삭제
	@Transactional // method 시작 시, 하나의 Transaction을 생성하고 return 후 commit 되도록 설정함.
	@Modifying
	@Query("DELETE FROM Posting p WHERE p.parentNo =?1 AND p.groupOrder > ?2 AND p.groupLayer > ?3")
	public void deleteReplies(Integer parentNo, Integer groupOrder, Integer groupLayer);
	 
	// 해당 글의 댓글 삭제
	@Transactional
	@Modifying
	@Query("DELETE FROM Reply r WHERE r.posting IN (SELECT p.pno FROM Posting p WHERE p.parentNo =?1 AND p.groupOrder =?2 AND p.groupLayer =?3)")
	public void deleteComments(Integer parentNo, Integer groupOrder, Integer groupLayer);

	// 해당 글에 답글이 존재하는지 체크
	@Query("SELECT count(p) FROM Posting p WHERE p.parentNo =?1 AND p.groupLayer > ?2")
	public int checkReplies(@Param("parentno")int parentno, @Param("grouporder")int grouporder);

}