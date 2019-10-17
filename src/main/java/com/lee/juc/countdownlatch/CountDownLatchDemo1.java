package com.lee.juc.countdownlatch;

import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by lsd
 * 2019-10-04 18:31
 */
class MyCountDownLatchDemo {

    /**
     * n个线程并发执行某个任务所需要的时间
     *
     * @param nThreads 线程数
     * @param task     任务
     * @return 所需要的时间
     */
    @SneakyThrows
    public long timeTasks(int nThreads, final Runnable task) {
        var startGate = new CountDownLatch(1);
        var endGate = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            new Thread(() -> {
                try {
                    // 阻塞等待
                    startGate.await();
                    try {
                        task.run();
                    } finally {
                        // 任务结束计数
                        endGate.countDown();
                    }
                } catch (InterruptedException ignored) {
                }
            }).start();
        }
        long start = System.currentTimeMillis();
        // 任务开始
        startGate.countDown();
        // 阻塞等待所有任务结束
        endGate.await();
        long end = System.currentTimeMillis();
        return end - start;
    }
}

public class CountDownLatchDemo1 {

    public static void main(String[] args) {
        final long timeSum = new MyCountDownLatchDemo().timeTasks(5,
                () -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
        System.out.println(timeSum/1000.0);
    }
}
