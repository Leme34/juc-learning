package com.lee.juc.atomic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用原子引用实现自旋锁
 * <p>
 * Created by lsd
 * 2019-11-28 21:48
 */
@Slf4j
public class AtomicReferenceSpinLock {

    // 线程对象的原子引用
    private AtomicReference<Thread> threadAtomicReference = new java.util.concurrent.atomic.AtomicReference<>();
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 获取自旋锁
     */
    public void lock() {
        log.debug("尝试获取自旋锁");
        // 自旋等待CAS成功返回true为止
        while (!threadAtomicReference.compareAndSet(null, Thread.currentThread())) {
            log.debug("自旋重试");
        }
        log.debug("获取锁成功");
    }

    /**
     * 释放自旋锁
     */
    public void unlock() {
        while (!threadAtomicReference.compareAndSet(Thread.currentThread(), null)) {
        }
        log.debug("释放锁成功");
    }

    public static void main(String[] args) {
        final AtomicReferenceSpinLock spinLock = new AtomicReferenceSpinLock();
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {
            }
            spinLock.lock();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ignored) {
            } finally {
                spinLock.unlock();
            }
        }, "A线程").start();

        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {
            }
            spinLock.lock();
            spinLock.unlock();
        }, "B线程").start();

        countDownLatch.countDown();
    }

}
