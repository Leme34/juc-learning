package com.lee.juc.lock.readwritelock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁的升/降级
 * 结论：只能降级，不支持升级（因为多个读线程都想升级为写锁时导致都在相互等待释放读锁，则可能造成死锁）
 * <p>
 * Created by lsd
 * 2020-04-27 10:37
 */
public class UpgradingReadWriteLock {

    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private static ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    /**
     * 读锁升级：ReentrantReadWriteLock不允许升级，必定升级失败
     */
    private static void readUpgrading() {
        String threadName = Thread.currentThread().getName();
        readLock.lock();
        try {
            System.out.println(threadName + "得到了读锁，正在读取...");
            Thread.sleep(1000);
            // 尝试升级为写锁
            System.out.println("正在升级为写锁...");
            writeLock.lock();
            System.out.println(threadName + "成功升级为写锁...");  //必定升级失败，此语句不会输出
        } catch (InterruptedException ignored) {
        } finally {
            System.out.println(threadName + "释放读锁");
            readLock.unlock();
        }
    }

    /**
     * 写锁降级：在不释放写锁的情况下直接获取读锁即可
     * 可用于一个先写后读的任务，写完之后马上降级为读锁与其他读线程并行，从而提高效率
     */
    private static void writeDowngrading() {
        String threadName = Thread.currentThread().getName();
        writeLock.lock();
        try {
            System.out.println(threadName + "得到了写锁，正在写入...");
            Thread.sleep(1000);
            // 尝试降级为读锁：在不释放写锁的情况下直接获取读锁即可降级
            System.out.println(threadName + "写操作已完成，正在降级为读锁...");
            readLock.lock();
            System.out.println(threadName + "成功降级为读锁...");
            Thread.sleep(1000); //模拟其他读操作
            System.out.println(threadName + "读操作已完成");
        } catch (InterruptedException ignored) {
        } finally {
            System.out.println(threadName + "释放读锁");
            readLock.unlock();
            System.out.println(threadName + "释放写锁");
            writeLock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 演示锁降级成功
        Thread thread1 = new Thread(UpgradingReadWriteLock::writeDowngrading, "Thread1");
        thread1.start();
        thread1.join();
        System.out.println("=============================================================");
        // 演示锁升级失败
        new Thread(UpgradingReadWriteLock::readUpgrading, "Thread2").start();

    }

}
