package com.spring.board.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.spring.board.entity.Member;

@RepositoryRestResource // 내부적으로 Rest API가 만들어짐.
public interface MemberRepository extends PagingAndSortingRepository<Member,Long> {
//CrudRepository<Member,Long>
	Member findByEmail(String email);


}
