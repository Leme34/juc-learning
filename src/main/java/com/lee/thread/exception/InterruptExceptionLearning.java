package com.lee.thread.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 响应中断的两种最佳实践+示例
 * <p>
 * Created by lsd
 * 2020-04-23 17:05
 */
@Slf4j
public class InterruptExceptionLearning {

    /**
     * 阻塞任务（例如sleep）的场景
     * 阻塞任务会自动响应中断，并要求我们catch进行中断异常处理
     */
    public static void condition1() {
        Thread taskThread = new Thread(() -> {
            try {
                log.debug("任务执行中...");
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                log.error("任务被中断", e);
            }
        });
        taskThread.start();
        // 睡眠0.5s后调用中断
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ignored) {
        }
        taskThread.interrupt();
    }


    /**
     * 非阻塞任务（任务一直running）的场景
     * 需要手动检测并响应中断标志，否则线程将不会自动响应中断（即不会抛出中断异常）而停止
     */
    public static void condition2() {
        Thread taskThread = new Thread(() -> {
            int num = 0;
            // 这个任务的运行时间肯定是超过0.5s的，此处手动检测并响应中断
            while (!Thread.currentThread().isInterrupted() && num < Integer.MAX_VALUE / 2) {
                System.out.println(num++);
            }
            System.out.println("循环结束，任务完成");
        });
        taskThread.start();
        // 睡眠0.5s后调用中断
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ignored) {
        }
        taskThread.interrupt();
    }

    /**
     * 任务中循环多次阻塞（例如sleep）的场景-在循环外层捕获异常情况
     * 同condition2，仍然是由每次执行的阻塞任务去响应中断，而不需要手动响应中断
     */
    public static void condition3() {
        Thread taskThread = new Thread(() -> {
            log.debug("任务执行中...");
            try {
                // 100次循环，每次睡0.1s
                for (int i = 1; i <= 100; i++) {
                    System.out.println(i);
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch (InterruptedException e) {
                log.error("任务被中断", e);
            }
        });
        taskThread.start();
        // 睡眠5s后调用中断
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignored) {
        }
        taskThread.interrupt();
    }


    /**
     * 任务中循环多次阻塞（例如sleep）的场景-在循环内层捕获异常情况
     * 同condition3的区别：只有被中断时的那1次循环会抛出中断异常,循环仍然会继续执行
     * 因此需要手动检查并响应中断，此处的方案是：传递中断【最佳实践】
     */
    public static void condition4() {
        Thread taskThread = new Thread(() -> {
            log.debug("任务执行中...");
            // 100次循环，每次睡0.1s
            for (int i = 1; i <= 100; i++) {
                try {
                    doSthAndThrowsExcept(i);
                } catch (InterruptedException e) {  //响应中断（处理中断异常）
                    log.error("任务被中断", e);
                    return;
                }
            }
        });
        taskThread.start();
        // 睡眠5s后调用中断
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignored) {
        }
        taskThread.interrupt();
    }

    /**
     * 响应中断最佳实践：【传递中断】
     * 因为run方法中不能抛出异常，所以封装此方法进行异常抛出（传递中断）
     */
    private static void doSthAndThrowsExcept(int i) throws InterruptedException {
        System.out.println(i);
        TimeUnit.MILLISECONDS.sleep(100);
    }


    /**
     * 任务中循环多次阻塞（例如sleep）的场景-在循环内层捕获异常情况
     * 同condition3的区别：只有被中断时的那1次循环会抛出中断异常,循环仍然会继续执行
     * 因此需要手动检查并响应中断，此处的方案是：恢复中断【适用于线程的run方法等无法中断传递(throws InterruptedException)的场景】
     */
    public static void condition5() {
        Thread taskThread = new Thread(() -> {
            log.debug("任务执行中...");
            // 100次循环，每次睡0.1s
            for (int i = 1; i <= 100; i++) {
                // 手动检查并响应中断，如果没有这句线程被中断也不会跳出循环
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                try {
                    System.out.println(i);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    //中断异常被捕获后，中断标记会自动清除（置为false）导致中断异常被吞了（没有响应中断），因此需要手动恢复中断
                    Thread.currentThread().interrupt();
                    log.error("任务被中断", e);
                }
            }
        });
        taskThread.start();
        // 睡眠1s后调用中断
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignored) {
        }
        taskThread.interrupt();
    }


    public static void main(String[] args) {
//        condition1();
//        condition2();
//        condition3();
//        condition4();  //响应中断方式1：传递中断【最佳实践】
        condition5();   //响应中断方式2：恢复中断【适用于线程的run方法等无法中断传递(throws InterruptedException)的场景】

    }

}
