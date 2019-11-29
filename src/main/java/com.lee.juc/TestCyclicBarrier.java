package com.lee.juc;

/**
 * 循环 + 屏障 = CyclicBarrier，在所有的线程释放彼此之后可被 循环 使用的 屏障
 *
 * 当线程到达屏障位置时将调用 await() 方法，这个方法将阻塞直到所有线程都到达屏障位置。
 * 如果所有线程都到达屏障位置，那么屏障将打开，此时所有的线程都将被释放，而屏障将被重置以便下次使用。
 *
 * 与 CountDownLatch 的区别：闭锁用于等待事件，而屏障用于等待其他线程。
 *
 * Created by lsd
 * 2019-10-17 23:35
 */
public class TestCyclicBarrier {



}
