package com.lee.juc.completionService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.*;

/**
 * 模拟网页渲染器（CompletionService版本）
 * <p>
 * 优点：
 * 1.串行下载->并行下载，为每幅图像下载都创建一个独立任务，并在线程池执行他们
 * 2.从 CompletionService 中获取每张图片下载结果，下载完后立刻渲染显示
 * <p>
 * Created by lsd
 * 2019-10-22 21:09
 */
@Slf4j
public class FutureRender2 {
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    /**
     * 模拟网页渲染
     */
    void renderPage(CharSequence source) {
        // 1 提取所有图片地址，3张
        List<String> imgUrls = List.of("www.baidu.com", "www.baidu.com", "www.baidu.com");
        // 2 下载所有图片
        CompletionService<Image> completionService = new ExecutorCompletionService<>(executor);
        // TODO 为每幅图像下载都创建一个独立任务，并在线程池执行他们
        imgUrls.forEach(url ->
                completionService.submit(() -> this.downloadImg(url))
        );
        // 3 渲染文本，【2s】
        renderText(source);
        // TODO 并行下载渲染图片：从 CompletionService 中获取每张图片下载结果，下载完后立刻渲染
        try {
            for (int i = 0; i < imgUrls.size(); i++) {
                Future<Image> future = completionService.take();
                Image image = future.get();
                renderImage(image);          //【1s*3】
            }
        } catch (InterruptedException e) {
            // 恢复中断，因为无需返回结果，所以取消任务
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 模拟渲染单张图片
     */
    @SneakyThrows
    private void renderImage(Image image) {
        log.info("renderImage...");
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 模拟渲染文本
     */
    @SneakyThrows
    private void renderText(CharSequence source) {
        TimeUnit.SECONDS.sleep(2);
        log.info("文本渲染完成");
    }

    /**
     * 模拟下载图片
     */
    @SneakyThrows
    private Image downloadImg(String url) {
        TimeUnit.SECONDS.sleep(2);
        log.info("图片下载完成");
        return new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    }

    public static void main(String[] args) {
        log.info("开始！");
        final long start = System.currentTimeMillis();
        new FutureRender2().renderPage("");
        final long end = System.currentTimeMillis();
        log.info("结束！");
        // 总耗时大概5s=2+3
        System.out.println("总耗时：" + (end - start) / 1000.0 + "s");
    }

}
