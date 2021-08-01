package com.lee.juc.completablefuture._02_CompletableFuture_advance;

import org.ph.share.SmallTool;

import java.util.concurrent.CompletableFuture;

/**
 * 1. acceptEither、applyToEither、runAfterEither 方法的区别：
 * (1)acceptEither：接收上游两个任务中最先完成任务的结果，没有返回值
 * (2)applyToEither：接收上游两个任务中最先完成任务的结果，有返回值
 * (3)runAfterEither：不接收上游两个任务中最先完成任务的结果，也没有返回值
 *
 * https://www.bilibili.com/video/BV1ui4y1T7xf/
 */
public class _02_applyToEither {
    public static void main(String[] args) {
        SmallTool.printTimeAndThread("张三走出餐厅，来到公交站");
        SmallTool.printTimeAndThread("等待 700路 或者 800路 公交到来");

        CompletableFuture<String> bus = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("700路公交正在赶来");
            SmallTool.sleepMillis(100);
            return "700路到了";
        }).applyToEither(CompletableFuture.supplyAsync(() -> {  //上一个任务与此任务一起运行，谁先完成就把谁的结果传入Function的入参
            SmallTool.printTimeAndThread("800路公交正在赶来");
            SmallTool.sleepMillis(200);
            return "800路到了";
        }), firstComeBus -> firstComeBus);

        SmallTool.printTimeAndThread(String.format("%s,小白坐车回家", bus.join()));
    }
}
