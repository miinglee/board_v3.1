package com.spring.board.service;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.spring.board.util.HashUtil;

import lombok.extern.java.Log;

/***
 * [인증 provider]
 * 로그인시 사용자가 입력한 아이디와 비밀번호를 확인하고 해당 권한을 주는 클래스
 * 
 * @author Sunmin Lee
 *
 */
@Log
@Component("authProvider")
public class AuthProvider implements AuthenticationProvider{

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	    String id = authentication.getName();
	    String password = HashUtil.sha256(authentication.getCredentials().toString());
	    
	    log.info("///*******************************"+ authentication);
		return authentication;

	    

		
        // 로그인 성공시 로그인 사용자 정보 반환

	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return false;
	}
}
