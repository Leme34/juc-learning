package com.lee.thread.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 子线程未捕获异常处理的最佳实践
 * <p>
 * Created by lsd
 * 2020-04-24 16:32
 */
@Slf4j
public class UnCaughtExceptionLearning {

    /**
     * 错误做法，传统的异常捕获方法并不能捕获到子线程抛出的异常
     */
    private static void errorWay() {
        try {
            new Thread(() -> {
                int i = 1 / 0;
            }).start();
        } catch (Exception e) {  //错误：此处并不能捕获到子线程抛出的异常，因为try catch是针对当前线程的
            log.error("捕获到子线程抛出的异常", e);
        }
        log.debug("主线程结束");
    }


    /**
     * 做法1【不太推荐】：在每个子线程run方法中捕获异常
     */
    private static void way1() {
        new Thread(() -> {
            try {
                int i = 1 / 0;
            } catch (RuntimeException e) {
                log.error("捕获到子线程抛出的异常", e);
            }
        }).start();
        log.debug("主线程结束");
    }

    /**
     * 做法2【推荐】：使用 UnCaughtExceptionHandler 统一处理
     */
    private static void way2() {
        // 使用我们自定义的UnCaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(new MyUnCaughtExceptionHandler());
        new Thread(() -> {
            int i = 1 / 0;
        }).start();
        log.debug("主线程结束");
    }


    public static void main(String[] args) {
//        errorWay();
//        way1();
        way2();
    }


}
