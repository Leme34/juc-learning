package com.lee.juc.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * 参照：https://mp.weixin.qq.com/s?__biz=MzAxNjM2MTk0Ng==&mid=2247488866&idx=2&sn=710bdbf6c0a18bb9e80b4304a1be34c7&chksm=9bf4a5d7ac832cc1cb4eaa7e3d5c28826c8a88b2b0ec63c2b918496477f0b1d041728c0a0754&mpshare=1&scene=1&srcid=&sharer_sharetime=1570674552712&sharer_shareid=8a1cdd32486c9f92e64c6ab956392caa&key=ac0a697a2870cbd11d962b029fdb6e2c71c7ca79f305bb49790469c2cdd483fdf69babf006b72be4e6a3e8ecbe20ec5378b19005b3dd2fe4c4606569655b77cd9089f28ae62cc3a0dee83d6a2c9a0ca1&ascene=1&uin=MTE1MjQ1NDM2MQ%3D%3D&devicetype=Windows+10&version=62070141&lang=zh_CN&pass_ticket=JXLltkbHeNtqU%2B1WqPNuCIXjWCgBmjigTA6opEUyYW1nlQoW9BfuR%2FtDZ%2BTDcBO9
 *
 * CompletableFuture 核心优势：
 * 1）无需手工维护线程，给任务分配线程的工作无需开发人员关注；
 * 2）在使用上，语义更加清晰明确；
 * 例如：t3 = t1.thenCombine(t2, () -> { // doSomething ... } 能够明确的表述任务 3 要等任务 2 和 任务 1完成后才会开始执行。
 * 3）代码更加简练，支持链式调用，让你更专注业务逻辑。
 * 4）方便的处理异常情况
 *
 * ps：在生产环境下，不建议直接使用 CompletableFuture 的 supplyAsync()
 * 会交由 ForkJoinPool池中的某个执行线程（ Executor ）运行
 *
 *
 * 接下来，通过 CompletableFuture 来模拟实现专辑下多板块数据聚合处理。
 *
 * Created by lsd
 * 2019-10-10 16:50
 */
public class TestCompletableFuture2 {

    public static void main(String[] args) throws Exception {
        // 暂存数据
        List<String> stashList = new ArrayList<>();
        // 任务 1：调用推荐接口获取数据
        CompletableFuture<String> t1 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("T1: 获取推荐接口数据...");
                    sleepSeconds(5);
                    stashList.add("[T1 板块数据]");
                    return "[T1 板块数据]";
                });
        // 任务 2：调用搜索接口获取数据
        CompletableFuture<String> t2 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("T2: 调用搜索接口获取数据...");
                    sleepSeconds(3);
                    return " [T2 板块数据] ";
                });
        // 任务 3：任务 1 和任务 2 完成后执行，聚合结果
        CompletableFuture<String> t3 =
                t1.thenCombine(t2, (t1Result, t2Result) -> {
                    System.out.println(t1Result + " 与 " + t2Result + "实现去重逻辑处理");
                    return "[T1 和 T2 板块数据聚合结果]";
                });
        // 等待任务 3 执行结果
        System.out.println(t3.get(6, TimeUnit.SECONDS));
    }

    static void sleepSeconds(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
