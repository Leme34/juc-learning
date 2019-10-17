package com.lee.juc.volatile_atomic;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 根据Java内存模型的实现，线程在具体执行时，会先拷贝主存数据到线程本地（CPU缓存），操作完成后再把结果从线程本地刷到主存。
 * 内存可见控制的是线程执行结果在内存中对其它线程的可见性。
 *
 * volatile变量的读写都会直接刷到主存，即保证了变量的修改可见性，但不能保证原子性，不阻塞线程
 * synchronized、lock则可以保证变量的修改可见性和原子性，阻塞线程
 *
 * Created by lsd
 * 2019-09-08 12:24
 */
public class VolatileDemo {
    public static void main(String[] args) throws InterruptedException {
        MyRunnable runnable = new MyRunnable();
        // 子线程把 flag 置为 true
        new Thread(runnable).start();
        // 主线程循环读取 flag ，为 true 则结束
        while (true) {
            //方法1：若注释这句程序无法结束，因为死循环速度过高，就算内存中的值已被改变，主线程也来不及去读取（仍是进入死循环时读到的false）
//            Thread.sleep(100);
            //方法2：阻止其它线程获取当前对象的监控锁（runnable对象），
            //synchronized还会创建一个内存屏障，使得先获得这个锁的线程的所有操作，都happens-before于随后获得这个锁的线程的操作。
//            synchronized (runnable) {
                if (runnable.isFlag()) {
                    System.out.println("============");
                    break;
                }
            // TODO 加上这句后不会造成死循环，因为输出语句需要时间（与sleep同理），就有机会读取到最新的 isStop，而不再是主线程缓存中的 false
            //System.out.println("running");
//            }
        }

    }
}

@Getter
@Setter
class MyRunnable implements Runnable {
    // 模拟：主线程和子线程的共享数据
    private boolean flag = false;
    // 方法3：Volatile
//    private volatile boolean flag = false;

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.flag = true;
        System.out.println("MyThread：flag已置为true");
    }
}
