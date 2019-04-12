package com.spring.board.entity;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import lombok.extern.java.Log;

@Log
public class UserDetailsImpl extends User { 


    /* 사실 여기선 이거밖에 안씀. 위에 두개는 어떤거 하는지 모르겠는데 좀더 연구한 후에... */
    public UserDetailsImpl (String email, String password, String s) {
    	
        super(email, password, AuthorityUtils.createAuthorityList(s));
        log.info(password);
    }
}



