package com.lee.juc.threadlocal;

/**
 * ThreadLocal中基本数据类型的装拆箱可能导致NPE
 */
public class ThreadLocalNPE {

    ThreadLocal<Long> longThreadLocal = new ThreadLocal<>();

    public void set() {
        longThreadLocal.set(Thread.currentThread().getId());
    }

    /**
     * 此处的返回值为基本数据类型long，而longThreadLocal中声明的类型为Long，则会进行装拆箱操作，若get()返回的是null，则会导致NPE
     */
    public long get() {
        return longThreadLocal.get(); //还没set就get取出的是null
    }

    public static void main(String[] args) {
        ThreadLocalNPE threadLocalNPE = new ThreadLocalNPE();
        System.out.println(threadLocalNPE.get());
        Thread thread1 = new Thread(() -> {
            threadLocalNPE.set();
            System.out.println(threadLocalNPE.get());
        });
        thread1.start();
    }
}
