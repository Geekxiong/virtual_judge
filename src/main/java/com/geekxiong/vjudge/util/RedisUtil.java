package com.geekxiong.vjudge.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description RedisUtil
 * @Author xiong
 * @Date 2020/07/17 9:02
 * @Version 1.0
 */

@Component
public class RedisUtil {
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setWithTime(String id, Object obj, int ttl, TimeUnit timeUnit) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(id, obj, ttl, timeUnit);
    }

    public void set(String id, Object obj) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(id, obj);
    }

    public Object get(String id) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        return vo.get(id);
    }

    public void updateWithTime(String id, Object obj, int ttl, TimeUnit timeUnit) {
        setWithTime(id, obj, ttl, timeUnit);
    }

    public void update(String id, Object obj) {
        set(id, obj);
    }

    public void delete(String id) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.getOperations().delete(id);
    }

    public void rightPushToList(String id, Object obj){
        ListOperations<String, Object> lo =  redisTemplate.opsForList();
        lo.rightPush(id, obj);
    }

    public void leftPushToList(String id, Object obj){
        ListOperations<String, Object> lo =  redisTemplate.opsForList();
        lo.leftPush(id, obj);
    }

    public Object rightPopFromList(String id){
        ListOperations<String, Object> lo =  redisTemplate.opsForList();
        return lo.rightPop(id);
    }

    public Object leftPopFromList(String id){
        ListOperations<String, Object> lo =  redisTemplate.opsForList();
        return lo.leftPop(id);
    }

    public Long getListSize(String id) {
        ListOperations<String, Object> lo =  redisTemplate.opsForList();
        return lo.size(id);
    }

}
