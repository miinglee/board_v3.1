package com.spring.board.interceptor;

import java.net.URI;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UriComponentsBuilder;

import com.spring.board.service.AccessTokenService;
import com.spring.board.service.AccessTokenServiceImpl;

import lombok.extern.java.Log;

@Log
@Component
public class TokenCheckInterceptor extends HandlerInterceptorAdapter{
    
    @Autowired
    private AccessTokenService accessTokenService;
    
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info(".................................preHandle.................................");
		// oauth/check_token 검사
		
		// access Token 받아오기	
		
	    String token = "";
	    
	   
		Cookie Cookie[] =  request.getCookies();
		for(int i=0; i<Cookie.length; i++) {
			Cookie c = Cookie[i];
			if(c.getName().equals("accessToken")) {
				token = c.getValue();
				log.info("----------------------------"+token);
				break;
			}
		}
		if(token.equals("")) {
			throw new InvalidTokenException("토큰이 없습니다.");
		}
		
		// 유효하지 않으면, 로그인 페이지로 redirect
		String result = accessTokenService.checkAccessToken(token);		
		
		if(result.equals("")) {
			response.sendRedirect("/member/loginView");
		}
		
		return true;
	}
	
}