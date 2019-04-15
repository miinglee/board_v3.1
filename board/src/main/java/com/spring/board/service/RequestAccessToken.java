package com.spring.board.service;


import java.net.URI;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.java.Log;

@Log
@Component("AccessToken")
public class RequestAccessToken {

    public String requestAccessToken(String username, String password, String oauthaurl) throws JSONException {
        log.info("===================Request access token");
        String token = null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);



        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(oauthaurl)
                                                           .queryParam("grant_type", "password")
                                                           .queryParam("username", username)
                                                           .queryParam("password", password);

        URI myUri=builder.buildAndExpand().toUri();

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> rs = restTemplate.exchange(myUri, HttpMethod.POST, request,String.class);
        JSONObject jsonObject = new JSONObject(rs.getBody());
        log.info("========================access_token : "+jsonObject.getString("access_token"));

        //get access_token from jsonObject here

        return token;
    }
}
