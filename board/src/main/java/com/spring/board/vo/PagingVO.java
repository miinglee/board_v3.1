package com.spring.board.vo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PagingVO {
	private static final int DEFAULT_SIZE = 100;
	private static final int DEFAULT_MAX_SIZE = 10000000;

	private int page;
	private int size;

	public PagingVO() {
		this.page = 1;
		this.size = DEFAULT_SIZE;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page < 0 ? 1 : page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {

		this.size = size < DEFAULT_SIZE || size > DEFAULT_MAX_SIZE ? DEFAULT_SIZE : size;
	}

	public Pageable makePageable(int direction, String property) {

		Sort.Direction dir = direction == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;

		// PageRequest(검색을 원하는 페이지 번호(0부터 시작), 한 페이지 개수, 정렬방식, 속성)
		return new PageRequest(this.page - 1, this.size, dir, property);
	}
}
