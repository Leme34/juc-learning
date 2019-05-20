package com.lee.juc;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {

    public static void main(String[] args) {
        RunnableDemo demo = new RunnableDemo();
        for (int i = 0; i < 10; i++) {
            new Thread(demo).start();
        }
    }
}

class RunnableDemo implements Runnable {

    // 多个线程共享的变量，volatile不能解决i++的原子性
//    private volatile int i = 0;
    // 使用原子变量解决
    private AtomicInteger i = new AtomicInteger();

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(i++);
        System.out.println(i.getAndIncrement());
    }
}