package com.lee.juc.deadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用获取锁超时策略避免死锁
 * <p>
 * Created by lsd
 * 2020-04-26 08:11
 */
@Slf4j
public class TryLock {

    private static int flag;
    private static ReentrantLock lock1 = new ReentrantLock();
    private static ReentrantLock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        // 线程1先获取锁1，再获取锁2
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    if (lock1.tryLock(800, TimeUnit.MILLISECONDS)) {
                        log.debug("获取到了锁1");
                        if (lock2.tryLock(800, TimeUnit.MILLISECONDS)) {
                            log.debug("获取到了锁2");
                            log.debug("开始干活...");
                            log.debug("任务结束...释放锁1和锁2");
                            lock2.unlock();
                            lock1.unlock();
                            break;  //结束循环
                        } else {
                            log.debug("获取锁2失败...释放锁1并重试");
                            lock1.unlock();
                        }
                    } else {
                        log.debug("获取锁1失败...准备重试");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 线程2先获取锁2，再获取锁1
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    if (lock2.tryLock(800, TimeUnit.MILLISECONDS)) {
                        log.debug("获取到了锁2");
                        if (lock1.tryLock(800, TimeUnit.MILLISECONDS)) {
                            log.debug("获取到了锁1");
                            log.debug("开始干活...");
                            log.debug("任务结束...释放锁1和锁2");
                            lock1.unlock();
                            lock2.unlock();
                            break;  //结束循环
                        } else {
                            log.debug("获取锁1失败...释放锁2并重试");
                            lock2.unlock();
                        }
                    } else {
                        log.debug("获取锁2失败...准备重试");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
