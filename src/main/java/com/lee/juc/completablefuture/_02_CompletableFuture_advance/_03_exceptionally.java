package com.lee.juc.completablefuture._02_CompletableFuture_advance;

import org.ph.share.SmallTool;

import java.util.concurrent.CompletableFuture;

/**
 * 1. handler、whenComplete、exceptionally 方法的区别：
 * (1)handler：入参为 BiFunction<? super T, Throwable, ? extends U> fn，
 *    若上游任务能够成功执行则获取到类型为T的上游返回结果；若上游任务抛出异常，则会获取到上游抛出的Throwable子类异常。
 *    从而保证上游正常或异常都能被handler处理并返回。
 * (2)whenComplete：入参为 BiConsumer<? super T, ? super Throwable> action
 *    与 handler 的区别是，处理后没有返回值
 * (3)exceptionally：，当且仅当上游任务抛出异常，会进入exceptionally代码块中，有返回值
 *
 * https://www.bilibili.com/video/BV1ui4y1T7xf/
 */
public class _03_exceptionally {
    public static void main(String[] args) {
        SmallTool.printTimeAndThread("张三走出餐厅，来到公交站");
        SmallTool.printTimeAndThread("等待 700路 或者 800路 公交到来");
        CompletableFuture<String> bus = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("700路公交正在赶来");
            SmallTool.sleepMillis(100);
            return "700路到了";
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("800路公交正在赶来");
            SmallTool.sleepMillis(200);
            return "800路到了";
        }), firstComeBus -> {
            SmallTool.printTimeAndThread(firstComeBus);
            if (firstComeBus.startsWith("700")) {
                throw new RuntimeException("撞树了……");
            }
            return firstComeBus;
        }).exceptionally(e -> {   //上边任何一个任务抛出异常都会到exceptionally中来，也可以加在链子的中间
            SmallTool.printTimeAndThread(e.getMessage());
            SmallTool.printTimeAndThread("小白叫出租车");
            return "出租车 叫到了";
        });

        SmallTool.printTimeAndThread(String.format("%s,小白坐车回家", bus.join()));
    }
}
