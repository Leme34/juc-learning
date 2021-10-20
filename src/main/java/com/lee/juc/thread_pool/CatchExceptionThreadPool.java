package com.lee.juc.thread_pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 当我们使用 ThreadPoolExecutor.submit 向线程池 ThreadPoolExecutor(java.util.concurrent.ExecutorService) 提交任务时，
 * 如果不调用 Future.get() ，那么此异常将被线程池吃掉。
 *
 * 此处演示以下情况：
 * 1、使用 ThreadPoolExecutor.execute 提交任务，异常可抛出
 * 2、使用 ThreadPoolExecutor。submit 提交任务，若不获取 submit 方法的返回值 future，有异常也不会抛出
 * 3、使用 ThreadPoolExecutor。submit 提交任务，在创建 ThreadPoolExecutor 时，通过覆盖 ThreadPoolExecutor.afterExecute 方法，进行异常处理
 *
 * @author synda
 * @date 2021/10/20
 */
@Slf4j
public class CatchExceptionThreadPool {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), (r, ex) -> {
        log.error(r + " was rejected by " + ex);
    });

    private static final ThreadPoolExecutor catchExceptionExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>()) {
        // 通过覆盖 ThreadPoolExecutor.afterExecute 方法，我们才能捕获到任务抛出的异常(RuntimeException)
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            //此方法的默认实现为空，这样我们就可以通过继承或者覆盖ThreadPoolExecutor 来达到自定义的错误处理。
            super.afterExecute(r, t);
            printException(r, t);
        }
    };

    private static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future) {
            try {
                Future future = (Future) r;
                if (future.isDone())
                    future.get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
            log.error(t.getMessage(), t);
        }
    }

    public static void main(String[] args) {
        // 1、使用 ThreadPoolExecutor.execute 提交任务，异常可抛出
        //executor.execute(() -> {
        //    int a = 1 / 0;
        //});

        // 2、使用 ThreadPoolExecutor。submit 提交任务，若不获取 submit 方法的返回值 future，有异常也不会抛出
        //executor.submit(() -> {
        //    int a = 1 / 0;
        //});

        // 3、使用 ThreadPoolExecutor。submit 提交任务，在创建 ThreadPoolExecutor 时，通过覆盖 ThreadPoolExecutor.afterExecute 方法，进行异常处理
        catchExceptionExecutor.submit(() -> {
            int a = 1 / 0;
        });

        executor.shutdown();
        catchExceptionExecutor.shutdown();
    }

}
