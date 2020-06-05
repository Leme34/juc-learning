package com.lee.juc.atomic;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用原子引用实现线程安全的扣减余额
 * <p>
 * Created by lsd
 * 2020-06-04 16:48
 */
@Slf4j
public class AtomicReferenceBigDecimal {

    // BigDecimal对象的原子引用
    private static AtomicReference<BigDecimal> accountBalance;


    /**
     * 读取余额
     *
     * @return 账户余额
     */
    public static BigDecimal getBalance() {
        return accountBalance.get();
    }


    /**
     * 线程安全的取款
     *
     * @param amount 金额
     */
    public static void withdraw(BigDecimal amount) {
        BigDecimal before = getBalance();
        BigDecimal after = before.subtract(amount);
        while (!accountBalance.compareAndSet(before, after)) {
            log.debug("自旋重试，before={}，after={}", before, after);
        }
        log.debug("成功，before={}，after={}", before, after);
    }


    /**
     * 初始账户余额为50，5个线程并发，每个线程取出10元
     * 运行的结果：最后一个线程会把账户余额减为0
     * 【若竞争激烈，无法得出结果运行多几次即可】因为CAS不适用于这种线程数>CPU数的场景！！！
     */
    public static void main(String[] args) {
        // 初始50元
        AtomicReferenceBigDecimal.accountBalance = new AtomicReference<>(new BigDecimal("50"));

        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                withdraw(BigDecimal.TEN);
            }).start();
        }
        countDownLatch.countDown();
    }

}
