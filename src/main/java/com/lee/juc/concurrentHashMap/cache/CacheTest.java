package com.lee.juc.concurrentHashMap.cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试使用ConcurrentHashMap实现的缓存【最终版】
 * <p>
 * Created by lsd
 * 2020-05-01 11:47
 */
public class CacheTest {

    //使用最终迭代出的缓存实现
    private final static Cache6 cache = new Cache6(new ExpensiveFunction());
    private final static CountDownLatch countdownLatch = new CountDownLatch(1);
    private final static ThreadLocal<SimpleDateFormat> threadLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

    public static void main(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 10000; i++) {
            fixedThreadPool.execute(() -> {
                try {
                    countdownLatch.await();
                } catch (InterruptedException ignored) {
                }
                try {
                    SimpleDateFormat df = threadLocal.get();
                    System.out.println(Thread.currentThread().getName() + "开始计算时间：" + df.format(new Date()));
                    System.out.println(cache.compute("666"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        countdownLatch.countDown();
        fixedThreadPool.shutdown();
    }

}
