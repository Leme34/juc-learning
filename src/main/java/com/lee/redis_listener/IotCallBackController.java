package com.lee.redis_listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 此时启动应用调用开锁模拟接口"/iot/unLock"，逻辑就会暂时处于订阅等待状态；
 * 之后再模拟调用开锁回调Redis消息发布逻辑"/iot/unLockCallBack"，
 * 之前的阻塞等待就会因为监听回调而完成同步返回。
 */
@RestController
@RequestMapping("/iot")
public class IotCallBackController {

    //引入Redis客户端操作对象
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/unLockCallBack", method = RequestMethod.POST)
    public boolean unLockCallBack(@RequestParam(value = "thingName") String thingName,
            @RequestParam(value = "requestId") String requestId) {
        //生成监听频道Key
        String key = "IOT_" + thingName + "_" + requestId;
        //模拟实现消息回调
        stringRedisTemplate.convertAndSend(key, "this is a redis callback");
        return true;
    }
}
