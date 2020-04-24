package com.lee.juc.lock.producer_consumer.reentrantlock_and_condition;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 ReentrantLock + Condition 线程安全地解决 生消问题
 * <p>
 * Created by lsd
 * 2019-09-22 11:08
 */
@Getter
public class ProductQueue<T> {

    // 产品队列
    private final T[] items;
    private final static int DEFAULT_SIZE = 10;

    // 队列当前产品数量
    private int count;
    private int head;
    private int tail;

    // 抢占式的锁对象
    private final Lock lock = new ReentrantLock();
    // 两个 wait-set
    private Condition full = lock.newCondition();   //队列满了
    private Condition empty = lock.newCondition();  //队列为空

    // 有参构造
    public ProductQueue(int maxSize) {
        items = (T[]) new Object[maxSize];
    }

    // 默认构造
    public ProductQueue() {
        this(DEFAULT_SIZE);
    }

    /**
     * 生产产品，放入产品队列
     */
    public void put(T product) {
        // 锁住临界资源
        lock.lock();
        try {
            //队列满了不生产
            while (count == items.length) {
                System.out.println("产品队列满了");
                full.await();  //有点类似于Object.wait,挂起，会释放锁
            }
            // 放入队列，更新count
            items[tail++] = product;
            count++;
            // 重置尾指针（循环队列）
            if (tail >= items.length) {
                tail = 0;
            }
            //有点类似于Object.notifyAll
            empty.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 消费产品
     */
    public T take() {
        // 锁住临界资源
        lock.lock();
        try {
            // 队列为空不消费
            while (count == 0) {
                System.out.println("产品队列为空");
                empty.await();
            }
            T item = items[head++];
            count--;
            //重置头指针（循环队列）
            if (head >= items.length) {
                head = 0;
            }
            //有点类似于Object.notifyAll,挂起，会释放锁
            full.signalAll();
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }


    /**
     * 1个生产者，2个消费者 的测试
     */
    public static void main(String[] args){
        // 生产者、消费者共用的队列
        ProductQueue<Object> productQueue = new ProductQueue<>();
        // 创建、启动生产者
        Producer producer = new Producer(productQueue);
        new Thread(producer).start();
        // 等待所有消费者创建完成
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i=0;i<2;i++){
            new Consumer(productQueue,countDownLatch).start();
        }
        System.out.println("所有消费者创建完成...");
        // 所有消费者开始抢夺产品
        countDownLatch.countDown();
    }
}
