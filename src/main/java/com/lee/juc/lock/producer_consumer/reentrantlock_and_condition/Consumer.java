package com.lee.juc.lock.producer_consumer.reentrantlock_and_condition;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

/**
 * Created by lsd
 * 2019-09-22 13:43
 */
@AllArgsConstructor
public class Consumer extends Thread {

    // 产品队列
    private ProductQueue<Object> queue;
    // 计数器闭锁
    private CountDownLatch countDownLatch;

    @SneakyThrows
    @Override
    public void run() {
        // 等待主线程的所有消费者线程创建完成，才开始抢夺产品
        countDownLatch.await();
        while (true) {
            Object item = queue.take();
            System.out.println("消费了产品：" + item);
        }
    }

}
