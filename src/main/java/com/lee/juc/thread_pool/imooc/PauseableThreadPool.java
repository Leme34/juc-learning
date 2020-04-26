package com.lee.juc.thread_pool.imooc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 利用每个任务执行前的钩子(回调)函数实现可暂停线程池
 * <p>
 * Created by lsd
 * 2020-04-26 17:00
 */
@Slf4j
public class PauseableThreadPool extends ThreadPoolExecutor {

    private boolean isPause; //是否已暂停
    ReentrantLock lock = new ReentrantLock();
    Condition unpaused = lock.newCondition();

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 每个任务执行前的钩子(回调)函数
     * 每个任务执行前都检查当前线程池的暂停标志，若已被暂停则锁住线程
     *
     * @param t
     * @param r
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            while (isPause) {
                unpaused.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    /**
     * 暂停线程池（设置线程池暂停标志）
     */
    private void pause() {
        log.debug("请求暂停线程池...");
        lock.lock();
        try {
            isPause = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 恢复线程池（设置线程池暂停标志+释放锁）
     */
    private void resume() {
        log.debug("请求恢复线程池...");
        lock.lock();
        try {
            isPause = false;
            unpaused.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PauseableThreadPool pauseableThreadPool = new PauseableThreadPool(
                10,
                20,
                10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        Runnable task = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                log.debug("finished...");
            } catch (InterruptedException e) {
                log.error("interrupted...");
            }
        };
        for (int i = 0; i < 100; i++) {
            pauseableThreadPool.execute(task);
        }
        //5s后暂停线程池
        TimeUnit.SECONDS.sleep(5);
        pauseableThreadPool.pause();
        //5s后恢复线程池
        TimeUnit.SECONDS.sleep(5);
        pauseableThreadPool.resume();
    }


}
