package com.lee.juc.atomic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * AtomicIntegerFieldUpdater 可以把对象【可访问】【非static】的【volatile】属性升级为原子属性
 * 适用场景：类对象中存在少部分属性需要原子的 get-set 组合操作，又不想对整个类使用原子引用
 * <p>
 * Created by lsd
 * 2020-04-28 00:25
 */
@Slf4j
public class AtomicIntegerFieldUpdaterDemo {
    // 对照组
    private static Candidate tom = new Candidate();
    private static Candidate peter = new Candidate();
    // 原子升级类，底层需要反射所以必须保证此属性可访问(can be access)
    private static final AtomicIntegerFieldUpdater<Candidate> scoreUpdater =
            AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    private static CountDownLatch countDownLatch = new CountDownLatch(10);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                tom.score++; //普通加操作
                scoreUpdater.getAndIncrement(peter);  //CAS加操作，会直接改变peter.score的值
            }
            countDownLatch.countDown();
        };
        // 开启10个线程进行加操作
        for (int i = 0; i < 10; i++) {
            new Thread(task).start();
        }
        countDownLatch.await();
        System.out.println("普通加操作结果：" + tom.score);
        System.out.println("升级为原子变量后加操作结果：" + peter.score);
    }


}

class Candidate {
    volatile int score;   //必须是volatile变量才能升级为原子变量
}
