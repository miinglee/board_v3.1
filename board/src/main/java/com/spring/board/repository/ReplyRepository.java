package com.spring.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.spring.board.entity.Posting;
import com.spring.board.entity.Reply;

public interface ReplyRepository extends CrudRepository<Reply, Long> {

	public Page<Reply> findByPostingOrderByParentnoDescGrouporderAsc(Posting posting, Pageable page);
	
	 // 해당 글의 댓글 리스트 
	 @Query("SELECT r FROM Reply r WHERE r.posting = ?1 ORDER BY r.parentno DESC, r.grouporder ASC")
	 public List<Reply> getRepliesOfPosting(Posting posting);
	
	 // 해당 댓글의 댓글을 작성하는 경우, 기존 데이터의 groupOrder 값 ++ 갱신
	 @Modifying
	 @Query("UPDATE Reply r SET r.grouporder = r.grouporder + 1 "
	 		+ "WHERE r.parentno = :parentno AND r.grouporder >= :grouporder")
	public void updateGroupOrder(@Param("parentno")int parentno, @Param("grouporder")int grouporder);
	
	 // reply 테이블의 시퀀스 값 가져오기
	 @Query("SELECT MAX(r.rno) FROM Reply r")
	public Object getMaxSeq();
	
	 // 해당 댓글의 댓글들 삭제
	 @Transactional
	 @Modifying
	 @Query("DELETE FROM Reply r WHERE r.posting = ?1 AND r.parentno =?2 AND r.grouporder > ?3 AND r.grouplayer > ?4")
	public void deleteReplies(Posting posting, int parentno, int grouporder, int grouplayer);
	
	 // 해당 글의 댓글 갯수 갱신
	 @Modifying
	 @Query("UPDATE Posting p SET p.replycnt = p.replycnt + :amount "
	 		+ "WHERE p.pno = :pno")
	public void updateReplyCount(@Param("pno")Long pno, @Param("amount")int amount);
	
	// 해당 댓글에 대댓글이 있는지 체크
	 @Query("SELECT count(r) FROM Reply r WHERE r.posting = ?1 AND r.parentno =?2 AND r.grouplayer > ?3")
	public int checkReplies(Posting posting, int parentno, int grouplayer);


}