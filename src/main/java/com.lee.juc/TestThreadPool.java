package com.lee.juc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * execute(Runnable x) 没有返回值。可以执行任务，但无法判断任务是否成功完成。——实现Runnable接口
 * submit(Runnable x) 返回一个future。可以用这个future来判断任务是否成功完成。——实现Callable接口
 */
public class TestThreadPool {

    public static void main(String[] args) {
        //1.创建线程池,线程池中包含5个线程
        ExecutorService pool = Executors.newFixedThreadPool(2);
        ThreadDemo2 tpd = new ThreadDemo2();
        //2.向线程池中提交6个任务，分3批执行
        for (int i = 0; i < 6; i++) {
            pool.execute(tpd);
        }
        //3.平和关闭，会等待线程池中的线程完成任务后才关闭线程池
        pool.shutdown();
    }

}

class ThreadDemo2 implements Runnable{
    @Override
    public void run() {
        System.out.println("一个线程被放到我们的阻塞队列中");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("一个线程被唤醒");
        }
    }
}
