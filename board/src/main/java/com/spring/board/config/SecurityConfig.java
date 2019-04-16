package com.spring.board.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.spring.board.interceptor.TokenCheckInterceptor;
import com.spring.board.service.AuthProvider;

@Configuration
@EnableWebSecurity /* WebConfig 컴포넌트 등록 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


@Resource(name = "userService")
private UserDetailsService userDetailsService;

@Autowired
PasswordEncoder passwordEncoder;

@Autowired
AuthProvider authProvider;

@Autowired
public LoginSuccessHandler loginSuccessHandler;

@Override
@Bean
public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
}

@Bean
public TokenStore tokenStore() {
    return new InMemoryTokenStore();
}

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
	.csrf().disable()
	.authorizeRequests() /* 인증 요청 선언?????? */
	.anyRequest().authenticated()
	.and().formLogin()
	.successHandler(loginSuccessHandler)
	.loginPage("/member/loginView") // 로그인 뷰
	//.loginProcessingUrl("/member/validate") // 로그인 수행	
	//.defaultSuccessUrl("/posting/list") // 로그인 성공시
	.usernameParameter("email") // 로그인 뷰에서 아이디로 사용할 이름
	.passwordParameter("password") // 로그인 패스워드로 사용할 이름
	.permitAll(); /* 모두 오픈 ( 반대는 denyAll() ) */
	//.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	
	//http.authenticationProvider(authProvider); // 로그인 프로세스가 진행될 provider (로그인 버튼이 눌러지면 authenticate 메소드를 호출하여 로그인 검사를 수행함)
	http.exceptionHandling().accessDeniedPage("/member/accessDenied");
	http.logout().logoutUrl("/member/logout").logoutSuccessUrl("/member/loginView").deleteCookies("JSESSIONID","accessToken","UserID").invalidateHttpSession(true);
}

//@Bean
//public FilterRegistrationBean corsFilter() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    CorsConfiguration config = new CorsConfiguration();
//    config.setAllowCredentials(true);
//    config.addAllowedOrigin("*");
//    config.addAllowedHeader("*");
//    config.addAllowedMethod("*");
//    source.registerCorsConfiguration("/**", config);
//    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//    bean.setOrder(0);
//    return bean;
//}

}



