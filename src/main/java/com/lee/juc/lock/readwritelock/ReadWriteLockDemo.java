package com.lee.juc.lock.readwritelock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 * <p>
 * 写写/读写 需要互斥
 * 读读 不需要互斥
 * <p>
 * 读锁可以让多个读线程并发持有
 */
public class ReadWriteLockDemo {

    public static void main(String[] args) {

        ReadWriteLockTest readWriteLockTest = new ReadWriteLockTest();
        // 1个线程写，100个并发读
        new Thread(() ->
                readWriteLockTest.set((int) (Math.random() * 101)), "Write")
                .start();

        for (int i = 0; i < 100; i++) {
            new Thread(() -> readWriteLockTest.get()).start();
        }
    }

}

class ReadWriteLockTest {

    private int num = 0;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    //读 允许多个线程并发读
    public void get() {
        lock.readLock().lock();

        try {
            System.out.println(Thread.currentThread().getName() + " : " + num);
        } finally {
            lock.readLock().unlock();
        }
    }

    //写 一次只能有一个线程来读
    public void set(int num) {

        lock.writeLock().lock();

        try {
            System.out.println(Thread.currentThread().getName());
            this.num = num;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
