package com.lee.juc.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 此案例实现调用start方法来提前加载好数据，然后 FutureTask.get() 的时候数据已加载完成
 * 1.通过提前启动计算来减少等待 FutureTask.get() 的阻塞等待时间
 * 2.提供了处理 Callable 异常封装的一种解决思路
 *
 *
 * FutureTask 实现了 Future、Runnable 接口，既可以作为Runnable被线程执行，又可以作为Future得到Callable的返回值。
 * 因此，FutureTask可以交给 Executor 执行，也可以由调用的线程直接执行（ FutureTask.run() ）
 *
 * 另外，FutureTask的获取可以直接 new 出来
 * 也可以通过 ExecutorService.submit()方法返回一个FutureTask对象
 *
 * FutureTask实现是基于AbstractQueuedSynchronizer同步框架（下面简称AQS）
 * 基于AQS实现的同步器都会包含如下两种类型的操作：
 *  * 1、至少一个acquire操作
 *  * 2、至少一个release操作
 *
 * 调用 FutureTask.get() 时，
 * 1.如果状态为已经执行完成那么就会返回 Callable 的返回值
 * 2.如果状态为没有执行完成，那么会阻塞当前线程并放入等待队列中
 * 当其它线程执行 release 操作以后（如FutureTask.run()或者FutureTask.cancel），就会去唤醒等待队列中的线程
 *
 *
 * 使用场景；
 * 1.有多个线程执行若干任务，每个任务最多只能被执行一次。
 * 2.当多个线程试图同时执行同一个任务时，只允许一个线程执行任务，
 * 其他线程需要等待这个任务执行完后才能继续执行
 *
 * Created by lsd
 * 2019-10-16 11:07
 */
@Slf4j
public class TestFutureTask {

    /**
     * 由于 Callable 的异常包括： 受检查异常（InterruptedException）、不受检查异常（RuntimeException）、错误（Error）
     * 但除了 InterruptedException，其他不受检查异常全都被封装为一个 受检查异常（ExecutionException） 由 Future.get()抛出
     * <p>
     * 调用者应首先把已知异常（InterruptedException）直接抛出，然后调用此方法对每种未知异常进行单独处理
     *
     * @param t e.getCause()
     */
    public static RuntimeException launderThrowable(Throwable t) {
        // 处理 Callable 抛出的运行时异常
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {  // 处理Error
            throw (Error) t;
        } else {
            // Callable抛出的 非不受检查异常和Error 的其他异常
            throw new RuntimeException(t);
        }
    }

    private final FutureTask<String> futureTask = new FutureTask<>(() -> {
        TimeUnit.SECONDS.sleep(1);
        return "任务完成";
    });

    private final Thread t = new Thread(futureTask);

    public void startTask() {
        t.start();
    }

    public void cancelTask() {
        futureTask.cancel(true);
    }

    public String getResult() throws InterruptedException {
        try {
            return futureTask.get();
        } catch (CancellationException e) {
            throw new CancellationException("取消异常，任务被取消啦~");
        } catch (ExecutionException e) {       // 运行时异常、Error、其他异常都封装为 ExecutionException
            throw TestFutureTask.launderThrowable(e.getCause()); // getCause获取被封装的初始异常
        }
    }

    public static void main(String[] args) {
        final TestFutureTask futureTask = new TestFutureTask();
        futureTask.startTask();
        try {
            System.out.println(futureTask.getResult());
        } catch (InterruptedException e) {
            log.error("中断异常，任务被中断啦~", e);
            // 设置线程的中断标志，这样更高级别的中断处理程序就会注意到它，并可以适当地处理它。
            Thread.currentThread().interrupt();
        }finally {
            // 由于不需要结果，因此取消任务
            futureTask.cancelTask();
        }
    }

}
