package com.lee.redis_listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 模拟了一个开锁请求，在完成异步消息处理后会开启Redis订阅监听
 */
@RestController
@RequestMapping("/iot")
public class IotController {

    //注入Redis消息容器对象
    @Autowired
    RedisMessageListenerContainer redisMessageListenerContainer;

    @RequestMapping(value = "/unLock", method = RequestMethod.POST)
    public boolean unLock(@RequestParam(value = "thingName") String thingName,
            @RequestParam(value = "requestId") String requestId)
            throws InterruptedException, ExecutionException, TimeoutException {

        //此处实现异步消息调用处理....每个操作都应该很复杂,需要花费相对很长的时间（从多个系统中查数据等）

        //生成监听频道Key
        String key = "IOT_" + thingName + "_" + requestId;
        //创建监听Topic
        ChannelTopic channelTopic = new ChannelTopic(key);
        //创建消息任务对象
        IotMessageTask iotMessageTask = new IotMessageTask();
        //任务对象及监听Topic添加到消息监听容器
        try {
            redisMessageListenerContainer.addMessageListener(new IotMessageListener(iotMessageTask), channelTopic);
            System.out.println("start redis subscribe listener->" + key);
            //进入同步阻塞等待，超时时间设置为60秒
            Message message = (Message) iotMessageTask.getIotMessageFuture().get(60000, TimeUnit.MILLISECONDS);
            System.out.println("receive redis callback message->" + message.toString());
        } finally {
            //销毁消息监听对象
            if (iotMessageTask != null) {
                redisMessageListenerContainer.removeMessageListener(iotMessageTask.getMessageListener());
            }
        }
        return true;
    }
}
