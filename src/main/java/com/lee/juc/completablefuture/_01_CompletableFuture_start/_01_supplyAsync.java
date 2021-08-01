package com.lee.juc.completablefuture._01_CompletableFuture_start;

import org.ph.share.SmallTool;

import java.util.concurrent.CompletableFuture;

/**
 * runAsync 与 supplyAsync 都是开启一个异步任务，区别是前者没有返回值而后者有
 *
 * https://www.bilibili.com/video/BV1nA411g7d2/
 */
public class _01_supplyAsync {
    public static void main(String[] args) {
        SmallTool.printTimeAndThread("小白进入餐厅");
        SmallTool.printTimeAndThread("小白点了 番茄炒蛋 + 一碗米饭");

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("厨师炒菜");
            SmallTool.sleepMillis(200);
            SmallTool.printTimeAndThread("厨师打饭");
            SmallTool.sleepMillis(100);
            return "番茄炒蛋 + 米饭 做好了";
        });

        SmallTool.printTimeAndThread("小白在打王者");
        SmallTool.printTimeAndThread(String.format("%s ,小白开吃", cf1.join()));
    }
}
