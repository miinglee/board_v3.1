package com.spring.board;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.activation.CommandMap;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.board.entity.ImgFile;
import com.spring.board.entity.Posting;
import com.spring.board.repository.FileRepository;
import com.spring.board.repository.PostingRepository;

import com.spring.board.vo.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@RequiredArgsConstructor
@Controller
@RequestMapping("/posting/")
@Log
public class BoardController {

	
	// S3 사용하여 이미지 업로드 하는 경우
	// private final S3Uploader s3Uploader;
	@Autowired
	private PostingRepository postingRepository;

	@Autowired
	private FileRepository fileRepository;

	///////////////////////////// # 게시판 리스트
	@GetMapping("/list")
	public void list(@ModelAttribute("pageVO") PagingVO vo, Model model) {
		
		  Pageable page = vo.makePageable(0, "pno"); 
		  Page<Posting> result = postingRepository.findAllByOrderByParentNoDescGroupOrderAsc(page);
		  		
		  log.info(""+ page); log.info(""+result);
		  
		  log.info("TOTAL PAGE NUMBER: " + result.getTotalPages());
		  model.addAttribute("result", new PagingMaker(result));
		 
		  List<Posting> list = result.getContent();
		  model.addAttribute("list", list);
		  
		  int maxseq = postingRepository.getMaxSeq();
		  model.addAttribute("maxseq", maxseq);		  
	}
	
	///////////////////////////// # 메인 글 작성 (GET)
	@GetMapping("/write")
	public void registerGET(@ModelAttribute("vo") Posting vo, @ModelAttribute("flag")String flag, @ModelAttribute("pno")String pno, 
			@ModelAttribute("parentNo")String parentNo, @ModelAttribute("groupOrder")String groupOrder, @ModelAttribute("groupLayer")String groupLayer, Model model){
		
		log.info("write get");
		vo.setWriter("작성자");
		log.info("flag:" + flag);
		  
		  if(flag=="ADDWRITE") {
		  
		  log.info("pno :" + (pno));
		  log.info("pno :" + (parentNo));
		  log.info("pno :" + (groupOrder));
		  log.info("pno :" + (groupLayer));
		  
		  model.addAttribute("vo.pno", pno); 
		  model.addAttribute("vo.parentNo", parentNo);
		  model.addAttribute("vo.groupOrder", groupOrder);
		  model.addAttribute("vo.groupLayer", groupLayer);
		  }

		  model.addAttribute("flag", flag);
		  int maxseq = postingRepository.getMaxSeq();
		  model.addAttribute("maxseq", maxseq);		
	}
	
	///////////////////////////// # 메인 글 작성 (POST)
	@PostMapping("/write/{maxseq}")
	public String registerPOST(@PathVariable("maxseq")int maxseq, RedirectAttributes redirectAttrs, MultipartHttpServletRequest request) throws Exception{
		
		log.info("write post");
		log.info("request : " + request);
		
		// DB에 데이터 저장 (Table=post)
		Posting posting = new Posting();
		posting.setParentNo(maxseq + 1);
		posting.setGroupOrder(0);
		posting.setGroupLayer(0);
		posting.setReplycnt(0);
		posting.setTitle(request.getParameter("title"));
		posting.setContent(request.getParameter("content"));
		posting.setWriter(request.getParameter("writer"));
		postingRepository.save(posting);		
		log.info("posting : " + posting);
		
		/***[이미지 업로드]***/
		
		// file 받아오기
		MultipartFile mf = request.getFile("files");
		log.info("mf : " + mf );
		if(!mf.getOriginalFilename().equals("")) {
			// 파일 업로드
			UploadImage.upload(mf);		
	
			// DB에 데이터 저장 (Table=file)
			ImgFile imgFile = new ImgFile(); 
			imgFile.setFname(mf.getOriginalFilename());
			imgFile.setRno(0L); 
			imgFile.setPno(posting.getPno());		
			fileRepository.save(imgFile);
			log.info("imgFile : " + imgFile);
		}
		return "redirect:/posting/list";
	}	
	
	///////////////////////////// # 답글 작성 (POST)
	@Transactional
	@PostMapping("/addWrite")
	public String addRegisterPOST(MultipartHttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception{
		
		log.info("write post");
		log.info("request [pno] : " + request.getParameter("pno"));

		Posting posting = new Posting();
		posting.setReplycnt(0);
		posting.setParentNo(Integer.parseInt(request.getParameter("parentNo")));
		posting.setGroupOrder(Integer.parseInt(request.getParameter("groupOrder"))+1);
		posting.setGroupLayer(Integer.parseInt(request.getParameter("groupLayer"))+1);
		posting.setContent(request.getParameter("content"));
		posting.setTitle(request.getParameter("title"));
		posting.setWriter(request.getParameter("writer"));
				
		// 답글에 답글 쓰는 경우 : groupOrder++ 수정
		postingRepository.updateGroupOrder(posting.getParentNo(), posting.getGroupOrder());
		
		// 답글 데이터 저장
		postingRepository.save(posting);
		
		redirectAttrs.addFlashAttribute("msg", "success");
		
		return "redirect:/posting/list";
	}	
	
	///////////////////////////// # 메인 상세 페이지 (GET)
	@GetMapping("/view")
	public void view(Long pno, @ModelAttribute("pageVO") PagingVO vo, Model model){
		
		log.info("pno: "+ pno);
		
		// Posting Data 가져오기
		postingRepository.findById(pno).ifPresent(board -> model.addAttribute("vo", board));
		
		// file Data 가져오기
		fileRepository.findByPno(pno).ifPresent(fileList -> model.addAttribute("fileList", fileList));		
	}
	
	///////////////////////////// # 메인 글 수정 (GET)
	@GetMapping("/modify")
	public void modify(Long pno, @ModelAttribute("pageVO") PagingVO vo, Model model){
		
		log.info("Modify (GET) pno : "+ pno);
		
		// Posting Data 가져오기
		postingRepository.findById(pno).ifPresent(board -> model.addAttribute("vo", board));
		
		// file Data 가져오기
		fileRepository.findByPno(pno).ifPresent(fileList -> model.addAttribute("fileList", fileList));
	}
	
	///////////////////////////// # 메인 글 수정 (POST)
	@PostMapping("/modify")
	public String modifyPost(PagingVO vo, RedirectAttributes redirectAttrs, MultipartHttpServletRequest request) throws Exception{
		
		log.info("Modify (POST) request : " + request);
		
		// 수정된 데이터 저장 (Table=post)
		Posting posting = new Posting();
		posting.setPno(Long.parseLong(request.getParameter("pno")));
		posting.setTitle(request.getParameter("title"));
		posting.setContent(request.getParameter("content"));

		
		postingRepository.findById(posting.getPno()).ifPresent(data ->{	
			data.setTitle(posting.getTitle()); 
			data.setContent(posting.getContent());
		  
			postingRepository.save(data); 
			//redirectAttrs.addFlashAttribute("msg", "success"); 
			redirectAttrs.addAttribute("pno", posting.getPno());
			data.setTitle(posting.getTitle()); 
			data.setContent(posting.getContent()); 
		 });

		// 수정된 이미지 업로드
		log.info("request : " + request.getFile("files"));
		
		// 이전 이미지 있는 경우 삭제
		fileRepository.findByPno(posting.getPno()).ifPresent(data ->{
			log.info("Delete file");
			fileRepository.deleteByPno(posting.getPno());	
		});
		
		// file 받아오기
		MultipartFile mf = request.getFile("files");
		log.info("mf : " + mf );
		
		if(!mf.getOriginalFilename().equals("")) {
			// 파일 업로드		
			UploadImage.upload(mf);				
			
			// DB에 데이터 저장 (Table=file)
			ImgFile imgFile = new ImgFile(); 
			imgFile.setFname(mf.getOriginalFilename());
			imgFile.setRno(0L); 
			imgFile.setPno(posting.getPno());		
		
			fileRepository.save(imgFile);
			log.info("imgFile : " + imgFile);		
		}
		
		// 페이징했던 결과로 이동 
		redirectAttrs.addAttribute("page", vo.getPage());
		redirectAttrs.addAttribute("size", vo.getSize());

		return "redirect:/posting/view";
	}
	
	///////////////////////////// # 메인 글 삭제 (POST)
	@PostMapping("/delete")
	public String delete(PagingVO vo, RedirectAttributes redirectAttrs, MultipartHttpServletRequest request){
		
		log.info("DELETE pno: " + request.getParameter("pno"));
		log.info("DELETE [groupOrder] : " + request.getParameter("groupOrder"));
		log.info("DELETE [groupLayer] : " + request.getParameter("groupLayer"));
		log.info("DELETE [parentNo] : " + request.getParameter("parentNo"));
		
		// 답글이 있는 경우 삭제 불가
		if(postingRepository.checkReplies(Integer.parseInt(request.getParameter("parentNo")), Integer.parseInt(request.getParameter("groupLayer")))>0){
			redirectAttrs.addFlashAttribute("msg", "fail");
		}else {
			// 메인 글의 댓글 삭제
			postingRepository.deleteComments(Integer.parseInt(request.getParameter("parentNo")), 
					Integer.parseInt(request.getParameter("groupOrder")), Integer.parseInt(request.getParameter("groupLayer")));
			
			// 메인 글 삭제
			postingRepository.deleteById(Long.parseLong(request.getParameter("pno")));
			
			redirectAttrs.addFlashAttribute("msg", "success");
		}
		
		// 메인 글의 답글 삭제
//		postingRepository.deleteReplies(Integer.parseInt(request.getParameter("parentNo")), 
//				Integer.parseInt(request.getParameter("groupOrder")), Integer.parseInt(request.getParameter("groupLayer")));				
		
		// 페이징했던 결과로 이동
		redirectAttrs.addAttribute("page", vo.getPage());
		redirectAttrs.addAttribute("size", vo.getSize());

		return "redirect:/posting/list";
	}		

}