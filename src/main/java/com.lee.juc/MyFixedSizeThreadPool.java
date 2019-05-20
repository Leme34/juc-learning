package com.lee.juc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 手写 FixedSizeThreadPool
 */
public class MyFixedSizeThreadPool {

    // 标志线程池是否被关闭
    private volatile boolean isWorking = true;

    // 阻塞队列
    private LinkedBlockingQueue<Runnable> blockingQueue;
    // 工作线程集合
    private List<Thread> workers;

    /**
     * 池中的工作线程（静态内部类）
     */
    public static class worker extends Thread {

        private MyFixedSizeThreadPool threadPool;

        public worker(MyFixedSizeThreadPool threadPool) {
            this.threadPool = threadPool;
        }

        @Override
        public void run() {
            Runnable task = null;
            // TODO 2、
            // 若线程池未关闭，则阻塞地获取队列的任务执行（队列为空时会阻塞当前线程）
            // 若线程池被关闭，则需要非阻塞地获取任务执行（队列为空时不会阻塞当前线程）
            while (this.threadPool.isWorking || this.threadPool.blockingQueue.size() > 0) {
                try {
                    if (this.threadPool.isWorking) {
                        task = threadPool.blockingQueue.take();
                    } else {
                        task = threadPool.blockingQueue.poll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 若有获取到任务，则执行
                if (task != null) {
                    task.run();
                    System.out.println("线程：" + Thread.currentThread().getName() + "执行完毕~");
                }
            }
        }
    }

    /**
     * 线程池构造函数
     *
     * @param poolSize  池中线程数量
     * @param queueSize 池中阻塞队列大小
     */
    public MyFixedSizeThreadPool(int poolSize, int queueSize) {

        // 参数校验
        if (poolSize <= 0 || queueSize <= 0) {
            throw new IllegalArgumentException("非法参数");
        }

        workers = Collections.synchronizedList(new ArrayList<>(poolSize));
        blockingQueue = new LinkedBlockingQueue<>(queueSize);

        // 创建出poolSize个工作线程，并初始化
        for (int i = 0; i < poolSize; i++) {
            worker worker = new worker(this);
            // 开始取任务执行
            worker.start();
            // 加入工作线程集合
            workers.add(worker);
        }
    }

    /**
     * 向阻塞队列提交任务（若队列已满不会阻塞，直接返回false）
     *
     * @return 在不超出队列容量的情况下，则将指定的元素插入此队列的尾部，返回 true
     * 如果此队列已满，则返回false
     */
    public boolean submit(Runnable task) {
        // TODO 1、若线程池已被关闭则不能添加任务
        if (!this.isWorking) {
            return false;
        }
        return this.blockingQueue.offer(task);
    }

    /**
     * 向阻塞队列提交任务（若队列已满会阻塞，直到能够插入）
     *
     * @return 在不超出队列容量的情况下，则将指定的元素插入此队列的尾部，返回 true
     * 如果此队列已满，则返回false
     */
    public void execute(Runnable task) {
        try {
            this.blockingQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭线程池
     * 1、阻止新的任务加入
     * 2、执行完阻塞队列中的任务，但是再从队列取任务时需要非阻塞地取
     * 3、强行中断 等待、阻塞 状态的线程
     */
    public void shutdown() {
        // 标记为关闭状态
        this.isWorking = false;
        // TODO 3、强行中断 等待、阻塞 状态的线程
        for (Thread worker : workers) {
            if (worker.getState().equals(Thread.State.WAITING) ||
                    worker.getState().equals(Thread.State.BLOCKED)) {
                worker.interrupt();
            }
        }
    }

    public static void main(String[] args) {
        MyFixedSizeThreadPool pool = new MyFixedSizeThreadPool(3, 6);
        for (int j = 0; j < 12; j++) {
//            pool.submit(() -> {
//                System.out.println("一个线程被放到我们的阻塞队列中");
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    System.out.println("一个线程被唤醒");
//                }
//            });
            pool.execute(() -> {
                System.out.println("一个线程被放到我们的阻塞队列中");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("一个线程被唤醒");
                }
            });
        }
        pool.shutdown();
    }

}
