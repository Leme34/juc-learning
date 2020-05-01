package com.lee.juc.concurrentHashMap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ConcurrentHashMap只能保证多线程并发put操作的线程安全 而 不能保证非原子操作的线程安全
 * ConcurrentHashMap提供的保证非原子操作线程安全的API：replace、putIfAbsent
 * <p>
 * Created by lsd
 * 2020-04-29 09:42
 */
public class CombinedOperationSafely {

    private static ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        String key = "小明";
        concurrentHashMap.put(key, 0);
        Runnable task = () -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {
            }
            for (int i = 0; i < 1000; i++) {
                while (true) {
                    Integer oldScore = concurrentHashMap.get(key);
//                concurrentHashMap.put(key, ++oldScore);    //先get后put是非原子操作，存在线程安全问题
                    //原子的检查并替换操作，类似cas思想，因此使用自旋，成功才退出循环
                    boolean success = concurrentHashMap.replace(key, oldScore, ++oldScore);
                    if (success) {
                        break;
                    }
                }
            }
        };
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new Thread(task));
        }
        // 所有线程同时开始执行
        countDownLatch.countDown();
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
        System.out.println(concurrentHashMap);
    }

}
