package com.lee.juc.completablefuture._04_CompletableFuture_performance;

import org.ph.share.SmallTool;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * 使用CompletableFuture.allOf.join()，等待集合中的所有任务执行完毕，再继续往下执行
 *
 * https://www.bilibili.com/video/BV1e44y1677h/
 */
public class _02_terribleCodeImprove {
    public static void main(String[] args) {

        SmallTool.printTimeAndThread("小白和小伙伴们 进餐厅点菜");
        long startTime = System.currentTimeMillis();
        // 点菜
        ArrayList<Dish> dishes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Dish dish = new Dish("菜" + i, 1);
            dishes.add(dish);
        }
        // 做菜
        ArrayList<CompletableFuture> cfList = new ArrayList<>();
        for (Dish dish : dishes) {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> dish.make());
            cfList.add(cf);
        }
        // 等待集合中的所有任务执行完毕，再继续往下执行
        CompletableFuture.allOf(cfList.toArray(new CompletableFuture[cfList.size()])).join();

        SmallTool.printTimeAndThread("菜都做好了，上桌 " + (System.currentTimeMillis() - startTime));

    }
}
