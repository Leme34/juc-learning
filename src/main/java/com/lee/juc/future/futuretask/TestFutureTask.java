package com.lee.juc.future.futuretask;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @formatter:off
 *
 * 此案例实现调用start方法来提前加载好数据，然后 FutureTask.get() 的时候数据已加载完成
 * 1.通过提前启动计算来减少等待 FutureTask.get() 的阻塞等待时间
 * 2.提供了处理 Callable 异常封装的一种解决思路
 *
 *
 * FutureTask 实现了 Future、Runnable 接口，既可以作为Runnable被线程执行，又可以作为Future得到Callable的返回值。
 * 因此，FutureTask可以交给 Executor 执行，也可以由调用的线程直接执行（ FutureTask.run() ）
 *
 * 另外，FutureTask的获取可以直接 new 出来，也可以通过 ExecutorService.submit()方法返回一个FutureTask对象
 *
 * FutureTask实现是基于AbstractQueuedSynchronizer同步框架（下面简称AQS）
 * 基于AQS实现的同步器都会包含如下两种类型的操作：
 *  1、至少一个acquire操作
 *  2、至少一个release操作
 *
 *
 * 调用 FutureTask.get() 时，
 *  1.如果状态为已经执行完成那么就会返回 Callable 的返回值
 *  2.如果状态为没有执行完成，那么会阻塞当前线程并放入等待队列中
 * 当其它线程执行 release 操作以后（如FutureTask.run()或者FutureTask.cancel），就会去唤醒等待队列中的线程
 *
 *
 * 使用场景；
 *  1.有多个线程执行若干任务，每个任务最多只能被执行一次。
 *  2.当多个线程试图同时执行同一个任务时，只允许一个线程执行任务，其他线程需要等待这个任务执行完后才能继续执行
 *
 * @formatter:on
 * Created by lsd
 * 2019-10-16 11:07
 */
@Slf4j
public class TestFutureTask {

    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            System.out.println("子线程正在计算...");
            Thread.sleep(3000);
            int sum = 0;
            for (int i = 0; i < 100; i++) {
                sum += i;
            }
            return sum;
        });
//        new Thread(futureTask).start();   //也直接放入线程中执行
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(futureTask);
        try {
            System.out.println("task运行结果：" + futureTask.get());
        } catch (InterruptedException e) {    //响应中断
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
