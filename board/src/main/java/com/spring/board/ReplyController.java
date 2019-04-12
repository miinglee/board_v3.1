package com.spring.board;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.entity.Posting;
import com.spring.board.entity.Reply;
import com.spring.board.repository.PostingRepository;
import com.spring.board.repository.ReplyRepository;
import com.spring.board.vo.PagingMaker;
import com.spring.board.vo.PagingVO;

import lombok.extern.java.Log;

@RestController
@Log
public class ReplyController {

	/***
	 * ResponseEntity : Http Response의 상태 코드와 데이터를 직접 제어해서 처리할 수 있다는 장점이 있음.
	 * @RequestBody : JSON으로 전달되는 데이터를 객체로 자동으로 변환하도록 처리하는 역할
	 */
	
	@Autowired
	private ReplyRepository replyRepo;
		
	///////////////////////////// # 댓글 리스트
	@RequestMapping(value ="/replies/{pno}", method=RequestMethod.GET)	
	public ResponseEntity<List<Reply>> getReplies(@PathVariable("pno")Long pno, @ModelAttribute("pageVO") PagingVO vo){

		log.info(pno.toString());		
		Posting posting = new Posting(); posting.setPno(pno); 
		return new ResponseEntity<>(getListByPosting(posting),HttpStatus.OK);		 			  
	}
	
	///////////////////////////// # 페이징 처리
	@RequestMapping(value ="/replies/paging/{pno}", method=RequestMethod.GET)	
	public PagingMaker<Reply> getPaging(@PathVariable("pno")Long pno, @ModelAttribute("pageVO") PagingVO vo
			, Model model){

		Posting posting = new Posting();
		posting.setPno(pno);
		
		  Pageable page = vo.makePageable(0, "rno"); 
		  Page<Reply> result = replyRepo.findByPostingOrderByParentnoDescGrouporderAsc(posting, page);
		  
		  log.info(""+ page); log.info(""+result);		  
		  log.info("TOTAL PAGE NUMBER: " + result.getTotalPages());	
		  
		  log.info("getPaging...");
		  return new PagingMaker<>(result);  
	}
	
	///////////////////////////// # Max 시퀀스 가져오기	
	@RequestMapping(value ="/replies/getMaxSeq/{pno}", method=RequestMethod.GET)	
	public ResponseEntity<Integer> getMaxSeq(@PathVariable("pno")Long pno){
		log.info(pno.toString());
		log.info("[controller] get Max Seq..........................");
		
		Posting posting = new Posting();
		posting.setPno(pno);
		
		Object tmpseq =  replyRepo.getMaxSeq();
		int maxseq = 0;
		maxseq = tmpseq == null ? 1 : Integer.parseInt(tmpseq.toString())+1;
		
		return new ResponseEntity<>(maxseq,HttpStatus.OK);
	}
	
	///////////////////////////// # 댓글 작성 (POST)	
	@Transactional
	@RequestMapping(value="/replies/add/{pno}", method=RequestMethod.POST)
	public ResponseEntity<List<Reply>> add(@PathVariable("pno")Long pno, @RequestBody Reply reply){

		log.info("//////////////////////rno:"+reply.getRno()+" parent:"+reply.getParentno() +" layer:"+reply.getGrouplayer()+" order:"+reply.getGrouporder());
		log.info("PNO: " + pno);
		log.info("REPLY: " + reply);
		
		Posting posting = new Posting();
		posting.setPno(pno);
		reply.setPosting(posting);		
		replyRepo.save(reply);
		
		// 댓글 갯수 갱신
		replyRepo.updateReplyCount(pno, 1);
		
		return new ResponseEntity<>(getListByPosting(posting), HttpStatus.CREATED);		
	}
	
	///////////////////////////// # 댓글의 댓글 작성 (POST)	
	@Transactional
	@RequestMapping(value="/replies/addReply/{pno}", method=RequestMethod.POST)
	public ResponseEntity<List<Reply>> addReply(@PathVariable("pno")Long pno, @RequestBody Reply reply){

		log.info("//////////////////////rno:"+reply.getRno()+" parent:"+reply.getParentno() +" layer:"+reply.getGrouplayer()+" order:"+reply.getGrouporder());
		log.info("PNO: " + pno);
		log.info("REPLY: " + reply);
		
		Posting posting = new Posting();
		posting.setPno(pno);

		reply.setPosting(posting);

		// 답글에 답글쓰는 경우 : grouporder 갱신
		replyRepo.updateGroupOrder(reply.getParentno(), reply.getGrouporder());
		replyRepo.save(reply);
		// 댓글 갯수 갱신
		replyRepo.updateReplyCount(pno, 1);
		
		return new ResponseEntity<>(getListByPosting(posting), HttpStatus.CREATED);		
	}

	///////////////////////////// # 댓글 삭제	
	@RequestMapping(value ="/replies/{pno}/{rno}", method=RequestMethod.DELETE)	
	@Transactional
	public ResponseEntity<List<Reply>> remove(@PathVariable("pno")Long pno, @PathVariable("rno")Long rno, @RequestBody Reply reply){
		
		log.info("delete reply: ");


		Posting posting = new Posting();
		posting.setPno(pno);
		
		//replyRepo.deleteReplies(posting, reply.getParentno(), reply.getGrouporder(), reply.getGrouplayer());
		
		// 대댓글이 있는 경우 삭제 불가
		if(replyRepo.checkReplies(posting, reply.getParentno(), reply.getGrouplayer())>0){
			return new ResponseEntity<>(getListByPosting(posting), HttpStatus.BAD_REQUEST);	
		}else {
			// 해당 댓글 1개 삭제
			replyRepo.deleteById(rno);
			log.info("delete reply: 해당 데이터 1개 삭제");
			
			// 댓글 갯수 갱신
			replyRepo.updateReplyCount(pno, -1);
		}	
		
		
		return new ResponseEntity<>(getListByPosting(posting), HttpStatus.OK);		
	}
	
	///////////////////////////// # 댓글 수정	
	@RequestMapping(value ="/replies/{pno}", method=RequestMethod.PUT)	
	@Transactional
	public ResponseEntity<List<Reply>> modify(@PathVariable("pno")Long pno, @RequestBody Reply reply){
		
	
		log.info("modify reply: "+ reply);	
		
		 replyRepo.findById(reply.getRno()).ifPresent(data ->{			 
			 data.setReplytext(reply.getReplytext()); 
			 data.setReplyer(reply.getReplyer());		  
			 replyRepo.save(data); 
		 });
		 		
		Posting posting = new Posting();
		posting.setPno(pno);
		
		return new ResponseEntity<>(getListByPosting(posting), HttpStatus.OK);
	}
	
	///////////////////////////// # 댓글 리스트	
	private List<Reply> getListByPosting(Posting posting)throws RuntimeException{
		
		log.info("getListByPosting...." + posting.getPno());
		
		return replyRepo.getRepliesOfPosting(posting);
	}
}

