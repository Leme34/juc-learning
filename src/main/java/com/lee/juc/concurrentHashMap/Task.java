package com.lee.juc.concurrentHashMap;

/**
 * 计算任务接口
 * @param <A> 参数泛型
 * @param <R> 返回值泛型
 */
public interface Task<A,R>{
    R compute(A args) throws InterruptedException;
}
