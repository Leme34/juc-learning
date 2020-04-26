package com.lee.juc.deadlock;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 死锁解决 -- 使用【消除等待环路】方法避免【哲学家就餐问题】中的死锁
 */
public class DiningPhilosophers {

    /**
     * 哲学家线程
     */
    @AllArgsConstructor
    @Data
    public static class Philosopher implements Runnable {

        private Object leftChopstick;  //当前哲学家左边的筷子
        private Object rightChopstick; //当前哲学家右边的筷子

        @Override
        public void run() {
            try {
                while (true) {
                    doAction("Thinking");
                    synchronized (leftChopstick) {
                        doAction("Picked up left chopstick");
                        synchronized (rightChopstick) {
                            doAction("Picked up right chopstick && eating");
                            doAction("Put down right chopstick");  //放下右边筷子（解锁）
                        }
                        doAction("Put down left chopstick");       //放下左边筷子（解锁）
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void doAction(String desc) throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " " + desc);
            Thread.sleep((long) (Math.random() * 10));
        }
    }

    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[5];        //5个哲学家线程(Runnable)
        Object[] chopsticks = new Object[philosophers.length];  //每人一根筷子
        for (int i = 0; i < chopsticks.length; i++) {           //初始化筷子
            chopsticks[i] = new Object();
        }
        // 启动所有哲学家线程，都一直尝试取一双筷子就餐，然后再放回这双筷子
        for (int i = 0; i < philosophers.length; i++) {
            Object leftChopstick = chopsticks[i]; //第i个哲学家对应的左边筷子
            Object rightChopstick = chopsticks[(i + 1) % chopsticks.length]; //第i个哲学家对应的右边筷子
            // 第i个哲学家开始去取一双筷子
            // 若是最后一位哲学家，则使用相反顺序的策略（先取右再取左），从而消除等待环路，防止死锁
            if (i == philosophers.length - 1) {
                philosophers[i] = new Philosopher(rightChopstick, leftChopstick);
            } else {  //否则都是先取左再取右
                philosophers[i] = new Philosopher(leftChopstick, rightChopstick);
            }
            // 启动当前哲学家线程
            new Thread(philosophers[i], "哲学家" + (i + 1) + "号").start();
        }
    }
}
