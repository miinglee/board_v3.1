package com.spring.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity /* WebConfig 컴포넌트 등록 */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

@Autowired
UserDetailsService userDetailsService;

@Autowired
PasswordEncoder passwordEncoder;

// 로그인 성공 후 default target page로 redirect(302)시키지 않고 200 Status와 함께 사용자 정보 JSON 문자열 리턴

@Autowired


    /* Password Encoder 등록 */
@Bean
public PasswordEncoder passwordEncoder() {
return new BCryptPasswordEncoder();
}


    /* 인증방식 */
@Autowired
public void configure(AuthenticationManagerBuilder auth) throws Exception {
auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
}


    /* Security 제외 패턴 */
@Override
public void configure(WebSecurity web) throws Exception {
web.ignoring()
.antMatchers("/resources/**","/webjars/**","/js/**","/css/**");
}

    /* 각종 시큐어 패턴등록 */
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
	.authorizeRequests() /* 인증 요청 선언?????? */
	.anyRequest().authenticated()
	.and().formLogin()
	.loginPage("/member/show") // 로그인 뷰 페이지 
	.loginProcessingUrl("/member/join")
	.permitAll(); /* 모두 오픈 ( 반대는 denyAll() ) */
}
}



