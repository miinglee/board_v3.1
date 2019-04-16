package com.spring.board.service;

import org.springframework.stereotype.Service;


public interface AccessTokenService {
	String requestAccessToken(String username, String password, String oauthaurl);
	String checkAccessToken(String token);
}
