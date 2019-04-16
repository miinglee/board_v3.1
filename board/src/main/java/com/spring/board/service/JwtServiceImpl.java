package com.spring.board.service;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.java.Log;

@Log
@Service("jwtService")
public class JwtServiceImpl implements JwtService{

	private static final String SALT =  "devglan-secret";

	// JWT에 넣어놓은 데이터 가져오기
	// HTTP Header -> JWT -> Claim -> Key -> Value
	
	@Override
	public Map<String, Object> get(String key) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		log.info("================JWT request : " + request);
		String jwt = request.getHeader("Authorization");
		log.info("================JWT jwt : " + jwt);
		Jws<Claims> claims = null;
			try {
				claims = Jwts.parser()
							 .setSigningKey(SALT.getBytes("UTF-8"))
							 .parseClaimsJws(jwt);
				log.info("================JWT : " + claims);
			} catch (UnsupportedJwtException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedJwtException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		@SuppressWarnings("unchecked")
		Map<String, Object> value = (LinkedHashMap<String, Object>)claims.getBody().get(key);
		return value;
	}

	@Override
	public int getMemberId() {
		return (int)this.get("member").get("memberId");
	}
}