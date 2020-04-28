package com.lee.juc.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.IntStream;

/**
 * LongAccumulator工具类示例，是Adder的升级版（不只是add，可以自定义计算规则），适用于需要大量并行计算且无计算顺序要求的场景
 * <p>
 * 相对于手动累加优点：
 * 1.若计算规则改变只需要改变传入的accumulatorFunction，与数据本身解耦，可以很方便的实现各种计算操作
 * 2.可以提交到线程池中并行计算提高效率（但不保证计算顺序）
 * <p>
 * Created by lsd
 * 2020-04-28 08:13
 */
public class LongAccumulatorDemo {

    public static void main(String[] args) {
        LongAccumulator longAccumulator = new LongAccumulator((x, y) -> x + y, 0);  //identity (initial value) for the accumulator function
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        // 1~9累加
        IntStream.range(1, 10).forEach(
                i -> threadPool.execute(() -> longAccumulator.accumulate(i))
        );
        // 等待所有任务执行完成
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
        System.out.println("结果：" + longAccumulator.getThenReset());
    }

}
