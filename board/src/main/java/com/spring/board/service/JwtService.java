package com.spring.board.service;

import java.util.Map;



public interface JwtService {

	Map<String, Object> get(String key);
	int getMemberId();
}