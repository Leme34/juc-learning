package com.lee.juc.threadlocal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 解决SimpleDateFormat的线程安全问题
 */
public class SimpleDateFormatUsage {

    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 某个线程 初次调用ThreadLocal.get方法 && 该ThreadLocal未set过值 时会调用initialValue获取初始值
    private static AtomicInteger callTimes = new AtomicInteger();
    private final static ThreadLocal<SimpleDateFormat> threadLocal = ThreadLocal.withInitial(
            () -> {
                callTimes.getAndIncrement();  //记录initialValue方法被调用的次数
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
    );


    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            threadPool.submit(() -> {
//                String date = new SimpleDateFormatUsage().dateBySynchronized(finalI);  //解决办法1：悲观锁
                String date = new SimpleDateFormatUsage().dateByThreadLocal(finalI);  //解决办法2：ThreadLocal
                System.out.println(date);
            });
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
        System.out.println("ThreadLocal.initialValue()被调用总次数为：" + callTimes);
    }

    /**
     * 解决办法1：悲观锁【性能低下】，使用Synchronized保证线程安全
     *
     * @param seconds 参数的单位是毫秒，从1970.1.1 00:00:00 GMT计时
     * @return 格式化后的日期字符串
     */
    public String dateBySynchronized(int seconds) {
        Date date = new Date(1000 * seconds);
        String s;
        synchronized (SimpleDateFormatUsage.class) {
            s = dateFormat.format(date);
        }
        return s;
    }

    /**
     * 解决办法2：ThreadLocal，每个线程都维护一个自己的SimpleDateFormat对象
     *
     * @param seconds 参数的单位是毫秒，从1970.1.1 00:00:00 GMT计时
     * @return 格式化后的日期字符串
     */
    public String dateByThreadLocal(int seconds) {
        Date date = new Date(1000 * seconds);
        SimpleDateFormat dateFormat = threadLocal.get();
        return dateFormat.format(date);
    }


}
