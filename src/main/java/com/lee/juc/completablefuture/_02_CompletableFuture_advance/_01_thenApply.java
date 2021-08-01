package com.lee.juc.completablefuture._02_CompletableFuture_advance;

import org.ph.share.SmallTool;

import java.util.concurrent.CompletableFuture;

/**
 * https://www.bilibili.com/video/BV1ui4y1T7xf/
 *
 * 1. thenCompose 与 thenApply 的区别：作用都是连接两个异步任务，只是传入的Function参数泛型不同
 * (1)前者是 Function<? super T, ? extends CompletionStage<U>>
 * (2)后者是 Function<? super T,? extends U>
 * 所以 thenCompose 如果我们传入 {@code CompletableFuture.supplyAsync(() -> {...})} 时，里边...的代码将会在一个新的线程中执行（supplyAsync会开启异步任务）
 * 而 thenApply 我们传入的 function 的代码将会在上一个任务相同的线程中执行
 *
 * 2. Api是否以Async为后缀的区别：
 * (1)thenApply将在与上游任务相同的线程上调用（如果上游任务已经完成，则调用该线程）
 * (2)thenApplyAsync其实也还是要等上一个任务完成并把结果传到入参，然后将在提供的Executor（如果传入）上调用，否则将在默认的ForkJoinPool上调用
 * 其他遵循该命名规则的Api同理
 *
 * 3. thenApply、thenAccept、thenRun 方法的区别：
 * (1)thenApply：接受上游任务的结果，有返回值
 * (2)thenAccept：接受上游任务的结果，没有返回值
 * (3)thenRun：不接受上游任务的结果，也没有返回值
 *
 */
public class _01_thenApply {
    public static void main(String[] args) {
        SmallTool.printTimeAndThread("小白吃好了");
        SmallTool.printTimeAndThread("小白 结账、要求开发票");

        CompletableFuture<String> invoice = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("服务员收款 500元");
            SmallTool.sleepMillis(10000);
            return "500";
        }).thenApplyAsync(money -> {  //此处的入参是上一步return的结果
            SmallTool.printTimeAndThread(String.format("服务员开发票 面额 %s元", money));
            SmallTool.sleepMillis(200);
            return String.format("%s元发票", money);
        });

        SmallTool.printTimeAndThread("小白 接到朋友的电话，想一起打游戏");

        SmallTool.printTimeAndThread(String.format("小白拿到%s，准备回家", invoice.join()));
    }


    private static void one() {
        SmallTool.printTimeAndThread("小白吃好了");
        SmallTool.printTimeAndThread("小白 结账、要求开发票");

        CompletableFuture<String> invoice = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("服务员收款 500元");
            SmallTool.sleepMillis(100);
            SmallTool.printTimeAndThread("服务员开发票 面额 500元");
            SmallTool.sleepMillis(200);
            return "500元发票";
        });

        SmallTool.printTimeAndThread("小白 接到朋友的电话，想一起打游戏");

        SmallTool.printTimeAndThread(String.format("小白拿到%s，准备回家", invoice.join()));
    }


    private static void two() {
        SmallTool.printTimeAndThread("小白吃好了");
        SmallTool.printTimeAndThread("小白 结账、要求开发票");

        CompletableFuture<String> invoice = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("服务员收款 500元");
            SmallTool.sleepMillis(100);

            CompletableFuture<String> waiter2 = CompletableFuture.supplyAsync(() -> {
                SmallTool.printTimeAndThread("服务员开发票 面额 500元");
                SmallTool.sleepMillis(200);
                return "500元发票";
            });

            return waiter2.join();
        });

        SmallTool.printTimeAndThread("小白 接到朋友的电话，想一起打游戏");

        SmallTool.printTimeAndThread(String.format("小白拿到%s，准备回家", invoice.join()));
    }
}
