package com.lee.juc.lock.reentrantlock_and_condition;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示ReentrantLock中的Condition用法
 * 1.要想使用Condition必须先获取锁
 * 2.await()会自动释放持有的lock锁（与Object.wait()一样，不需要另外手动释放lock，只是为了防止死锁还是需要在finally代码块释放锁）
 * <p>
 * Created by lsd
 * 2020-04-29 17:28
 */
@Slf4j
public class ConditionDemo {
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    private static void work() {
        lock.lock();
        try {
            log.debug("等待Condition...");
            condition.await();
            log.debug("Condition已被唤醒，开始执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static void getReady() {
        lock.lock();
        try {
            log.debug("准备工作完成，唤醒Condition...");
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        // 不能线程自己唤醒自己，所以至少需要两个线程
        new Thread(ConditionDemo::work).start(); //线程1调用工作方法
        new Thread(() -> {  //线程2唤醒线程1
            try {
                TimeUnit.SECONDS.sleep(5);
                getReady();
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

}
