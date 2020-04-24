package com.lee.thread.wait_notify;

import lombok.extern.slf4j.Slf4j;

/**
 * 用wait与notify机制实现两个线程交替打印0-100的奇偶数
 * Created by lsd
 * 2020-04-24 12:01
 */
@Slf4j
public class AlternativePrint {

    private static int i;
    private final static Object lockObj = new Object();

    public static void main(String[] args) {
        Runnable task = () -> {
            while (i <= 100) {
                synchronized (lockObj) {
                    log.debug("" + i++);
                    //通知唤醒等待线程(switch)
                    lockObj.notify();
                    // 未结束，则阻塞当前线程并释放锁
                    if (i <= 100) {
                        try {
                            lockObj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        new Thread(task).start();
        new Thread(task).start();
    }

}
