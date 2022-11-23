package com.msf.service.imp;

import com.msf.cache.MemoryCache;
import com.msf.common.core.constant.CacheNameConstants;
import com.msf.common.core.constant.SecurityConstants;
import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.RedisScan;
import com.msf.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * description：缓存service
 */
@Service
public class CacheServiceImpl implements ICacheService {
	@Override
	public String removeSchemaCache(String schemaCode) {
		if("*".equals(schemaCode)){
			MemoryCache.removeSchemaCacheAll();
		}else {
			MemoryCache.removeSchemaCache(schemaCode);
		}
		return "remove success!!";
	}

	@Override
	public String removeDicCache(String dicCode) {
		if("*".equals(dicCode)){
			MemoryCache.removeDicCacheAll();
			MemoryCache.removeDicDataCacheAll();
		}else {
			//删除字典配置缓存
			MemoryCache.removeDicCache(dicCode);
			//删除字典数据
			MemoryCache.removeDicDataCache(dicCode);
		}
		return "remove success!!";
	}
}
