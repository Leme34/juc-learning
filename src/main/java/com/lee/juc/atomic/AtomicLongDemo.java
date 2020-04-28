package com.lee.juc.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 演示高并发场景下AtomicLong的性能较低
 *
 * @see LongAdderDemo 高并发场景比AtomicLong效率更高的原子操作类
 * <p>
 * Created by lsd
 * 2020-04-28 07:39
 */
public class AtomicLongDemo {

    private static AtomicLong num = new AtomicLong(0);
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        // 1w个任务，并发累加10000次num
        for (int i = 0; i < 10000; i++) {
            threadPool.submit(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException ignored) {
                }
                for (int j = 0; j < 10000; j++) {
                    num.getAndIncrement();
                }
            });
        }
        long start = System.currentTimeMillis();
        countDownLatch.countDown();
        // 等待所有任务执行完成
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
        System.out.println("结果：" + num.get() + "，耗时：" + (System.currentTimeMillis() - start));

    }

}
