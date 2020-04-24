package com.lee.juc.thread_pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 五种线程池，四种拒绝策略，四种阻塞队列
 * Created by lsd
 * 2019-10-10 17:15
 */
public class TestThreadPoolExecutor {


    public static void main(String[] args) throws Exception {
        testThreadPoolExecutor();
    }

    public static void testThreadPoolExecutor() throws Exception {
        //基础参数
        int corePoolSize = 2;//最小活跃线程数
        int maximumPoolSize = 5;//最大活跃线程数
        int keepAliveTime = 5;//指定线程池中线程空闲超过 5s 后将被回收
        TimeUnit unit = TimeUnit.SECONDS;//keepAliveTime 单位
        //阻塞队列
        BlockingQueue<Runnable> workQueue = null;
        workQueue = new ArrayBlockingQueue<>(5);//基于数组的先进先出队列，有界
        workQueue = new LinkedBlockingQueue<>();//基于链表的先进先出队列，有界，默认Integer.MAX_VALUE
        workQueue = new PriorityBlockingQueue<>();  //按优先级排序的队列，默认大小11
        workQueue = new SynchronousQueue<>();//无存储任务的功能(可以理解为队里永远是满的，因此最终都会创建非核心线程来执行任务)，但synchronousQueue.size()永远返回0。每次只能放1个任务且直接交付任务，因此put和take会一直阻塞直到任务交付完成，所以称为同步队列，仅当有足够多的消费者并总有一个消费者准备好消费才适合使用
        //拒绝策略
        RejectedExecutionHandler rejected = null;
        rejected = new ThreadPoolExecutor.AbortPolicy();//默认，队列满了丢任务抛出异常
        rejected = new ThreadPoolExecutor.DiscardPolicy();//队列满了丢任务不异常
        rejected = new ThreadPoolExecutor.DiscardOldestPolicy();//将最早进入队列的任务删，之后再尝试加入队列
        rejected = new ThreadPoolExecutor.CallerRunsPolicy();//如果添加到线程池失败，那么主线程会自己去执行该任务
        //使用的线程池
        ExecutorService threadPool = null;
        threadPool = Executors.newCachedThreadPool();//有缓冲的线程池，线程数 JVM 控制
        threadPool = Executors.newFixedThreadPool(3);//固定大小的线程池
        threadPool = Executors.newScheduledThreadPool(2);
        threadPool = Executors.newSingleThreadExecutor();//单线程的线程池，只有一个线程在工作
        threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                rejected);//自定义线程池，可控制参数比较多
        //执行无返回值线程
        TaskRunnable taskRunnable = new TaskRunnable();
        threadPool.execute(taskRunnable);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //执行有返回值线程
            TaskCallable taskCallable = new TaskCallable(i);
            Future<String> future = threadPool.submit(taskCallable);
            futures.add(future);
        }
        for (int i = 0; i < futures.size(); i++) {
            String result = futures.get(i).get();
            System.out.println(i + " result = " + result);
        }
    }

    /**
     * 返回值的线程，使用 threadpool.execut() 执行
     */
    public static class TaskRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " runnable result!");
        }
    }

    /**
     * 有返回值的线程，使用 threadpool.submit() 执行
     */
    public static class TaskCallable implements Callable<String> {
        public TaskCallable(int index) {
            this.i = index;
        }

        private int i;

        @Override
        public String call() throws Exception {
            int r = new Random().nextInt(5);
            try {
                Thread.sleep(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("callable result!");
            return Thread.currentThread().getName() + " callable index=" + i + ",sleep=" + r;
        }
    }
}
