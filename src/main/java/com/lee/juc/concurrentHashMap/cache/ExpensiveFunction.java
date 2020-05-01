package com.lee.juc.concurrentHashMap.cache;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/*
 *   基于装饰器模式演进线程安全的缓存
 */

/**
 * 耗时计算类，例如查询数据库的操作
 * <p>
 * Created by lsd
 * 2019-10-21 09:01
 */
@Slf4j
public class ExpensiveFunction implements ComputeTask<String, BigInteger> {
    @Override
    public BigInteger compute(String args) throws InterruptedException {
        log.debug("耗时任务开始计算...");
        TimeUnit.SECONDS.sleep(10);
        log.debug("计算完成...");
        return new BigInteger(args);
    }
}


/**
 * 基于 synchronized 的缓存封装层
 * <p>
 * 缺点：串行执行，性能差
 */
class Cache1<A, R> implements ComputeTask<A, R> {
    private final ComputeTask<A, R> computeTask;    //被装饰的耗时计算任务

    public Cache1(ComputeTask<A, R> computeTask) {
        this.computeTask = computeTask;
    }

    private final Map<A, R> cacheMap = new HashMap<>();

    // synchronized 保证HashMap的并发安全
    @Override
    public synchronized R compute(A args) throws InterruptedException, ExecutionException {
        R result = cacheMap.get(args);
        if (result == null) {
            result = computeTask.compute(args);
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
 *      2.ConcurrentHashMap”若没有则添加“的操作是非原子的
 * @formatter:on
 */
class Cache2<A, R> implements ComputeTask<A, R> {
    private final ComputeTask<A, R> computeTask;     //被装饰的耗时计算任务

    public Cache2(ComputeTask<A, R> computeTask) {
        this.computeTask = computeTask;
    }

    private Map<A, R> cacheMap = new ConcurrentHashMap<>();

    @Override
    public R compute(A args) throws InterruptedException, ExecutionException {
        R result = cacheMap.get(args);
        if (result == null) {
            result = computeTask.compute(args);
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
 * 缺点：ConcurrentHashMap”若没有则添加“的操作是非原子的
 * 例如：若有多个线程并发查询同一个key，且该key未被缓存，则这些线程会同时开始计算这个key并放入缓存，造成重复计算
 */
class Cache3<A, R> implements ComputeTask<A, R> {
    private final ComputeTask<A, R> computeTask;    //被装饰的耗时计算任务

    public Cache3(ComputeTask<A, R> computeTask) {
        this.computeTask = computeTask;
    }

    private final Map<A, Future<R>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public R compute(A args) throws InterruptedException, ExecutionException {
        Future<R> future = cacheMap.get(args);
        if (future == null) {
            FutureTask<R> futureTask = new FutureTask<>(() -> computeTask.compute(args));
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
            throw new CancellationException("任务被取消");
        } catch (ExecutionException e) {
            throw e;
        }
    }
}

/**
 * 使用 ConcurrentHashMap 的 putIfAbsent 保证原子性
 * <p>
 * 缺点：
 * 1.若 future 任务被取消 或 出错则需要清除缓存，因为缓存中的 future 的结果已经是之前出错的结果了，再次get()仍然是抛异常（缓存污染）
 * 2.无法解决缓存清理 和 过期时间问题
 */
class Cache4<A, R> implements ComputeTask<A, R> {
    private final ComputeTask<A, R> computeTask;    //被装饰的耗时计算任务

    public Cache4(ComputeTask<A, R> computeTask) {
        this.computeTask = computeTask;
    }

    private final Map<A, Future<R>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public R compute(A args) throws InterruptedException, ExecutionException {
        Future<R> future = cacheMap.get(args);
        if (future == null) {
            FutureTask<R> futureTask = new FutureTask<>(() -> computeTask.compute(args));
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
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }

}


/**
 * 使用 ConcurrentHashMap 的 putIfAbsent 保证原子性 【缓存污染处理】
 * <p>
 * 缺点：无法解决缓存清理 和 过期时间问题
 */
@Slf4j
class Cache5<A, R> implements ComputeTask<A, R> {
    private final ComputeTask<A, R> computeTask;    //被装饰的耗时计算任务

    public Cache5(ComputeTask<A, R> computeTask) {
        this.computeTask = computeTask;
    }

    private final Map<A, Future<R>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public R compute(A args) throws InterruptedException {
        //一直重试计算直到成功返回
        while (true) {
            Future<R> future = cacheMap.get(args);
            if (future == null) {
                FutureTask<R> futureTask = new FutureTask<>(() -> computeTask.compute(args));
                // 原子性操作：若缓存中没有则缓存并开始任务，若缓存中已有则直接返回任务对象
                future = cacheMap.putIfAbsent(args, futureTask);
                if (future == null) {  // 缓存中没有
                    future = futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();   // 计算成功才返回结果
            } catch (CancellationException e) {
                cacheMap.remove(args);   //清理已被污染的缓存
                throw new CancellationException("任务被取消");
            } catch (InterruptedException e) {
                cacheMap.remove(args);   //清理已被污染的缓存
                throw e;
            } catch (ExecutionException e) {
                cacheMap.remove(args);   //清理已被污染的缓存，不抛出异常再次重试
                log.info("计算失败，正在重试", e);
            }
        }
    }

}


/**
 * 使用 ConcurrentHashMap 的 putIfAbsent 保证原子性 【支持缓存过期时间、随机TTL解决缓存雪崩】【最终版】
 */
@Slf4j
class Cache6<A, R> implements ComputeTask<A, R> {
    private final ComputeTask<A, R> computeTask;    //被装饰的耗时计算任务

    public Cache6(ComputeTask<A, R> computeTask) {
        this.computeTask = computeTask;
    }

    private final Map<A, Future<R>> cacheMap = new ConcurrentHashMap<>();
    // 用于定时清理过期缓存的线程池
    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    /**
     * 带有超时的缓存
     *
     * @param args
     * @param expire
     * @param timeUnit
     */
    public R compute(A args, long expire, TimeUnit timeUnit) throws InterruptedException {
        if (expire > 0) {
            this.addExpireTask(args, expire, timeUnit);
        }
        return this.compute(args);
    }

    /**
     * 增加缓存过期时的清理任务
     *
     * @param key
     * @param expire
     * @param timeUnit
     */
    private void addExpireTask(A key, long expire, TimeUnit timeUnit) {
        scheduledThreadPool.schedule(() -> {
            // 到期后取出Future，若未执行完则取消
            Future<R> future = cacheMap.get(key);
            if (!future.isDone()) {
                log.debug("Callable任务未执行完，已被取消");
                future.cancel(true);
            }
            cacheMap.remove(key);
            log.debug("过期缓存已被清理：key={}", key);
        }, expire, timeUnit);
    }

    /**
     * 随机过期时间，防止缓存雪崩
     */
    public R computeRandomExpire(A args) throws ExecutionException, InterruptedException {
        return this.compute(args, (long) (Math.random() * 10000), TimeUnit.MILLISECONDS);
    }

    @Override
    public R compute(A args) throws InterruptedException {
        //一直重试计算直到成功返回
        while (true) {
            Future<R> future = cacheMap.get(args);
            if (future == null) {
                FutureTask<R> futureTask = new FutureTask<>(() -> computeTask.compute(args));
                // 原子性操作：若缓存中没有则缓存并开始任务，若缓存中已有则直接返回任务对象
                future = cacheMap.putIfAbsent(args, futureTask);
                if (future == null) {  // 缓存中没有
                    future = futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();   // 计算成功才返回结果
            } catch (CancellationException e) {
                cacheMap.remove(args);   //清理已被污染的缓存
                throw new CancellationException("任务被取消");
            } catch (InterruptedException e) {
                cacheMap.remove(args);   //清理已被污染的缓存
                throw e;
            } catch (ExecutionException e) {
                cacheMap.remove(args);   //清理已被污染的缓存，不抛出异常再次重试
                log.info("计算失败，正在重试", e);
            }
        }
    }

}
