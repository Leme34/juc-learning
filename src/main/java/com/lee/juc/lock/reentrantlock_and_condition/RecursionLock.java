package com.lee.juc.lock.reentrantlock_and_condition;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入性演示 -- 递归获取锁
 */
public class RecursionLock {

    private static ReentrantLock lock = new ReentrantLock();

    private static void accessResource() {
        lock.lock();
        try {
            System.out.println("processing...");
            if (lock.getHoldCount() < 5) { //递归5次
                accessResource();
                System.out.printf("这是第%d次获取锁", lock.getHoldCount());
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        accessResource();
    }
}
