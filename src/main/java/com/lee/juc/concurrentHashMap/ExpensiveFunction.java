package com.lee.juc.concurrentHashMap;

import com.lee.juc.future.TestFutureTask;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 耗时计算类
 * <p>
 * Created by lsd
 * 2019-10-21 09:01
 */
public class ExpensiveFunction implements Task<String, BigInteger> {
    @Override
    public BigInteger compute(String args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        return new BigInteger(args);
    }
}


/**
 * 基于 synchronized 的缓存封装层
 * <p>
 * 缺点：串行执行，性能差，非原子
 */
class Cache1<A, R> implements Task<A, R> {
    private final Task<A, R> task;

    public Cache1(Task<A, R> task) {
        this.task = task;
    }

    private final Map<A, R> cacheMap = new HashMap<>();

    // synchronized 保证HashMap的并发安全
    @Override
    public synchronized R compute(A args) throws InterruptedException {
        R result = cacheMap.get(args);
        if (result == null) {
            result = task.compute(args);
            cacheMap.put(args, result);
        }
        return result;
    }
}

/**
 * 使用 ConcurrentHashMap 代替 synchronized
 * <p>
 * @formatter:off
 * 缺点：1.并发会导致前一个正在计算未放入缓存，其他线程访问又开始一次计算
 *      2.非原子
 * @formatter:on
 */
class Cache2<A, R> implements Task<A, R> {
    private final Task<A, R> task;

    public Cache2(Task<A, R> task) {
        this.task = task;
    }

    private Map<A, R> cacheMap = new ConcurrentHashMap<>();

    @Override
    public R compute(A args) throws InterruptedException {
        R result = cacheMap.get(args);
        if (result == null) {
            result = task.compute(args);
            cacheMap.put(args, result);
        }
        return result;
    }
}

/**
 * cacheMap 改为缓存持有异步计算结果的 Future 对象
 * 通过判断 Future对象是否为 null ：
 * 1.若不为空则已创建过计算任务，正在计算
 * 2.否则创建任务 并 开始计算
 * <p>
 * 缺点：非原子
 */
class Cache3<A, R> implements Task<A, R> {
    private final Task<A, R> task;

    public Cache3(Task<A, R> task) {
        this.task = task;
    }

    private final Map<A, Future<R>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public R compute(A args) throws InterruptedException {
        Future<R> future = cacheMap.get(args);
        if (future == null) {
            FutureTask<R> futureTask = new FutureTask<>(() -> task.compute(args));
            // 放入缓存
            future = futureTask;
            cacheMap.put(args, future);
            // 开始任务
            futureTask.run();
        }
        // 返回计算结果
        try {
            return future.get();
        } catch (CancellationException e) {
            throw new CancellationException("取消异常，任务被取消啦~");
        } catch (ExecutionException e) {
            throw TestFutureTask.launderThrowable(e.getCause());
        }
    }
}

/**
 * 使用 ConcurrentHashMap 的 putIfAbsent 保证原子性 【最终实现】
 * <p>
 * 缺点：
 * 1.若 future 任务被取消或出错需要清除缓存
 * 2.无法解决缓存清理 和 过期时间问题
 */
class Cache4<A, R> implements Task<A, R> {
    private final Task<A, R> task;

    public Cache4(Task<A, R> task) {
        this.task = task;
    }

    private final Map<A, Future<R>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public R compute(A args) throws InterruptedException {
        Future<R> future = cacheMap.get(args);
        if (future == null) {
            FutureTask<R> futureTask = new FutureTask<>(() -> task.compute(args));
            // 原子性操作：若缓存中没有则缓存并开始任务，若缓存中已有则直接返回任务对象
            future = cacheMap.putIfAbsent(args, futureTask);
            if (future == null) {  // 缓存中没有
                future = futureTask;
                futureTask.run();
            }
        }
        // 返回结果
        try {
            return future.get();
        } catch (CancellationException e) {
            throw new CancellationException("取消异常，任务被取消啦~");
        } catch (ExecutionException e) {
            throw TestFutureTask.launderThrowable(e.getCause());
        }
    }

}
