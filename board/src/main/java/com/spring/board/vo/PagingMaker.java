package com.spring.board.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.board.entity.Posting;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

@Getter
@ToString(exclude = "pageList")
@Log
public class PagingMaker<T> {
	
	public Page<T> result;

	public Pageable prevPage;
	public Pageable nextPage;

	public int currentPageNum;
	public int totalPageNum;
	public int currentPageSize;

	public Pageable currentPage;

	public List<Pageable> pageList;

	public PagingMaker(Page<T> result) {
		
		this.result = result;
		this.currentPageNum = result.getNumber() + 1;
		this.totalPageNum = result.getTotalPages();
		this.pageList = new ArrayList<>();

		if (!result.hasNext() && !result.hasPrevious()) { // 페이지 1번 1개인 경우
			this.currentPage = null;
		} else if (totalPageNum != 1 && !result.hasNext()) { // 페이지 여러개인데 현재 마지막 페이지인 경우
			this.currentPage = result.previousPageable().next();
		} else {
			this.currentPage = result.nextPageable().previousOrFirst();
		}

		calcPages();
	}

	private void calcPages() {

		int tempEndNum = (int) (Math.ceil(this.currentPageNum / 5.0) * 5); // 소수점 이하 올림

		int startNum = tempEndNum - 4;

		Pageable startPage = this.currentPage;

		if (startPage != null) {
			log.info("startPageNum: " + startPage.getPageNumber());
			
			this.currentPageSize = startPage.getPageSize();
			this.prevPage = startPage.getPageNumber() <= 0 ? null : startPage.previousOrFirst();			
			this.nextPage = startPage.getPageNumber() + 1 < totalPageNum ? startPage.next() : null;
			
			for (int i = startNum; i < this.currentPageNum; i++) {
				startPage = startPage.previousOrFirst();
			}
			
			log.info("tempEndNum: " + tempEndNum);
			log.info("startNum: " + startNum);
			log.info("currentPageSize: " + currentPageSize);
			log.info("prevPage: " + prevPage);
			log.info("nextPage: " + nextPage);
			log.info("total: "+ totalPageNum);

			// 실제 페이지 개수와 임시 페이지(5단위) 개수 비교
			if (this.totalPageNum < tempEndNum) {
				tempEndNum = this.totalPageNum;
				this.nextPage = null;
			}
			
			// 페이지 리스트
			for (int i = startNum; i <= tempEndNum; i++) {
				pageList.add(startPage);
				startPage = startPage.next();
			}
		}
	}
}
