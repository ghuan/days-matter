package com.msf.controller;

import com.msf.common.core.dto.AgeDTO;
import com.msf.service.ISimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author huan.gao
 * @date 2019/2/1
 */
@RestController
@RequestMapping("/simple")
public class SimpleController {

	@Autowired
	private ISimpleService simpleService;

	/**
	 * 获取拼音五笔码
	 */
	@PostMapping("/getCode")
	public Map<String,Object> getCode(@RequestBody Map<String,Object> req) {
		return simpleService.getCode(req);
	}

	/**
	 * 获取年龄
	 */
	@PostMapping("/getAge")
	public AgeDTO getAge(@RequestBody String birthday) {
		return simpleService.getAge(birthday);
	}

	/**
	 * 获取服务器时间
	 */
	@GetMapping("/getServerDate")
	public String getServerDate() {
		return simpleService.getServerDate();
	}

	/**
	 * 执行SQL
	 */
	@PostMapping("/executeSQL")
	public Map<String,Object> executeSQL(@RequestBody Map<String,Object> req) {
		return simpleService.executeSQL(req);
	}

	/**
	 * 远程执行SQL
	 */
	@PostMapping("/executeSQLRemote")
	public Map<String,Object> executeSQLRemote(@RequestBody Map<String,Object> req) {
		return simpleService.executeSQLRemote(req);
	}

}
