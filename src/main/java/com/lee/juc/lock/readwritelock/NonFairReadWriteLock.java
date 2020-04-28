package com.lee.juc.lock.readwritelock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁用法演示 与 ReentrantReadWriteLock的非公平和公平策略
 *
 * @formatter:off
 *
 * @see ReentrantReadWriteLock.FairSync
 * 公平锁的插队策略：都不允许插队
 *
 * @see ReentrantReadWriteLock.NonfairSync
 * 非公平锁的【写锁】插队策略：写锁可以随时插队
 * 非公平锁的【读锁】插队策略：
 *  1.读锁插队
 *      假设线程2和线程4正在读取，线程3想写入因为获取不到锁而进入了等待队列，其后又有线程5想要读取，此时能否让线程5插队到线程3之前？
 *      优点：线程5无需等待，可以与其他读线程一起进行读操作
 *      缺点：容易造成写线程饥饿
 *  2.为了避免饥饿，读锁仅在等待队列头结点非写线程时允许插队【ReentrantReadWriteLock的读锁实现方式】
 *
 * @formatter:on
 *
 * <p>
 * Created by lsd
 * 2020-04-27 10:37
 */
public class NonFairReadWriteLock {

    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);  //默认非公平
    private static ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private static ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    // 等待所有线程创建完成
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 读场景
     */
    private static void read() {
        try {
            countDownLatch.await();   // 等待主线程的所有线程创建完成，才开始
        } catch (InterruptedException ignored) {
        }
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "得到了读锁，正在读取...");
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放读锁");
            readLock.unlock();
        }
    }

    /**
     * 写场景
     */
    private static void write() {
        try {
            countDownLatch.await(); // 等待主线程的所有线程创建完成，才开始
        } catch (InterruptedException ignored) {
        }
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "得到了写锁，正在写入...");
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放写锁");
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        // 读锁Thread1与Thread2可以同时读，写锁Thread3与Thread4不能同时写
        new Thread(NonFairReadWriteLock::write, "Thread1").start();
        new Thread(NonFairReadWriteLock::read, "Thread2").start();
        new Thread(NonFairReadWriteLock::read, "Thread3").start();
        new Thread(NonFairReadWriteLock::write, "Thread4").start();
        new Thread(NonFairReadWriteLock::read, "Thread5").start();   //验证了非公平读锁的插队策略，队列头是写线程时不能插队
        // 开一个线程不断生产读线程，非公平情况下这些子线程会与上边最后一个读线程同时尝试获取读锁(争夺锁)，
        // 而公平情况下这些子线程会在上边最后一个读线程拿到读锁之后才尝试获取读锁
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                new Thread(NonFairReadWriteLock::read, "子线程创建的Thread" + i).start();
            }
        }).start();

        // 所有线程同时开始执行
        countDownLatch.countDown();
    }

}
