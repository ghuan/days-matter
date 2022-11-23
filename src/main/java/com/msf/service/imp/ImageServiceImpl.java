package com.msf.service.imp;

import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.VTools;
import com.msf.dao.IBaseDao;
import com.msf.data.entity.Image;
import com.msf.service.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * description：图片service
 */
@Service
public class ImageServiceImpl implements IImageService {

	@Autowired
	private IBaseDao dao;
	private final Long defaultMaxSize = 81920L;//最大允许80KB

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public Map<String,Object> upload(MultipartFile image,String imageName,Long maxSize) {
		maxSize = VTools.StringIsEmpty(maxSize+"") ? defaultMaxSize : maxSize;
		Long size = image.getSize();
		if(size > maxSize){
			throw new BusinessException("上传的图片超出了"+(maxSize/1024)+"KB");
		}
		Map<String,Object> result = new HashMap<>();
		if(VTools.StringIsEmpty(imageName)){
			imageName = image.getOriginalFilename();
		}else {
			String fileName = image.getOriginalFilename();
			imageName = imageName + fileName.substring(fileName.lastIndexOf("."));
		}
		Map<String,Object> params = new HashMap<>();
		params.put("name",imageName);
		Image img = dao.doLoad("from Image where name=:name",params);
		Long imageId=0L;
		try {
			if(img != null){
				img.setCONTENT(image.getBytes());
				img.setCONTENT_TYPE(image.getContentType());
				dao.update(img);
				imageId = img.getIMAGE_ID();
			}else {
				Image saveImg = new Image();
				saveImg.setNAME(imageName);
				saveImg.setCONTENT(image.getBytes());
				saveImg.setCONTENT_TYPE(image.getContentType());
				dao.save(saveImg);
				imageId = saveImg.getIMAGE_ID();
			}
			result.put("imageUrl","image?id="+imageId);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<byte[]> getImage(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id",id);
		Image image = dao.doLoad("from Image where IMAGE_ID=:id",params);
		if(image == null){
			throw new BusinessException("图片不存在");
		}
		byte[] imageByte = image.getCONTENT();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(image.getCONTENT_TYPE()));
		return new ResponseEntity<>(imageByte, headers, HttpStatus.OK);
	}
}
