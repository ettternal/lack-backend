package com.whale.lack.service;


import com.whale.lack.mapper.LackMapper;
import com.whale.lack.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * 用户服务测试
 *
 */
@SpringBootTest
public class LackServiceTest {
    @Resource
    private LackMapper userMapper;

    @Resource
    private UserService userService;

    public LackServiceTest(LackMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 测试添加用户
     */
    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    // https://www.code-nav.cn/

    /**
     * 测试更新用户
     */
    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    /**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    // https://space.bilibili.com/12890453/

    /**
     * 测试获取用户
     */
    @Test
    public void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    /**
     * 测试用户注册
     */
    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yu pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "dogYupi";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
    }

    @Test
    public void  testSearchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java","python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assert.assertNull(userList);
    }

    /**
     * 批量插入数据
     */
    @Test
    public void doInsertUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假二哈呀");
            user.setUserAccount("FakeErha");
            user.setAvatarUrl("https://tse4-mm.cn.bing.net/th/id/OIP-C.DMY4H6Xibxdrzf-hkElvZgHaDu?w=326&h=176&c=7&r=0&o=5&pid=1.7");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("13987496799");
            user.setEmail("13987496799@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("1111111");
            user.setTags("[]");
            userList.add(user);

        }
        //30秒10万条数据
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }


    /**
     * 自定义线程池
     * 参数含义：默认多少线程池，最大多少，线程存活时间，线程单位，任务队列
     */
    private ExecutorService executorService = new ThreadPoolExecutor(60,1000, 10000,TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
    /**
     * 并发批量插入数据
     */
    @Test
    public void doConcurrencyInsertUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int batchSize = 5000;
        //10万条数据分成10组每组1万条
        int j = 0;
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();
         for (int i = 0; i <20; i++) {
            List<User> userList = Collections.synchronizedList(new ArrayList<>());
            while (true) {
                j++;
                User user = new User();
                user.setUsername("假二哈呀");
                user.setUserAccount("FakeErha");
                user.setAvatarUrl("https://tse4-mm.cn.bing.net/th/id/OIP-C.DMY4H6Xibxdrzf-hkElvZgHaDu?w=326&h=176&c=7&r=0&o=5&pid=1.7");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("13987496799");
                user.setEmail("13987496799@qq.com");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("1111111");
                user.setTags("[]");
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            //新建一个异步的任务，不要返回值，使用此方法里面的操作就是异步的
            CompletableFuture<Void> feature = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName:" + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            },executorService);
            futuresList.add(feature);
        }
         CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[]{ }));
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }
}