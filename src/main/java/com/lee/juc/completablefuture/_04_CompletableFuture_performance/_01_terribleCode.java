package com.lee.juc.completablefuture._04_CompletableFuture_performance;

import org.ph.share.SmallTool;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * 演示一个个join异步任务的错误使用方法
 *
 * https://www.bilibili.com/video/BV1e44y1677h/
 */
public class _01_terribleCode {
    public static void main(String[] args) {

        SmallTool.printTimeAndThread("小白和小伙伴们 进餐厅点菜");
        long startTime = System.currentTimeMillis();

        ArrayList<Dish> dishes = new ArrayList<>();
        // 点菜
        for (int i = 1; i <= 10; i++) {
            Dish dish = new Dish("菜" + i, 1);
            dishes.add(dish);
        }
        // 做菜
        for (Dish dish : dishes) {
            CompletableFuture.runAsync(() -> dish.make()).join();
        }

        SmallTool.printTimeAndThread("菜都做好了，上桌 " + (System.currentTimeMillis() - startTime));

    }
}
