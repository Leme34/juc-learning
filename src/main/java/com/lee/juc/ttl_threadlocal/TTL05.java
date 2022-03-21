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
 * 安全的TTL案例
 */
@Slf4j
@RestController
public class TTL05 {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 2, 600L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final ThreadLocal<Map<String, String>> TTL = new TransmittableThreadLocal<Map<String, String>>() {
        /**
         * 线程初始化本地变量
         */
        @Override
        protected Map<String, String> initialValue() {
            log.info("请求线程initialValue...");
            return new HashMap<>();
        }

        /**
         * 因为TTL底层使用ITL（TTL extends ITL），会导致在new线程的时候，父子线程的数据传递，且无法销毁。
         *
         * 场景1：
         * Spring环境下在注入自定义Bean的扩展存在TTL的get操作，则项目启动的时候会触发，导致main线程存在TTL的value引用；
         * 当请求进入时，Tomcat开启一个线程处理，但Tomcat线程池（即请求线程池）没有被TtlExecutors装饰
         * main线程会将TTL(此时仅可相当于ITL)的值传递到子线程；
         * 子线程持有并修改TTL的引用，可能造成内存泄露；
         *
         * 场景2：
         * 如果使用普通线程池（没有被TtlExecutors装饰）执行异步任务，
         * 以下childValue实现即可把子线程获取的是父线程执行任务那个时刻的快照值重新赋值给新的HashMap，且父线程修改不影响子线程（非共享）
         *
         * 场景3:
         * 如果使用的是TtlExecutors装饰的线程池或者TtlRunnable、TtlCallable装饰的任务，此时就会变成引用共享，必须得重写copy方法才能实现非共享
         *
         * 所以此处重写为 new Thread() 时父线程不向子线程直接传递引用，防止ITL的潜在的内存泄漏。
         * 官方推荐使用DisableInheritableThreadFactory去装饰我们线程池的ThreadFactory，从而避免重写此方法
         */
        @Override
        protected Map<String, String> childValue(Map<String, String> parentValue) {
            //对于复杂的对象，可以采用序列化反序列化的方式进行深拷贝
            //String json = JSON.toJSONString(obj);
            //return JSON.parseObject(json, Map.class);
            return new HashMap<>(parentValue);
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

    @RequestMapping("/test05")
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
