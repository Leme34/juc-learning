package com.lee.juc.thread_pool;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 分别使用Thread.join() 和 单线程池 实现线程安全地修改List，并对比时间来说明线程池的快
 * <p>
 * Created by lsd
 * 2019-11-25 20:31
 */
public class TestSingleThreadExecutor {

    @SneakyThrows
    public static void testJoin() {
        var list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Thread thread = new Thread(() -> list.add("test"));
            thread.start();
            // 等待刚刚启动的线程结束
            thread.join();
        }
        System.out.println("list.size()=" + list.size());
    }

    @SneakyThrows
    public static void testSingleThreadExecutor() {
        var list = new ArrayList<>();
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10000; i++) {
            // submit将会阻塞直到FutureTask.get完成并返回，
            // 若使用execute，将不会阻塞所以主线程直接提交完任务还没完成就结束了
            singleThreadExecutor.submit(() -> list.add("test")).get();
        }
        System.out.println("list.size()=" + list.size());
        singleThreadExecutor.shutdown();
    }


    public static void main(String[] args) {
        final long start = System.currentTimeMillis();
//        testJoin();
        testSingleThreadExecutor();
        System.out.println("耗时：" + (System.currentTimeMillis() - start) / 1000.0);
    }

}
