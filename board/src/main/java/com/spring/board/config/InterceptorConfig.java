package com.spring.board.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.spring.board.interceptor.TokenCheckInterceptor;

import lombok.extern.java.Log;

@Configuration
@Log
public class InterceptorConfig extends WebMvcConfigurerAdapter{

	@Autowired
	TokenCheckInterceptor tokenCheckInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tokenCheckInterceptor)
		.addPathPatterns("/posting/*")
		.excludePathPatterns("/posting/list")
		.excludePathPatterns("/member/loginView")
		.excludePathPatterns("/login");		
	}
}