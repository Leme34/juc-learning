package com.lee.juc.completablefuture._04_CompletableFuture_performance;

import org.ph.share.SmallTool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 禁止线程复用的线程池，可用于验证之前的例子中由于线程复用的存在不能根据线程id看出现象的问题
 * 仅供学习测试，禁止生产环境使用！！！
 * https://www.bilibili.com/video/BV1e44y1677h/
 */
public class _06_thenRunAsync_threadReuse {
    public static void main(String[] args) {
        // 存活时间为0，一执行完马上销毁线程
        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                0, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>());

        CompletableFuture.runAsync(() -> SmallTool.printTimeAndThread("A"), executor)
                .thenRunAsync(() -> SmallTool.printTimeAndThread("B"), executor)
                .join();

    }
}
