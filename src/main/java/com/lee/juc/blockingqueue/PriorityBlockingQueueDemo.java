package com.lee.juc.blockingqueue;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * PriorityBlockingQueue的特点：
 * 1.无界队列（会扩容），put()不阻塞，但队列为空的时候take()会阻塞
 * 2.offer方法不限制大小可扩容
 * 3.支持优先级，队列中的数据默认自然排序（可传自定义comparator）
 * <p>
 * Created by lsd
 * 2020-04-29 16:12
 */
public class PriorityBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        PriorityBlockingQueue<Integer> priorityBlockingQueue = new PriorityBlockingQueue<>(2);
        for (int i = 0; i < 100; i++) {
            priorityBlockingQueue.put(i);
        }
        System.out.println("priorityBlockingQueue.size()=" + priorityBlockingQueue.size());
        System.out.println("priorityBlockingQueue=" + priorityBlockingQueue);
        System.out.println("priorityBlockingQueue.take()=" + priorityBlockingQueue.take());  //取一个元素
        System.out.println("priorityBlockingQueue=" + priorityBlockingQueue);
    }

}
