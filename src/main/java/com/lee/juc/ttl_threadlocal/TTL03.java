package com.lee.juc.ttl_threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO 此案例为错误使用案例！！！
 * 默认情况下，父线程与子线程，子线程与子线程共同引用了 TTL 中的 map（变量共享）案例
 * TODO 若TTL存放的是对象引用，而TTL复制线程本地变量的默认实现是引用传递，就会导致主子线程中TTL引用的实际是同一个对象！！存在线程安全问题，所以需要重写TransmittableThreadLocal的copy方法
 */
@Slf4j
@RestController
public class TTL03 {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 2, 600L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final ThreadLocal<Map<String, String>> TTL = new TransmittableThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            log.info("请求线程initialValue...");
            return new HashMap<>();
        }
    };

    @RequestMapping("/test03")
    public void test() throws InterruptedException {
        TTL.get().put("请求线程1", "1");
        log.info(Thread.currentThread().getName() + ",map=" + TTL.get());

        CompletableFuture<Void> t1 = CompletableFuture.runAsync(Objects.requireNonNull(TtlRunnable.get(() -> {
            log.info("1start," + TTL.get());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignored) {
            }
            // TODO 父线程修改TTL，子线程能感知到TTL变化
            log.info(Thread.currentThread().getName() + ",map=" + TTL.get());
            TTL.get().put("子线程1", "1");
        })), EXECUTOR);
        CompletableFuture<Void> t2 = CompletableFuture.runAsync(Objects.requireNonNull(TtlRunnable.get(() -> {
            log.info("2start," + TTL.get());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ignored) {
            }
            // TODO 子线程2在子线程1后执行，此处会拿到子线程1放入map的数据
            log.info(Thread.currentThread().getName() + ",map=" + TTL.get());
        })), EXECUTOR);
        List<CompletableFuture<Void>> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);
        TTL.get().put("请求线程2", "2");
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        TimeUnit.SECONDS.sleep(5);
        // TODO 子线程中修改了TTL，主线程可以感知到
        log.info("请求线程结束,map="+TTL.get());
        TTL.remove();
    }
}
