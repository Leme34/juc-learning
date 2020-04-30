package com.lee.juc.producer_consumer.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 阻塞队列实现生消模型
 * <p>
 * Created by lsd
 * 2020-04-29 18:30
 */
public class ProducerConsumerModel {
    //有界队列
    private static ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) {
        Runnable producer = () -> {
            while (true) {
                // 每次生产10个产品
                for (int i = 0; i < 10; i++) {
                    // 0.1s生产一个
                    try {
                        queue.put(i);
                        System.out.println("生产了产品:" + i);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Runnable consumer = () -> {
            while (true) {
                // 1s消费1个
                try {
                    Integer item = queue.take();
                    System.out.println("消费了产品：" + item);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        // 1个生产者，1个消费者
        new Thread(producer).start();
        new Thread(consumer).start();
    }

}
