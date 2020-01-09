package com.lee.juc.countdownlatch_cyclicbarrier_semaphore;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 互斥资源访问，相当于操作系统临界资源的 PV 操作
 * 当 new Semaphore(1); 时，可以实现 Synchronized 和 lock 的同步锁功能
 */
@Slf4j
public class SemaphoreDemo {
    public static void main(String[] args) {
        // 3个互斥资源
        Semaphore semaphore = new Semaphore(3);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    log.info("此Semaphore对象中当前可用的许可数：" + semaphore.availablePermits());
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            });
        }
        executorService.shutdown();
    }
}
