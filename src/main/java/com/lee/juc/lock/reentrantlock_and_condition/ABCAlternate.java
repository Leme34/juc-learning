package com.lee.juc.lock.reentrantlock_and_condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程按序交替执行
 * 开启3个线程打印：ABCABCABC...
 */
public class ABCAlternate {

    public static void main(String[] args) {

        ABCClass abc = new ABCClass();

        // 开启3个线程,循环打印10次ABC
        new Thread(() -> {
            for (int i =0;i<10;i++){
                abc.printA();
            }
        }, "A").start();
        new Thread(() -> {
            for (int i =0;i<10;i++){
                abc.printB();
            }
        }, "B").start();
        new Thread(() -> {
            for (int i =0;i<10;i++){
                abc.printC();
            }
        }, "C").start();
    }

}

// 控制ABC打印
class ABCClass {
    // 显式锁
    private Lock lock = new ReentrantLock();
    // 3个(对)线程通信变量，分别控制ABC的pv操作
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    // 当前输出的是ＡＢＣ的标记
    private int flag = 1;

    public void printA() {
        try {
            // 上锁
            lock.lock();
            // 判断标记
            if (flag != 1) {
                condition1.await();
            }
            // 打印 A
            System.out.println("A");
            // 修改标记，通知打印 B
            flag = 2;
            condition2.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        try {
            // 上锁
            lock.lock();
            // 判断标记
            if (flag != 2) {
                condition2.await();
            }
            // 打印 B
            System.out.println("B");
            // 修改标记，通知打印 C
            flag = 3;
            condition3.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        try {
            // 上锁
            lock.lock();
            // 判断标记
            if (flag != 3) {
                condition3.await();
            }
            // 打印 A
            System.out.println("C");
            // 修改标记，通知打印 A
            flag = 1;
            condition1.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}

