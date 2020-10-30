package com.lee.juc.lock.reentrantlock_and_condition;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示ReentrantLock的等待锁过程是可中断的
 */
public class LockInterruptibly {

    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + "尝试获取锁");
            try {
                lock.lockInterruptibly();  //可中断地等待锁
                try {
                    System.out.println(Thread.currentThread().getName() + "获取到了锁");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + "休眠被中断了");
                } finally {
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + "释放了锁");
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + "获得锁期间被中断了");
            }
        };
        Thread thread0 = new Thread(task);
        Thread thread1 = new Thread(task);
        thread0.start();
        thread1.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread0.interrupt();
        thread1.interrupt();
    }

}
