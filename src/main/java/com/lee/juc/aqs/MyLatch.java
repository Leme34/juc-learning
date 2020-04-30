package com.lee.juc.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 利用AQS实现的极简CountdownLatch
 * <p>
 * 因为是共享锁，所以需要自己实现AQS的 tryAcquireShared 与 tryReleaseShared 方法
 * <p>
 * Created by lsd
 * 2020-04-29 23:44
 */
@Slf4j
public class MyLatch {

    private final Sync sync = new Sync();

    private class Sync extends AbstractQueuedSynchronizer {
        /**
         * @return 返回正整数则直接放行，返回负数则阻塞排队
         * {@link #acquireShared(int)}
         */
        @Override
        protected int tryAcquireShared(int arg) {
            return getState() == 666 ? 1 : -1;  //AQS的state默认=0，此处约定state=666则是门闩打开
        }

        /**
         * @return 返回true会调用doReleaseShared()唤醒队列中的线程
         * {@link #releaseShared(int)}
         */
        @Override
        protected boolean tryReleaseShared(int arg) {
            setState(666);  //约定AQS的state=666则是门闩打开
            return true;
        }
    }

    /**
     * 阻塞等待
     */
    public void await() {
        sync.acquireShared(0);
    }

    /**
     * 释放门闩
     */
    public void signal() {
        sync.releaseShared(0);
    }


    public static void main(String[] args) throws InterruptedException {
        MyLatch latch = new MyLatch();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                log.debug("等待门闩放行...");
                latch.await();
                log.debug("门闩已放行，开始执行任务");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignored) {
                }
            }).start();
        }
        // 5s后放行
        TimeUnit.SECONDS.sleep(5);
        latch.signal();
    }

}
