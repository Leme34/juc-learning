package com.lee.thread.wait_notify;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Object的 wait 和 notify 示例
 * <p>
 * Created by lsd
 * 2020-04-24 09:37
 */
@Slf4j
public class WaitNotifyLearning {

    private final static Object lockObj = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            synchronized (lockObj) {
                log.debug("thread1获取到锁，开始执行");
                // 阻塞当前线程（加入等待队列），并释放lockObj这把锁
                try {
                    log.debug("lockObj.wait()...");
                    lockObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("thread1执行完成");
            }
        });
        Thread thread2 = new Thread(() -> {
            synchronized (lockObj) {
                log.debug("thread2获取到锁，开始执行");
                lockObj.notify(); // 从锁等待队列中随机唤醒一个
//                lockObj.notifyAll(); // 唤醒整个锁等待队列，抢夺到锁的线程去执行
                log.debug("thread2执行完成");
            }
        });
        thread1.start();
        // 睡眠一下保证thread1先执行
        TimeUnit.MILLISECONDS.sleep(500);
        thread2.start();
    }

}
