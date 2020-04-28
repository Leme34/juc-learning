package com.lee.juc.atomic;

/**
 * 模拟CAS操作，等价代码
 */
public class TwoThreadsCompetition {

    private static volatile int value;

    public static synchronized int compareAndSwap(int expectedValue, int newValue) {
        int oldValue = value;
        if (oldValue == expectedValue) {
            value = newValue;
        }
        return oldValue;
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> compareAndSwap(0, 1);
        // 通过debug可以看到只有一个线程会修改成功
        Thread t1 = new Thread(task, "Thread 1");
        Thread t2 = new Thread(task, "Thread 2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(value);
    }

}
