package com.lee.juc.lock.reentrantlock_and_condition;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示公平和不公平两种情况
 * 特例：Reentrantlock.tryLock()不遵守公平性，一旦有线程释放锁它就能争夺锁
 * 若要遵守公平性需要使用 Reentrantlock.tryLock(long timeout, TimeUnit unit)
 * <p>
 * Created by lsd
 * 2020-04-27 10:08
 */
public class FairLock {

    public static void main(String[] args) {
        PrintQueue printQueue = new PrintQueue();
        Thread[] thread = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new Job(printQueue));
        }
        for (int i = 0; i < 10; i++) {
            thread[i].start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }
}

/**
 * 打印任务
 */
class Job implements Runnable {

    PrintQueue printQueue;

    public Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "准备打印");
        printQueue.printJob(new Object());  //一式两份
        System.out.println(Thread.currentThread().getName() + "打印完毕");
    }
}


/**
 * 打印机队列
 */
class PrintQueue {

    private Lock queueLock = new ReentrantLock(false);   // TODO 公平/非公平锁开关

    /**
     * 一式两份
     *
     * @param document
     */
    public void printJob(Object document) {
        // 打印第一份
        queueLock.lock();
        try {
            int duration = new Random().nextInt(10) + 1;
            System.out.println(Thread.currentThread().getName() + "正在打印，需要" + duration + "秒");
            Thread.sleep(duration * 1000);
            System.out.println(Thread.currentThread().getName() + "第一份打印完成");
        } catch (InterruptedException ignored) {
        } finally {
            queueLock.unlock();
        }
        // 打印第二份，若是公平锁则上边释放锁后线程会重新去队尾排队，而不会直接又拿到锁去执行打印任务
        queueLock.lock();
        try {
            int duration = new Random().nextInt(10) + 1;
            System.out.println(Thread.currentThread().getName() + "正在打印，需要" + duration + "秒");
            Thread.sleep(duration * 1000);
            System.out.println(Thread.currentThread().getName() + "第二份打印完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }

}
