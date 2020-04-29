package com.lee.juc.blockingqueue;

import java.util.concurrent.SynchronousQueue;

/**
 * SynchronousQueue的特点：
 * 1.无存储空间，所以offer永远返回false(可以理解为队里永远是满的，因此最终都会创建非核心线程来执行任务)，如果没有空余的线程则执行拒绝策略
 * 2.一个线程put阻塞，等待另一个线程take；或者一个线程take阻塞，等待另一个线程put
 * <p>
 * Created by lsd
 * 2020-04-29 16:04
 */
public class SynchronousQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
        System.out.println("synchronousQueue.offer(1)=" + synchronousQueue.offer(1));
        System.out.println("synchronousQueue.offer(2)=" + synchronousQueue.offer(2));
        System.out.println("synchronousQueue.offer(3)=" + synchronousQueue.offer(3));
        System.out.println("synchronousQueue.size()=" + synchronousQueue.size());
        System.out.println("synchronousQueue.take()=" + synchronousQueue.take());   //会一直阻塞，等待另一个线程put

    }


}
