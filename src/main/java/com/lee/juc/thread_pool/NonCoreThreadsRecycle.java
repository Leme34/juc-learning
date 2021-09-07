package com.lee.juc.thread_pool;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 【思考】
 * 非核心线程在什么时候被回收？
 * 原文链接：https://mp.weixin.qq.com/s/9oKyX6Bq4MtVt5_80jqokA
 *
 * 【问题】
 * 如果当前线程池的活跃线程是 3 个（2 个核心线程+ 1 个非核心线程），但是它们各自的任务都执行完成了。
 * 然后我每隔 3 秒往线程池里面扔一个耗时 1 秒的任务。那么 30 秒之后，活跃线程数是多少？
 *
 * 【答案】
 * 还是 3 个。
 *
 * 【解析】
 * 一开始一共五个任务，三个线程都在执行任务，然后率先完成了任务的两个线程把队列里面的 2 个任务拿出来执行，
 * 接下来，每隔 3 秒就有一个耗时 1 秒的任务过来。而此时线程池里面的三个活跃线程都是空闲状态。
 * 虽然线程都是空闲的，但是当任务来的时候不是随机调用的，而是轮询。
 * 由于是轮询，每三秒执行一次，所以非核心线程的空闲时间最多也就是 9 秒，不会超过 30 秒，所以一直不会被回收。
 *
 * （1）为什么是轮询？
 * 因为底层使用的是 AQS 的等待队列。
 * （2）什么顺序呢？
 * Condition 里面的等待队列里面的顺序。
 * （3）非核心线程怎么回收？
 * 超过设定的非核心线程空闲时间
 * （4）被回收的这个线程是核心线程还是非核心线程呢？
 * 不知道。
 * 因为在线程池里面，核心线程和非核心线程仅仅是一个概念而已，其实拿着一个线程，我们并不能知道它是核心线程还是非核心线程。
 * 这个地方就是一个证明，因为当工作线程多余核心线程数之后，所有的线程都在 poll，也就是说所有的线程都有可能被回收。
 *
 * Created by lsd
 * 2021-09-07 22:35
 */
public class NonCoreThreadsRecycle {

    @Test
    public void test() throws InterruptedException {

        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, 3, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2), new DefaultThreadFactory("test"),
                new ThreadPoolExecutor.DiscardPolicy());

        //每隔两秒打印线程池的信息
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("=====================================thread-pool-info:" + new Date() + "=====================================");
            System.out.println("CorePoolSize:" + executorService.getCorePoolSize());
            System.out.println("PoolSize:" + executorService.getPoolSize());
            System.out.println("ActiveCount:" + executorService.getActiveCount());
            System.out.println("KeepAliveTime:" + executorService.getKeepAliveTime(TimeUnit.SECONDS));
            System.out.println("QueueSize:" + executorService.getQueue().size());
        }, 0, 2, TimeUnit.SECONDS);

        try {
            //同时提交5个任务,模拟达到最大线程数
            for (int i = 0; i < 5; i++) {
                executorService.execute(new Task());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //休眠10秒，打印日志，观察线程池状态
        Thread.sleep(10000);

        //每隔3秒提交一个任务
        while (true) {
            Thread.sleep(3000);
            executorService.submit(new Task());
        }
    }

    static class Task implements Runnable {
        @Override
        public void run(){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + "-执行任务");
        }
    }

}
