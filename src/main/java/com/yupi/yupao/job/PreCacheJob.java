package com.yupi.yupao.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    //重点用户,这里以后改成动态获取那些再白名单里的用户，在数据库上加字段等方法
    private List<Long> mainUserList= Arrays.asList(1L);

    //每天执行，预热推荐用户
    @Scheduled(cron = "0 59 23 * * ? *")//crontab表达式，意思是每天的23:59:00执行，可以百度在线生成工具
    public void doCacheRecommend(){
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            if (lock.tryLock(0,3000, TimeUnit.MILLISECONDS)) {//只有一个线程能获取到锁
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    //改造成分页，便于前端查看,使用分页时需要配置分页插件
                    Page<User> userList = userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey=String.format("yupao:user:recommend:%s",userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    //写缓存
                    try {
                        valueOperations.set(redisKey,userList,1, TimeUnit.MINUTES);
                    }catch (Exception e){
                        log.error("error",e);
                    }

                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }

    }



}
