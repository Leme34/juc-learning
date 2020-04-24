package com.lee.thread.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义未捕获异常全局处理器
 * <p>
 * Created by lsd
 * 2020-04-24 19:36
 */
@Slf4j
public class MyUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 子线程异常处理
     *
     * @param t 线程
     * @param e 异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("线程" + t.getName() + "发生异常", e);
    }

}
