package com.lee.juc.future;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.concurrent.*;

/**
 * 演示get的超时方法，需要注意超时后处理，调用future.cancel()。演示cancel传入true和false的区别，代表是否中断正在执行的任务。
 */
public class TestTimeout {

    private static final Ad DEFAULT_AD = new Ad("无网络时候的默认广告");
    private static final ExecutorService exec = Executors.newFixedThreadPool(10);

    @AllArgsConstructor
    @Data
    static class Ad {
        String name;
    }


    public static void printAd() {
        Future<Ad> future = exec.submit(() -> {    //考虑到任务可能被取消，因此阻塞操作需要保证正确处理（响应）中断异常
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Callable任务的sleep期间被中断了");
                return new Ad("被中断时候的默认广告");
            }
            return new Ad("旅游订票哪家强？找某程");
        });
        Ad ad;
        try {
            // 测试cancel，只有在get之前取消成功时才抛出CancellationException，因为get的时候会阻塞等待或抛出InterruptedException|ExecutionException|TimeoutException
//            future.cancel(true); //If the cancellation was successful it will fail the future with an {@link CancellationException}.
            ad = future.get(2000, TimeUnit.MILLISECONDS);
        } catch (CancellationException e) {     //只有Future.get()之前取消才会捕获到CancellationException
            ad = new Ad("被取消时候的默认广告");
            System.out.println("CancellationException...");
        } catch (InterruptedException e) {   //get阻塞等待被中断
            ad = new Ad("被中断时候的默认广告");
        } catch (ExecutionException e) {
            ad = new Ad("异常时候的默认广告");
        } catch (TimeoutException e) {
            ad = new Ad("超时时候的默认广告");
            System.out.println("超时，未获取到广告");
            //若mayInterruptIfRunning=false则正在执行的任务不会被取消，也不会发出中断信号
            //若mayInterruptIfRunning=true即使任务正在执行也要取消，会给执行中的任务发中断信号
            boolean cancel = future.cancel(true);
            System.out.println("cancel的结果：" + cancel);
        }
        exec.shutdown();
        System.out.println(ad);
    }

    public static void main(String[] args) {
        printAd();
    }
}

