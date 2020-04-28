package com.lee.juc.atomic;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Atomic变量 = volatile + CAS算法（compare and swap）
 */
public class AtomicIntegerDemo {

    public static void main(String[] args) {
        AddTask demo = new AddTask();
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(demo);
            threadList.add(t);
            t.start();
        }
        // 主线程等待10个子线程结束
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("错误结果：" + demo.getNum());
        System.out.println("真实值：" + demo.getRealNum());
        System.out.println("冲突次数：" + demo.getConflictNum());
    }
}

@Slf4j
@Data
class AddTask implements Runnable {

    // 多个线程共享的变量，volatile不能解决i++的原子性
    private volatile int num = 0;
    // 使用原子变量解决
    private AtomicInteger realNum = new AtomicInteger();        //真实值

    private AtomicInteger conflictNum = new AtomicInteger();    //冲突次数
    private boolean[] isAdded = new boolean[10000000];          //数字已累加标记，用于记录发生冲突位置


    @Override
    public void run() {
        for (int i = 0; i < 100000; i++) {
            int after = num++;              //num变量会被其他线程修改，此处必须用线程隔离的栈变量after暂存累加后的值再去判断是否冲突
            synchronized (this) {           //若发生冲突则累加冲突次数（需要保证累加冲突次数与置为的原子性）
                if (isAdded[after]) {
                    conflictNum.incrementAndGet();
                }
                isAdded[after] = true;  //置为已累加
            }
            // 真实值累加
            realNum.getAndIncrement();
        }
    }

}
