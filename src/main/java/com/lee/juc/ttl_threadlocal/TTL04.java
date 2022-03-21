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
 * 子线程浅复制父线程的map（非变量共享）案例，父子线程/子线程之间map数据不会相互影响
 * TODO 此案例还有可能存在内存泄露问题，解决办法见 {@link TTL05}
 */
@Slf4j
@RestController
public class TTL04 {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 2, 600L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final ThreadLocal<Map<String, String>> TTL = new TransmittableThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            log.info("请求线程initialValue...");
            return new HashMap<>();
        }

        /**
         * 父子线程拷贝对象
         * 如果使用的是TtlExecutors装饰的线程池或者TtlRunnable、TtlCallable装饰的任务
         * 需要重写copy方法且重新赋值给新的HashMap，不然会导致父子线程都是持有同一个引用，只要有修改取值都会变化。引用值线程不安全
         *
         * @param parentValue 父线程执行子任务那个时刻的快照值，后续父线程再次set值也不会影响子线程get，因为已经不是同一个引用
         */
        @Override
        public Map<String, String> copy(Map<String, String> parentValue) {
            //对于复杂的对象，可以采用序列化反序列化的方式进行深拷贝
            //String json = JSON.toJSONString(obj);
            //return JSON.parseObject(json, Map.class);
            return new HashMap<>(parentValue);
        }
    };

    @RequestMapping("/test04")
    public void test() {
        TTL.get().put("请求线程1", "1");
        log.info(Thread.currentThread().getName() + ",map=" + TTL.get());

        CompletableFuture<Void> t1 = CompletableFuture.runAsync(Objects.requireNonNull(TtlRunnable.get(() -> {
            log.info("1start," + TTL.get());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ignored) {
            }
            log.info(Thread.currentThread().getName() + ",map=" + TTL.get());
            TTL.get().put("子线程1", "1");
        })), EXECUTOR);
        CompletableFuture<Void> t2 = CompletableFuture.runAsync(Objects.requireNonNull(TtlRunnable.get(() -> {
            log.info("2start," + TTL.get());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ignored) {
            }
            // TODO 若该子线程2比子线程1后执行则此处会拿到子线程1放入map的数据
            log.info(Thread.currentThread().getName() + ",map=" + TTL.get());
        })), EXECUTOR);
        List<CompletableFuture<Void>> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        TTL.get().put("请求线程2", "2");
        log.info("请求线程结束");
        TTL.remove();
    }
}
