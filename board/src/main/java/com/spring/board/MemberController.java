package com.spring.board;

import java.net.URI;

import javax.management.relation.Role;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
	@GetMapping("/loginView")
	public ModelAndView Login() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("member/loginView");
		return mv;
	}
	
	///////////////////////////// # 로그인 (POST)
	@PostMapping("/validate")
	public String LoginPOST(HttpServletRequest request) {
		log.info("================Login[POST] /member/validate");
		log.info("================request : " + request);
		
		String username = "email";
		String password = "password";
		String accessToken = "";
		
		// access Token 받아오기
		try {
			accessToken = requestAccessToken(username, password, "http://localhost:8080/oauth/token");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		log.info("=============accessToken : " + accessToken);
		
		// 쿠키에 저장
		Cookie cookie = new Cookie("accessToken", accessToken);
		
		
		return "redirect:/posting/list";
	}

	 public String requestAccessToken(String username, String password, String oauthaurl) throws JSONException {
	        log.info("===================Request access token");
	        String token = null;       

	        String plainCreds = "devglan-client:devglan-secret";
	        byte[] plainCredsBytes = plainCreds.getBytes();
	        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	        String base64Creds = new String(base64CredsBytes);
	        
	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();	        
	        headers.add("Authorization", "Basic " + base64Creds);
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        //HttpURLConnection

	        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(oauthaurl)
	                                                           .queryParam("grant_type", "password")
	                                                           .queryParam("username", username)
	                                                           .queryParam("password", password);

	        URI myUri=builder.buildAndExpand().toUri();
	        log.info("========================myUri"+myUri);
	        HttpEntity<?> request = new HttpEntity<>(headers);
	        log.info("========================request"+request);
	        ResponseEntity<String> rs = restTemplate.exchange(myUri, HttpMethod.POST, request,String.class);
	        log.info("========================rs"+rs);
	        JSONObject jsonObject = new JSONObject(rs.getBody());
	        log.info("========================access_token : "+jsonObject.getString("access_token"));

	        token = jsonObject.getString("access_token");
	        //get access_token from jsonObject here

	        return token;
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
