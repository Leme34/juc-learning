package com.lee.juc.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 演示高并发场景下LongAdder的性能比AtomicLongDemo高
 *
 * @see AtomicLongDemo
 * <p>
 * 原因：
 * 1.AtomicLong每次加操作都需要flush和refresh内存去保证不同线程栈数据的一致性(也是最终结果正确性保障)，竞争激烈情况下性能就会下降
 * 2.LongAdder引入分段锁思想(空间换时间)，内部有一个base变量和一个Cell[]数组。
 * 若竞争不激烈则直接把结果CAS到base变量，否则各个线程在自己的Hash分配到的槽Cell[i]中进行累加。
 * 如果还是出现竞争，会换一个Cell[i]再次尝试，最终把Cell[]里面的value和base相加，得到最终的结果。
 * 从而把不同线程对应到不同的Cell上进行修改，使用了（JDK7的HashMap）分段锁的思想减少冲突。
 * @see LongAdder java.util.concurrent.atomic.LongAdder#sum()
 * 注意：sum()求和过程是无锁的，在for循环过程中已被遍历过的值如果被修改，则返回的最终结果可能不能十分精确
 *
 *
 * <p>
 * Created by lsd
 * 2020-04-28 07:39
 */
public class LongAdderDemo {

    private static LongAdder num = new LongAdder();
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        // 1w个任务，并发累加10000次num
        for (int i = 0; i < 10000; i++) {
            threadPool.submit(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException ignored) {
                }
                for (int j = 0; j < 10000; j++) {
                    num.increment();
                }
            });
        }
        long start = System.currentTimeMillis();
        countDownLatch.countDown();
        // 等待所有任务执行完成
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
        System.out.println("结果：" + num.sum() + "，耗时：" + (System.currentTimeMillis() - start));

    }

}
