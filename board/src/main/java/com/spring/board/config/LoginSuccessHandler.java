package com.spring.board.config;

import java.net.URI;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.java.Log;

@Log
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {

//		log.info("--------------determineTargetUrl------------------------");
//
//		Object dest = request.getSession().getAttribute("dest");
//
//		String nextURL = null;
//		
//		if (dest != null) {
//
//			request.getSession().removeAttribute("dest");
//			
//			nextURL = (String) dest;
//
//		} else {
//
//			nextURL = super.determineTargetUrl(request, response);
//		}
//		
//		log.info("-------------------"+nextURL+"========================");
//		return nextURL;
		
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
	
	
}