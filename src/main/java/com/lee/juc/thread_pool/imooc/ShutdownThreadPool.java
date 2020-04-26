package com.lee.juc.thread_pool.imooc;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by lsd
 * 2020-04-26 16:18
 */
@Slf4j
public class ShutdownThreadPool {

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                log.debug("finished...");
            } catch (InterruptedException e) {
                log.error("interrupted...");
            }
        };
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            fixedThreadPool.execute(task);
        }
        TimeUnit.SECONDS.sleep(2);  //等待线程池运行2s
        List<Runnable> notCompleteTask = fixedThreadPool.shutdownNow();  //强制关闭线程池，返回从未开始执行的任务列表
    }

    /**
     * 优雅关闭线程池
     * @param fixedThreadPool
     * @throws InterruptedException
     */
    private static void shutdown(ExecutorService fixedThreadPool) throws InterruptedException {
        fixedThreadPool.shutdown();  //优雅关闭，会等待任务执行完
        // 虽然不会马上关闭线程池但是会拒绝任务提交，
        log.debug("isShutdown = {}", fixedThreadPool.isShutdown()); //关闭后会立刻变为true
        log.debug("isTerminated = {}", fixedThreadPool.isTerminated()); //线程池是否完全停止
//        fixedThreadPool.execute(task);  //关闭后还提交任务会抛出异常
        TimeUnit.SECONDS.sleep(20);  //等待线程池运行完队列中剩余任务
        log.debug("isTerminated = {}", fixedThreadPool.isTerminated()); //线程池是否完全停止
    }

}
