package com.spring.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.board.entity.Member;
import com.spring.board.entity.UserDetailsImpl;
import com.spring.board.repository.MemberRepository;

import lombok.extern.java.Log;

@Log
@Service(value = "userService")
public class UserDetailsServiceImpl implements UserDetailsService  {
    /* UserDetailsService 인터페이스를 상속 받아야함.... 왜 멍청하게 난 저걸 Bean 으로 등록하고 사용하려고 했는지... */
    @Autowired
    MemberRepository memberRepository; /* 그냥 여긴 JPA DB 쿼리 날려서 가져옴. */

    @Bean
    public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email); /* JPA row member data */
        
        if(member == null) {
        	throw new UsernameNotFoundException(email);
        }
        
        //log.info("////////////[email] "+ email);
        //log.info("////////////[password] "+ passwordEncoder().encode(member.getPassword()));

        /* 그냥 String 으로 security User 를 상속한 도메인 객체에 때려박음... 맞는지는 모르겠음 */
        //return new UserDetailsImpl(member.getEmail(), passwordEncoder().encode(member.getPassword()), member.getRole().toString());
        return new UserDetailsImpl(member.getEmail(), member.getPassword(), member.getRole().toString());
    }
}



