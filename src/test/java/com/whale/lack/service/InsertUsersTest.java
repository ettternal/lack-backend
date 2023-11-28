package com.whale.lack.service;

import com.whale.lack.mapper.LackMapper;
import com.whale.lack.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component //成为spring bean
public class InsertUsersTest {

    @Resource
    private LackMapper userMapper;

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("假二哈");
            user.setAvatarUrl("https://tse4-mm.cn.bing.net/th/id/OIP-C.DMY4H6Xibxdrzf-hkElvZgHaDu?w=326&h=176&c=7&r=0&o=5&pid=1.7");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("13987496799");
            user.setEmail("13987496799@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("1111111");
            user.setTags("[]");
            userMapper.insert(user);

        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }

}
