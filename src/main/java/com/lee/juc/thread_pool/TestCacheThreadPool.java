package com.lee.juc.thread_pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lsd
 * 2019-11-25 21:27
 */
public class TestCacheThreadPool {

    public static void main(String[] args) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(() -> System.out.println("execute"));
        cachedThreadPool.submit(() -> "submit");
        cachedThreadPool.shutdown();
    }

}
