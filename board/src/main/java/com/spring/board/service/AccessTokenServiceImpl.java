package com.spring.board.service;

import java.net.URI;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.java.Log;

@Log
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

	public String requestAccessToken(String username, String password, String oauthaurl) {
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
	       JSONObject jsonObject = null;
	       
		try {
			jsonObject = new JSONObject(rs.getBody());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		
		try {
			token = jsonObject.getString("access_token");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
	       log.info("========================access_token : "+token);

	       return token;
	   }
	 
	 public String checkAccessToken(String token) {

		 	log.info("===================Check access token");
	        
	        String oauthaurl ="http://localhost:8080/oauth/check_token";
	        
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
	                                                           .queryParam("token", token);

	        URI myUri=builder.buildAndExpand().toUri();
	        log.info("========================myUri"+myUri);
	        HttpEntity<?> request = new HttpEntity<>(headers);
	        log.info("========================request"+request);
	        ResponseEntity<String> rs = restTemplate.exchange(myUri, HttpMethod.POST, request,String.class);
	        log.info("========================rs"+rs);
	        JSONObject jsonObject = null;
	        String result = "";
	        
			try {
				jsonObject = new JSONObject(rs.getBody());
				log.info("#################################jsonObject " + jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}      
			
			try {			
				
				Iterator i = jsonObject.keys();
				while(i.hasNext()) {
					if(i.next().toString().equals("user_name")) {
						result = jsonObject.getString("user_name");
						break;
					}					
				}				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        
	        log.info("========================check token result : "+result);

	        return result;
	    }
 
}
