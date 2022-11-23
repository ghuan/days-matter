package com.msf.controller;

import com.msf.service.IDicService;
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
@RequestMapping("/dic")
public class DicController {

	@Autowired
	private IDicService dicService;

	/**
	 * 保存dicDetails明细
	 *
	 * @return 用户信息
	 */
	@PostMapping(value = {"/saveDic"})
	public void saveDic(@RequestBody Map req) {
		dicService.saveDic(req);
	}

	@PostMapping(value = {"/removeDic"})
	public void removeDic(@RequestBody Map req) {
		dicService.removeDic(req);
	}
}
