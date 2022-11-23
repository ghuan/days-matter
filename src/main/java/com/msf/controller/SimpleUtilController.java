package com.msf.controller;

import com.msf.service.ISimpleUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author huan.gao
 * @date 2019/2/1
 */
@RestController
@RequestMapping("/simple")
public class SimpleUtilController {

	@Autowired
	private ISimpleUtilService simpleUtilService;

	/**
	 * 简单的通用查询
	 */
	@PostMapping("/simpleQuery")
	public Map<String,Object> simpleQuery(@RequestBody Map<String,Object> req) {
		return simpleUtilService.simpleQuery(req);
	}

	/**
	 * 简单的通用查询
	 */
	@PostMapping("/simpleLoad")
	public Map<String,Object> simpleLoad(@RequestBody Map<String,Object> req) {
		return simpleUtilService.simpleLoad(req);
	}

	/**
	 * 简单的通用查询
	 */
	@PostMapping("/simpleSave")
	public Map<String,Object> simpleSave(@RequestBody Map<String,Object> req) {
		return simpleUtilService.simpleSave(req);
	}

	/**
	 * 简单的字典加载查询
	 */
	@PostMapping("/addDicText")
	public Map<String,Object> addDicText(@RequestBody Map<String,Object> req) {
		return simpleUtilService.addDicText(req);
	}
	/**
	 * 简单的通用删除（物理删除）
	 */
	@PostMapping("/simpleRemove")
	public void simpleRemove(@RequestBody Map<String,Object> req) {
		simpleUtilService.simpleRemove(req);
	}

}
