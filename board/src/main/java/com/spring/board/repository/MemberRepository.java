package com.spring.board.repository;

import org.springframework.data.repository.CrudRepository;

import com.spring.board.entity.Member;

public interface MemberRepository extends CrudRepository<Member,Long> {

	Member findByEmail(String email);


}
