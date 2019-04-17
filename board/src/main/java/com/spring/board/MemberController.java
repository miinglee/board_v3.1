package com.spring.board;

import java.net.URI;

import javax.management.relation.Role;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.spring.board.entity.Member;
import com.spring.board.repository.MemberRepository;
import com.spring.board.service.AccessTokenService;

import io.jsonwebtoken.Jwts;
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

    @Autowired
    private AccessTokenService accessTokenService;
    
	///////////////////////////// # 로그인 작성 (GET)
	@GetMapping("/loginView")
	public void Login() {
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName("member/loginView");
//		return mv;
	}
	
	///////////////////////////// # 로그인 (POST)
//	@PostMapping("/validate")
//	public String LoginPOST(HttpServletRequest request) {
//		log.info("================Login[POST] /member/validate");
//		log.info("================request : username " + request.getAttribute("username"));			
//		
//		String username = request.getParameter("username");
//		String password = request.getParameter("password");
//		
//
//		return "/posting/list";
//	}

	@GetMapping("/logout")
	public void logout() {
		
	}
	
	@GetMapping("/accessDenied")
	public void accessDenied() {
		
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
		member.setRole("ROLE_USER");
		//member.setPassword(request.getParameter("password"));
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.setPassword(passwordEncoder.encode(request.getParameter("password")));
		
		memberRepository.save(member);

		return "redirect:/posting/list";
	}	
}
