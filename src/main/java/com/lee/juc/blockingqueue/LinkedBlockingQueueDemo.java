package com.lee.juc.blockingqueue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 演示LinkedBlockingQueue既可以无界，也可以有界
 * <p>
 * Created by lsd
 * 2020-04-29 15:55
 */
public class LinkedBlockingQueueDemo {

    //若没有指定capacity则是无界队列
    private static LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>(5);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
//            linkedBlockingQueue.put(i);    //指定capacity情况下，阻塞添加到限定数量会一直阻塞
            linkedBlockingQueue.offer(i);  //指定capacity情况下，不阻塞添加也只能放进去限定的元素
        }
        System.out.println(linkedBlockingQueue);
    }

}
