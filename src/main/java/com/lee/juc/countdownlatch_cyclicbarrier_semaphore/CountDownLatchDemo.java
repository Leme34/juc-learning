package com.lee.juc.countdownlatch_cyclicbarrier_semaphore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;

/**
 * 闭锁：只有所有其他线程的操作全部完成时，当前操作才继续执行
 * 此案例演示CountDownLatch的【一等多】用法，实现计算所有并发线程的总执行时间
 *
 * @see com.lee.juc.lock.readwritelock.NonFairReadWriteLock 【多等一】用法示例
 * <p>
 * Created by lsd
 * 2019-09-09 00:17
 */
public class CountDownLatchDemo {
    // 同时并发执行的线程数
    private final static int THREAD_NUM = 5;

    public static void main(String[] args) {
        // 线程数量作为参数，每个线程执行完都使 countDownLatch-1，减到 =0 即所有任务完成，闭锁释放，执行 await() 之后的代码
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);
        MyRunnable runnable = new MyRunnable(countDownLatch);

        // 开始时间
        long startTime = System.currentTimeMillis();
        // 5个线程并发执行
        for (int i = 0; i < THREAD_NUM; i++) {
            new Thread(runnable).start();
        }

        try {
            // 闭锁等待
            countDownLatch.await();
            // 结束时间
            long endTime = System.currentTimeMillis();
            System.out.println("耗费的总时间：" + (endTime - startTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("线程执行出错啦！！！");
        }

    }

}

@NoArgsConstructor
@AllArgsConstructor
class MyRunnable implements Runnable {
    // 初始化为需要等待的线程总数量
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        // TODO countDownLatch不是用来做线程安全的，而是一个做异步线程同步化的组件，所以若使用到内存共享变量需要自己保证操作的原子性
        synchronized (this) {
            try {
                for (int i = 1; i <= 10; i++) {
                    System.out.println(Thread.currentThread().getName() + "：" + i);
                }
                System.out.println("线程" + Thread.currentThread().getName() + "执行完啦~~~");
            } finally {
                // 1个线程完成，递减1
                countDownLatch.countDown();
                // count = 0 时闭锁释放，执行 await() 之后的代码
                System.out.println("countDownLatch.getCount() == " + countDownLatch.getCount());
            }
        }
    }
}
