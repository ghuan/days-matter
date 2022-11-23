package com.msf.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache {
    public static Map<String,Object> schemaCache = new ConcurrentHashMap<>();
    public static Map<String,Object> dicCache = new ConcurrentHashMap<>();
    public static Map<String,Object> dicDataCache = new ConcurrentHashMap<>();

    public static void removeSchemaCache(String schemaCode){
        schemaCache.remove(schemaCode);
    }
    public static void removeSchemaCacheAll(){
        schemaCache.clear();
    }
    public static void removeDicCache(String dicCode){
        dicCache.remove(dicCode);
    }
    public static void removeDicCacheAll(){
        dicCache.clear();
    }
    public static void removeDicDataCache(String s){
        dicDataCache.forEach((key,data) -> {
            if(key.indexOf(s) != -1){
                dicDataCache.remove(key);
            }
        });
    }
    public static void removeDicDataCacheAll(){
        dicDataCache.clear();
    }

    public static Object getSchemaCache(String schemaCode){
        return schemaCache.get(schemaCode);
    }
    public static Object getDicCache(String dicCode){
        return dicCache.get(dicCode);
    }
    public static void putSchemaCache(String schemaCode,Object data){
        schemaCache.put(schemaCode,data);
    }
    public static void putDicCache(String dicCode,Object data){
        dicCache.put(dicCode,data);
    }
    public static Object getDicDataCache(String s){
        return dicDataCache.get(s);
    }
    public static void putDicDataCache(String s,Object data){
        dicDataCache.put(s,data);
    }
}
