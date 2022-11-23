package com.msf.controller;

import com.msf.service.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author zhangyq
 * @date 2019/11/1
 */
@RestController
public class ImageController {

	@Autowired
	private IImageService imageService;
	/**
	 * 上传图片
	 */
	@PostMapping("/image/upload")
	public Map<String,Object> upload(@RequestParam(value = "image") MultipartFile image, String imageName,Long maxSize) {
		return imageService.upload(image,imageName,maxSize);
	}

	/**
	 * 获取图片
	 */
	@GetMapping("/image")
	public ResponseEntity<byte[]> getImage(@RequestParam(value = "id") Long id) {
		return imageService.getImage(id);
	}

}
