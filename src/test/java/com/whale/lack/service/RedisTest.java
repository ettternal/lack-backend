package com.whale.lack.service;

import com.whale.lack.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

//操作redis的类
@SpringBootTest
public class RedisTest {

    //操作redis的对象
    @Resource
    private RedisTemplate redisTemplate;


    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("erhaString","fish");
        valueOperations.set("erhaInt",1);
        valueOperations.set("erhaDouble",2.0);
        User user = new User();
        user.setId(001);
        user.setUsername("testRedisString");
        valueOperations.set("erhaUser",user);
        //查
        Object erha = valueOperations.get("erhaString");
        Assert.assertTrue("fish".equals((String) erha));
        erha = valueOperations.get("erhaInt");
        Assert.assertTrue(1== (int)erha);
        erha = valueOperations.get("erhaDouble");
        Assert.assertTrue(2.0 == (Double) erha);
        erha = valueOperations.get("erhaUser");
        valueOperations.get("erhaUser");
        //删
        redisTemplate.delete("erhaString");

    }
}
