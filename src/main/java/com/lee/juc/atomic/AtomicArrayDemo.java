package com.lee.juc.atomic;

import lombok.AllArgsConstructor;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 原子数组用法
 * <p>
 * Created by lsd
 * 2020-04-27 23:34
 */
public class AtomicArrayDemo {

    public static void main(String[] args) {
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(1000);
        Incrementer incrementer = new Incrementer(atomicIntegerArray);
        Decrementer decrementer = new Decrementer(atomicIntegerArray);
        // +-操作各100个线程，使得原子数组中每个值都进行+1和-1操作
        Thread[] incrementerThreads = new Thread[100];
        Thread[] decrementerThreads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            decrementerThreads[i] = new Thread(decrementer);
            incrementerThreads[i] = new Thread(incrementer);
            decrementerThreads[i].start();
            incrementerThreads[i].start();
        }
        // 等待所有线程结束
        for (int i = 0; i < 100; i++) {
            try {
                decrementerThreads[i].join();
                incrementerThreads[i].join();
            } catch (InterruptedException ignored) {
            }
        }
        // 检查是否原子数组中每个值是否都为0，若有不为0的则是出现线程安全问题
        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            if (atomicIntegerArray.get(i) != 0) {
                System.out.println("发现了错误，index=" + i);
            }
        }
        System.out.println("程序运行结束");
    }

}


/**
 * 给数组的每个元素-1
 */
@AllArgsConstructor
class Decrementer implements Runnable {

    private AtomicIntegerArray array;

    @Override
    public void run() {
        for (int i = 0; i < array.length(); i++) {
            array.getAndDecrement(i);
        }
    }
}

/**
 * 给数组的每个元素+1
 */
@AllArgsConstructor
class Incrementer implements Runnable {

    private AtomicIntegerArray array;

    @Override
    public void run() {
        for (int i = 0; i < array.length(); i++) {
            array.getAndIncrement(i);
        }
    }
}
