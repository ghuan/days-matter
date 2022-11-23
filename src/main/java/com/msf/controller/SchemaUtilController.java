package com.msf.controller;

import com.msf.service.ISchemaUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zyq
 * @date 2019/2/1
 */
@RestController
@RequestMapping("/schema")
public class SchemaUtilController {

	@Autowired
	private ISchemaUtilService schemaUtilService;

	@PostMapping("/getSchema")
	public Map<String,Object> getSchema(@RequestBody Map<String,Object> req) {
		return schemaUtilService.getSchema(req);
	}
}
