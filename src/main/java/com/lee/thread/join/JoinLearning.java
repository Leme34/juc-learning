package com.lee.thread.join;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Join用法演示，以及Join中断响应方法
 * <p>
 * Created by lsd
 * 2020-04-24 14:26
 */
@Slf4j
public class JoinLearning {

    public static void main(String[] args) {
        // 获取主线程引用，用于中断主线程的等待
        Thread mainThread = Thread.currentThread();
        Runnable task = () -> {
            try {
                mainThread.interrupt(); //中断主线程的等待，但是子线程仍会继续执行，因此需要在主线程的中断catch中传递中断
                TimeUnit.SECONDS.sleep(5);
                log.debug("任务完成");
            } catch (InterruptedException e) {
                log.error("任务被中断");
            }
        };
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        // 主线程等待所有子线程执行完成
        log.debug("主线程开始Join等待");
        try {
//            t1.join();
//            t2.join();
            // 根据源码得到以下join的等价实现
            // 为什么join的java源码只有wait没有notify却能通知到主线程继续运行？
            // 在Thread类的底层c++的exit方法调用了ensure_join方法，其中有notifyAll语句。这也是为什么不推荐使用Thread对象作为对象锁的原因（会在底层自动释放锁）。
            synchronized (t1) {
                t1.wait();
            }
            synchronized (t2) {
                t2.wait();
            }
        } catch (InterruptedException e) {
            log.error("主线程的Join等待被中断");
            // 中断子线程
            t1.interrupt();
            t2.interrupt();
        }
        log.debug("主线程完成");
    }

}
