package com.msf.controller;

import com.msf.service.IDicUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author huan.gao
 * @date 2019/2/1
 */
@RestController
@RequestMapping("/dic")
public class DicUtilController {

	@Autowired
	private IDicUtilService dicUtilService;

	@PostMapping(value = {"/getDicData"})
	public List<Map<String,Object>> getDicData(@RequestBody Map req) {
		return dicUtilService.getDicData(req);
	}
	@PostMapping(value = {"/getSimpleDicData"})
	public Map<String,Object> getSimpleDicData(@RequestBody Map req) {
		return dicUtilService.getSimpleDicData(req);
	}
	@PostMapping("/addDicText")
	public Map<String,Object> addDicText(@RequestBody Map<String,Object> req) {
		return dicUtilService.addDicText(req);
	}
}
