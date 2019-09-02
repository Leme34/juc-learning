package com.lee.redis_listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;

import java.util.concurrent.CompletableFuture;

/**
 * 为了实现异步阻塞还需要我们创建消息任务对象
 * 通过JDK1.8新提供的CompletableFuture类实现线程阻塞效果，通过定义消息监听对象及超时时间完善处理机制
 * https://cloud.tencent.com/developer/article/1494584
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IotMessageTask<T> {

    //声明线程异步阻塞对象(JDK 1.8新提供Api)，实现线程阻塞效果
    private CompletableFuture<T> iotMessageFuture = new CompletableFuture<>();

    //声明消息监听对象
    private MessageListener messageListener;

    //声明超时时间
    private boolean isTimeout;
}
