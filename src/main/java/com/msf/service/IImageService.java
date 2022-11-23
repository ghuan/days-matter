package com.msf.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 *
* description：图片service接口
 */
public interface IImageService {
	Map<String,Object> upload(MultipartFile image, String imageName, Long maxSize);
	ResponseEntity<byte[]> getImage(Long id);
}
