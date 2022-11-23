package com.msf.util;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试
 *
 * @author tianma
 * @date 2022/ 10/08 11:10:13
 */
public class Test {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        for(int i=0;i<20;i++){
            new Thread(() -> {
                restTemplate.postForObject("http://localhost:9001/simple/simpleQuery",new HashMap<String,Object>(){{
                    put("schemaCode","sys_dic");
                    put("pageSize",50);
                    put("pageNo",1);
                }}, Map.class);
            }).start();
        }
    }
}
