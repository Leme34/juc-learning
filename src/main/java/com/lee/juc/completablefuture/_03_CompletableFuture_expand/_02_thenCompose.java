package com.lee.juc.completablefuture._03_CompletableFuture_expand;

import org.ph.share.SmallTool;

import java.util.concurrent.CompletableFuture;

/**
 * https://www.bilibili.com/video/BV1wZ4y1A7PK/
 */
public class _02_thenCompose {
    public static void main(String[] args) {
//        thenCompose();
        thenComposeAsync();
    }

    /**
     * 最终有2个异步(在2个线程中独立)运行的任务，但由于线程复用的存在不能根据线程id看出现象
     */
    private static void thenCompose() {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> { // 异步任务①
            SmallTool.printTimeAndThread("厨师炒菜");
            SmallTool.sleepMillis(200);
            return "番茄炒蛋";
        }).thenCompose(dish -> { // 这里的代码会在上游任务的同一个线程执行
            SmallTool.printTimeAndThread("服务员A 准备打饭，但是被领导叫走，打饭交接给服务员B");
            SmallTool.sleepMillis(200);

            // 异步任务②：此处返回的CompletableFuture里边的代码将会在新的线程中执行（supplyAsync会开启异步任务）
            return CompletableFuture.supplyAsync(() -> {
                SmallTool.printTimeAndThread("服务员B 打饭");
                SmallTool.sleepMillis(100);
                return dish + " + 米饭";
            });
        });

        SmallTool.printTimeAndThread(cf1.join() + "好了，开饭");
    }

    /**
     * 最终有3个异步(在3个线程中独立)运行的任务，但由于线程复用的存在不能根据线程id看出现象
     */
    private static void thenComposeAsync() {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {  // 异步任务①
            SmallTool.printTimeAndThread("厨师炒菜");
            SmallTool.sleepMillis(200);
            return "番茄炒蛋";
        }).thenComposeAsync(dish -> {  // 异步任务②：这里的代码会在提供的Executor（如果传入）上调用，否则将在默认的ForkJoinPool上调用
            SmallTool.printTimeAndThread("服务员A 准备打饭，但是被领导叫走，打饭交接给服务员B");
            SmallTool.sleepMillis(200);

            // 此处返回的CompletableFuture里边的代码将会在新的线程中执行（supplyAsync会开启异步任务）
            return CompletableFuture.supplyAsync(() -> { // 异步任务③
                SmallTool.printTimeAndThread("服务员B 打饭");
                SmallTool.sleepMillis(100);
                return dish + " + 米饭";
            });
        });

        SmallTool.printTimeAndThread(cf1.join() + "好了，开饭");
    }
}
