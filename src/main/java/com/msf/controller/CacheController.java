package com.msf.controller;

import com.msf.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zyq
 * @date 2019/2/1
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

	@Autowired
	private ICacheService cacheService;

	@GetMapping("removeSchemaCache/{schemaCode}")
	public String removeSchemaCache(@PathVariable String schemaCode){
		return cacheService.removeSchemaCache(schemaCode);
	}

	@GetMapping("removeDicCache/{dicCode}")
	public String removeDicCache(@PathVariable String dicCode){
		return cacheService.removeDicCache(dicCode);
	}
}
