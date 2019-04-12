package com.spring.board;

import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.entity.Member;
import com.spring.board.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member/")
@Log
public class MemberController {

	@Autowired
	MemberRepository memberRepository;
	
	///////////////////////////// # 로그인 작성 (GET)
	@GetMapping("/show")
	public ModelAndView Login() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("member/show");
		return mv;
	}
	
	///////////////////////////// # 로그인 (POST)
	@PostMapping("/validate")
	public ModelAndView LoginPOST(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/posting/list");
		return mv;
	}
	
	///////////////////////////// # 회원가입 작성 (GET)
	@GetMapping("/join")
	public ModelAndView join(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("member/join");
		return mv;
	}
	
	///////////////////////////// # 회원가입 작성 (POST)
	@PostMapping("/register")
	public String join(HttpServletRequest request){
		
		log.info("member POST : " + request);
		Member member = new Member();
		member.setName(request.getParameter("name"));
		member.setEmail(request.getParameter("email"));
		//member.setPassword(request.getParameter("password"));
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.setPassword(passwordEncoder.encode(request.getParameter("password")));
		
		memberRepository.save(member);

		return "redirect:/posting/list";
	}	
}
