package com.lee.juc.volatile_atomic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子引用的 CAS 自旋无锁算法 Demo
 * <p>
 * Created by lsd
 * 2019-11-28 21:48
 */
@Slf4j
public class AtomicReferenceDemo {

    // 线程对象的原子引用
    private AtomicReference<Thread> threadAtomicReference = new java.util.concurrent.atomic.AtomicReference<>();

    public void lock() {
        // 自旋等待CAS成功返回true为止
        while (!threadAtomicReference.compareAndSet(null, Thread.currentThread())) {
        }
        log.info("上锁成功");
    }

    public void unlock() {
        while (!threadAtomicReference.compareAndSet(Thread.currentThread(), null)) {
        }
        log.info("解锁成功");
    }

    public static void main(String[] args) {
        final AtomicReferenceDemo test = new AtomicReferenceDemo();
        new Thread(() -> {
            test.lock();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ignored) {
            }
            test.unlock();
        }, "A线程").start();

        new Thread(() -> {
            test.lock();
            test.unlock();
        }, "B线程").start();

    }

}
