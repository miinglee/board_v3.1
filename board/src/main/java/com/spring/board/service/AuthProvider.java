package com.spring.board.service;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.spring.board.util.HashUtil;

/***
 * [인증 provider]
 * 로그인시 사용자가 입력한 아이디와 비밀번호를 확인하고 해당 권한을 주는 클래스
 * 
 * @author Sunmin Lee
 *
 */
@Component("authProvider")
public class AuthProvider implements AuthenticationProvider{

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	    String id = authentication.getName();
	    String password = HashUtil.sha256(authentication.getCredentials().toString());
	

		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return false;
	}


}
