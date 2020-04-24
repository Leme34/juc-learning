package com.lee.juc.lock.producer_consumer.reentrantlock_and_condition;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * 生产者
 * <p>
 * Created by lsd
 * 2019-09-22 12:09
 */
@AllArgsConstructor
public class Producer implements Runnable {
    // 产品队列
    private ProductQueue<Object> queue;

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            // 每次生产10个产品
            for (int i = 0; i < 10; i++) {
                // 0.1s生产一个
                queue.put(i);
                System.out.println("生产了产品:" + i);
                Thread.sleep(100);
            }
        }
    }
}
