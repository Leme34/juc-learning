package com.lee.juc.volatile_learning;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

/**
 * DCL（Double Check Lock）双重检测锁机制 + volatile 的懒汉式单例模式
 * <p>
 * 原因：
 * instance = new VolatileSingleton(); 是非原子性操作，可分为以下3步：
 * 1.分配对象内存空间
 * 2.初始化对象
 * 3.把 instance 指向刚分配的内存地址（但instance!=null）
 * <p>
 * 若步骤3 重排序 到步骤2之前，
 * 则 instance 还没完成初始化，但其他线程判断 instance!=null 就被 return 拿去使用了
 * 因此需要使用 volatile 禁止指令重排，保证线程安全
 * <p>
 * Created by lsd
 * 2019-11-07 23:42
 */
@Slf4j
public class VolatileSingleton {

    private static volatile VolatileSingleton instance = null;

    private VolatileSingleton() {
        log.info("create instance...");
    }

    /**
     * 直接使用 synchronized 方法存在效率问题
     */
    public static VolatileSingleton getInstance() {
        if (instance == null) { //若已经有了则无需加锁，直接返回，提高性能
            synchronized (VolatileSingleton.class) { //加锁保证原子操作
                if (instance == null) {
                    instance = new VolatileSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        IntStream.range(0, 100).forEach(i ->
                new Thread(VolatileSingleton::getInstance).start()
        );
    }

}
