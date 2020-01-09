package com.lee.juc.countdownlatch_cyclicbarrier_semaphore;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 循环屏障，在所有的线程释放彼此之后可被循环使用的 线程屏障
 * <p>
 * 当线程到达需要阻塞等待的(屏障)位置时调用 await() ，这个方法将阻塞直到所有线程都到达屏障位置时才放行
 * 当所有线程都到达屏障位置，那么屏障将打开，此时所有的线程都被释放，而屏障将被重置以便下次使用。
 * <p>
 * 与 CountDownLatch 的区别：
 * 1.CountDownLatch只能阻塞一个线程等待其他事件，而CyclicBarrier可以阻塞多个线程相互等待
 * 2. CountDownLatch减计数，CyclicBarrier加计数
 * 3. CountDownLatch是一次性的，CyclicBarrier可以重用
 * <p>
 * Created by lsd
 * 2019-10-17 23:35
 */
@Slf4j
public class CyclicBarrierDemo {

    /**
     * 构造方法参数：屏障阻塞等待的总线程数，屏障打开后执行的任务
     * 此处模拟开5个线程并行读取一个大文件，只有当所有线程都读取完成时才能合并文件
     */
    private static CyclicBarrier barrier = new CyclicBarrier(5, () ->
            log.info("所有读取线程已到达屏障，开始合并文件...")
    );

    /**
     * 读取此线程负责的那部分
     */
    @SneakyThrows
    public static void readFilePart() {
        // 随机睡眠1~3s，模拟读取时间
        Random r = new Random();
        final int time = r.nextInt(2) + 1;
        TimeUnit.SECONDS.sleep(time);
        log.info("我那部分读完啦~耗时：" + time + "s");
    }

    /**
     * 把该线程读取的那部分写到文件中
     */
    @SneakyThrows
    public static void writeFilePart() {
        Random r = new Random();
        final int time = r.nextInt(2) + 1;
        TimeUnit.SECONDS.sleep(time);
        log.info("我那部分合并完啦~耗时：" + time + "s");
    }

    /**
     * 模拟开5个线程并行读取一个大文件，只有当所有线程都读取完成时才能合并文件
     */
    public static void mergeFile() {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                readFilePart();
                // 阻塞等待其他线程读取完
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 所有线程一起开始合并文件
                writeFilePart();
            }).start();
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        mergeFile();
        // 屏障重用
        TimeUnit.SECONDS.sleep(10);
        log.error("=======================测试屏障重用=========================");
        mergeFile();
    }

}
