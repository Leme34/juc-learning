package com.lee.juc.cancellableTask;

import java.util.concurrent.*;

/**
 * 支持生产 可定制的 Callable 任务 的线程池
 *
 * Created by lsd
 * 2019-10-25 00:50
 */
public class CancellingExecutor extends ThreadPoolExecutor {

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * 改写 FutureTask 的工厂方法
     * 使其支持 由定制的 Cancellable 对象 生产出 定制化的 FutureTask，并把 定制的取消操作 封装在其中
     *
     * @param callable 用于创建 FutureTask 的 callable 对象
     * @param <T>      public <T> 表示泛型方法
     */
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        // 若是定制化的 callable ，创建定制化的 FutureTask
        return callable instanceof Cancellable ?
                ((Cancellable<T>) callable).newTask() : super.newTaskFor(callable);
    }
}
