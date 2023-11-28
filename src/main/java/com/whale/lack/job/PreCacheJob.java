package com.whale.lack.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whale.lack.mapper.UserMapper;
import com.whale.lack.model.domain.User;
import com.whale.lack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 缓存预热任务
 */
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;

    @Resource
    public UserMapper userMapper;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);
    @Resource
    private RedissonClient redissonClient;

    //每天执行，预热推荐用户
    @Scheduled(cron = "0 27 22 * * ? ")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("lack:precachejob:docache:lock");
        try {
            //waitTime:其他线程等待的时间，因为我们缓存预热每天只做一次，所以只要有一个线程拿到锁就
            //leaseTime：锁过期时间
            if (lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)) {//是否拿到锁
                System.out.println("getLock:"+ Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20),
                            queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations =
                            redisTemplate.opsForValue();
                    //写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 30000,
                                TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        } finally {
            //释放自己的锁
            if (lock.isHeldByCurrentThread()) {//是否是当前线程
                System.out.println("unlock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}































