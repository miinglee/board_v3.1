package com.spring.board.vo;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.java.Log;

@Log
public class UploadImage {
	
	/***[이미지 업로드]***/	
	
	// 이미지 파일 업로드 경로
	static final String uploadPath = "./src/main/resources/static/upload/";	
	
	public static void upload(MultipartFile mf) {	
				
		// path에 upload 폴더가 없는 경우 생성
		File dir = new File(uploadPath); 
		if (!dir.isDirectory()) { dir.mkdirs(); }
		
		// 업로드할 파일 이름
		String fileName = mf.getOriginalFilename();
		
		// pathname으로 파일 생성
		File uploadFile = new File(uploadPath+"//"+fileName);
		
		// 파일 업로드
		try {
			FileCopyUtils.copy(mf.getBytes(), uploadFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("fileName : " + fileName ); 
		log.info("uploadFile : " + uploadFile );
	}
}
