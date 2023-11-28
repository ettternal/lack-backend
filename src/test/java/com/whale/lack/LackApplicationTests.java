package com.whale.lack;

import com.whale.lack.mapper.UserMapper;
import com.whale.lack.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import java.security.NoSuchAlgorithmException;

/**
 * 启动类测试
 *
 */
@SpringBootTest
class LackApplicationTests {
    private UserMapper userMapper;

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
            user.setUsername("二哈呀");
            user.setUserAccount("FakeErha");
            user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.J7odbcNKBU93chEgiP0hzgHaJS&w=223&h=280&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2");
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
    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(newPassword);
    }


    @Test
    void contextLoads() {

    }

}

