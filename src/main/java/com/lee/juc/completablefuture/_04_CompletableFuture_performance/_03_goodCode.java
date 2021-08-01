package com.lee.juc.completablefuture._04_CompletableFuture_performance;

import org.ph.share.SmallTool;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * {@link _02_terribleCodeImprove}使用 Stream Api 重写
 * https://www.bilibili.com/video/BV1e44y1677h/
 */
public class _03_goodCode {
    public static void main(String[] args) {
        /**
         * 修改默认最大线程数
         * 一般不要随便修改common pool的线程数，因为这是一个全局线程池
         * 所以一般都是创建自己定制的线程池传入CompletableFuture中使用，做到解耦
         * {@link _05_customThreadPool}
         */
        // -Djava.util.concurrent.ForkJoinPool.common.parallelism=8
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");

        SmallTool.printTimeAndThread("小白和小伙伴们 进餐厅点菜");
        long startTime = System.currentTimeMillis();

        CompletableFuture[] dishes = IntStream.rangeClosed(1, 12)
                .mapToObj(i -> new Dish("菜" + i, 1))
                .map(dish -> CompletableFuture.runAsync(dish::make))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(dishes).join();

        SmallTool.printTimeAndThread("菜都做好了，上桌 " + (System.currentTimeMillis() - startTime));

    }
}
