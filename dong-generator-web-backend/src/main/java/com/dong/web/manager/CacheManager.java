package com.dong.web.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    //本地缓存
    Cache<String, String> localCache = Caffeine.newBuilder()
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();


    /**
     * 写入本地缓存，写入redis缓存
     * @param key
     * @param value
     */
    public void put(String key, String value){
        localCache.put(key, value);
        stringRedisTemplate.opsForValue().set(key, value, 100, TimeUnit.MINUTES);
    }


    public String get(String key){
        // 先从本地缓存中查找
        String value = localCache.getIfPresent(key);
        if (value != null){
            return value;
        }

        // 如果没有查到，从Redis中获取，获取到后放入本地缓存并返回
        value = stringRedisTemplate.opsForValue().get(key);
        if (value != null){
            // 写入本地缓存
            localCache.put(key, value);
            return value;
        }

        return value;
    }

    /**
     * 移出所有缓存
     * @param key
     */
    public void delete(String key){
        localCache.invalidate(key);
        stringRedisTemplate.delete(key);
    }




}
