package com.lee.juc.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 用 AtomicStampedReference 或 AtomicMarkableReference 解决ABA问题
 * <p>
 * AtomicStampedReference 可以给原子引用加上版本号，追踪原子引用整个的变化过程，如：A -> B -> A -> C，
 * 通过【AtomicStampedReference】，我们可以知道，引用变量中途【被更改了几次】。
 * 但是有时候，并不关心引用变量更改了几次，只是单纯的关心【是否更改过】，所以就有了 【AtomicMarkableReference】
 * <p>
 * 下面的代码分别用AtomicInteger和AtomicStampedReference、AtomicMarkableReference来对初始值为100的原子整型变量进行更新，
 * AtomicInteger会成功执行CAS操作，而加上版本戳的AtomicStampedReference 或 AtomicMarkableReference 来对初始值为100的原子整型变量进行更新对于ABA问题会执行CAS失败
 */
public class ABA {
    private static AtomicInteger atomicInt = new AtomicInteger(100);

    private static AtomicStampedReference<Integer> atomicStampedRef =
            new AtomicStampedReference<>(100, 0);

    private static AtomicMarkableReference<Integer> atomicMarkableRef =
            new AtomicMarkableReference<>(100, true); //初始标记为true


    public static void main(String[] args) throws InterruptedException {
        /** ==========================AtomicInteger演示ABA问题================================== */
        Thread intT1 = new Thread(() -> {
            atomicInt.compareAndSet(100, 101);
            atomicInt.compareAndSet(101, 100);
        });

        Thread intT2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            boolean c3 = atomicInt.compareAndSet(100, 101);
            System.out.println(c3); // true
        });

        intT1.start();
        intT2.start();
        intT1.join();
        intT2.join();

        /** ==========================AtomicStampedReference解决ABA问题==================================== */
        Thread stampedRefT1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            atomicStampedRef.compareAndSet(100, 101,
                    atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
            atomicStampedRef.compareAndSet(101, 100,
                    atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
        });

        Thread stampedRefT2 = new Thread(() -> {
            int stamp = atomicStampedRef.getStamp();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignored) {
            }
            boolean c3 = atomicStampedRef.compareAndSet(100, 101,
                    stamp, stamp + 1);
            System.out.println(c3); // false
        });

        stampedRefT1.start();
        stampedRefT2.start();


        /** ==========================AtomicMarkableReference解决ABA问题==================================== */
        Thread markableRefT1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            // 标记为非初始mark（已被修改过）
            atomicMarkableRef.compareAndSet(100, 101, true, false);
            atomicMarkableRef.compareAndSet(101, 100, false, false);
        });

        Thread markableRefT2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignored) {
            }
            // 要求mark为初始值（未被修改过）才更新
            boolean c3 = atomicMarkableRef.compareAndSet(100, 101,
                    true, false);
            System.out.println(c3); // false
        });

        markableRefT1.start();
        markableRefT2.start();
    }
}
