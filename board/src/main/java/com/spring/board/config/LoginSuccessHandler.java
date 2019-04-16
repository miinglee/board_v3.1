package com.spring.board.config;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.spring.board.service.AccessTokenService;

import lombok.extern.java.Log;


@Log
@Configuration
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
    @Autowired
    private AccessTokenService accessTokenService;
    
	@Override
	 public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		// Authentication 객체 : 계정 관련 정보
		
		log.info("================LoginSuccessHandler authentication : " + authentication.getPrincipal());
		
		String username = request.getParameter("email");
		String password = request.getParameter("password");
		log.info("================LoginSuccessHandler username : " + username);
		log.info("================LoginSuccessHandler password : " + password);
		  
		// access Token 받아오기	
	   String accessToken ="";		  	
	   accessToken = accessTokenService.requestAccessToken(username, password, "http://localhost:8080/oauth/token");
	  
	   // 쿠키에 저장
	   Cookie token = new Cookie("accessToken", accessToken);
	   token.setPath("/");
	   response.addCookie(token);	
	  
	   Cookie UserID = new Cookie("UserID", username);
	   UserID.setPath("/");
	   response.addCookie(UserID);
	   
	   response.sendRedirect("/posting/list");		
	}		
	
}