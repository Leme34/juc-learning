package com.lee.juc.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 模拟面试场景，所有面试者先在大厅集合，每次3人进入候考厅(队列长度为3)，一次面试1个人
 */
public class ArrayBlockingQueueDemo {

    private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

    public static void main(String[] args) {
        Runnable candidateTask = () -> {
            for (int i = 0; i < 10; i++) {
                String name = "Candidate" + i;
                try {
                    queue.put(name);
                    System.out.println(name + "已入队列");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                queue.put("null");   //作为程序结束标志
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Runnable interviewerTask = () -> {
            try {
                String name;
                while (!(name = queue.take()).equalsIgnoreCase("null")) {   //非结束标志则一直消费
                    System.out.println(name + "正在面试...");
                    Thread.sleep(5000);   //模拟面试耗时
                    System.out.println(name + "面试结束");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        System.out.println("本次共需要面试10个候选人");
        //候选人（生产者）
        Thread candidateThread = new Thread(candidateTask);
        candidateThread.start();
        //面试官（消费者）
        Thread interviewerThread = new Thread(interviewerTask);
        interviewerThread.start();
    }
}
