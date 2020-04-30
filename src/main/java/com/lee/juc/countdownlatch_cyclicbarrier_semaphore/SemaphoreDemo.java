package com.lee.juc.countdownlatch_cyclicbarrier_semaphore;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 控制互斥资源访问的信号量，相当于操作系统临界资源的 PV 操作
 * 当 new Semaphore(1); 时，可以实现 Synchronized 和 lock 的同步锁功能
 * 注意：
 * 1.同一个线程获取与归还的许可数量必须一致，否则许可会越丢越多，导致死锁
 * 2.为了避免饥饿，一般需要设置为公平
 * <p>
 * 其他使用场景：
 * 1.如何实现两个互斥方法(不能同时运行)？
 *   我们假设共有5个信号量，那么要求耗时的A方法需要5个许可才能运行，而B方法只需要1个许可即可运行，这样避免了两个方法同时运行
 * 2.假设线程1需要等待线程2完成后才开始执行，若不使用CountDownLatch，如何使用Semaphore实现？
 *   线程1调用acquire()，线程2完成后调用release()
 */
@Slf4j
public class SemaphoreDemo {
    public static void main(String[] args) {
        // 3个互斥资源
        Semaphore semaphore = new Semaphore(3);  //默认非公平，若公平则等待时间越长越优先(排队)
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();  //获取1个许可
                    log.info("此Semaphore对象中当前可用的许可数：" + semaphore.availablePermits());
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();  //归还1个许可
                }
            });
        }
        executorService.shutdown();
    }
}
