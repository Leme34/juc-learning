package com.lee.juc.completionService.demo1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 模拟网页渲染器（future版本）
 * <p>
 * 优点：渲染文本 与 下载图像 并行
 * 缺点：用户需要等到所有图像都下载完才能看到网页的图片
 * <p>
 * Created by lsd
 * 2019-10-22 21:09
 */
@Slf4j
public class FutureRender1 {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 模拟网页渲染
     */
    void renderPage(CharSequence source) {
        // 1 提取所有图片地址，3张
        List<String> imgUrls = List.of("www.baidu.com", "www.baidu.com", "www.baidu.com");
        // 2 下载所有图片，【6s】
        Callable<List<Image>> task = () ->
                imgUrls.stream().map(this::downloadImg).collect(Collectors.toList());
        Future<List<Image>> future = executor.submit(task);
        // 3 渲染文本，【2s】
        renderText(source);
        try {
            List<Image> images = future.get();
            // 4 渲染图片，【3s】
            renderImage(images);
        } catch (InterruptedException e) {
            // 恢复中断，因为无需返回结果，所以取消任务
            Thread.currentThread().interrupt();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 模拟渲染所有图片
     */
    @SneakyThrows
    private void renderImage(List<Image> images) {
        for (int i = 0; i < images.size(); i++) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 模拟渲染文本
     */
    @SneakyThrows
    private void renderText(CharSequence source) {
        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * 模拟下载图片
     */
    @SneakyThrows
    private Image downloadImg(String url) {
        TimeUnit.SECONDS.sleep(2);
        return new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    }

    public static void main(String[] args) {
        final long start = System.currentTimeMillis();
        new FutureRender1().renderPage("");
        final long end = System.currentTimeMillis();
        // 总耗时大概9s=6+3
        System.out.println("总耗时：" + (end - start) / 1000.0 + "s");
    }

}
