package com.lee.juc.cancellableTask;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * 可定制的 Callable 任务
 *
 * Created by lsd
 * 2019-10-25 00:20
 */
public interface Cancellable<T> extends Callable<T> {

    /**
     * 可定制的任务取消行为
     */
    void cancel();

    /**
     * 创建可定制的 RunnableFuture（ FutureTask 的父类） 对象
     */
    RunnableFuture<T> newTask();

}
