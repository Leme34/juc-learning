package com.lee.juc.deadlock;

import java.util.concurrent.TimeUnit;

/**
 * 【单体架构下】模拟 账户a 给 账户b 转账的死锁情况
 * 根据锁对象hash值保证加锁顺序，避免因为加锁顺序相反而死锁
 */
public class TransferMoney implements Runnable {

    int flag = 1;
    static Account a = new Account(500);  //初始都有500，账户数越多同时转账死锁概率会变低，但是仍然会发生死锁
    static Account b = new Account(500);
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        TransferMoney r1 = new TransferMoney();
        TransferMoney r2 = new TransferMoney();
        r1.flag = 1;  //r1是a->b
        r2.flag = 0;  //r2是b->a
        Thread t1 = new Thread(r1);  //a给b转200
        Thread t2 = new Thread(r2);  //b给a转200
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("a的余额" + a.balance);
        System.out.println("b的余额" + b.balance);
    }

    @Override
    public void run() {
        if (flag == 1) {
            transferMoney(a, b, 200);
        }
        if (flag == 0) {
            transferMoney(b, a, 200);
        }
    }


    /**
     * 死锁版本
     */
    public static void transferMoneyWithDeadLock(Account from, Account to, int amount) {
        synchronized (from) {    //先锁源账户
            //【以下耗时代码的间隙会导致非连续获得锁,进而导致死锁】因为a->b与b->a这两次转账获取锁顺序是相反的
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ignored) {
            }
            synchronized (to) {  //再锁目标账户
                transfer(from, to, amount);
            }
        }
    }


    /**
     * 正确版本
     * 思路：避免相反的加锁顺序，传入此方法内的对象hash值是唯一不变的，用于对加锁顺序进行排序
     * 若是真实开发可以使用唯一的自增id，还可以避免以下hash冲突重新竞争另一把锁的情况
     */
    public static void transferMoney(Account from, Account to, int amount) {
        // 根据对象hash值大小调整加锁顺序，防止死锁
        int fromHash = System.identityHashCode(from);
        int toHash = System.identityHashCode(to);
        if (fromHash < toHash) {
            synchronized (from) {
                synchronized (to) {
                    transfer(from, to, amount);
                }
            }
        } else if (fromHash > toHash) {
            synchronized (to) {
                synchronized (from) {
                    transfer(from, to, amount);
                }
            }
        } else {  //对象hash冲突情况
            synchronized (lock) {  //让冲突的线程重新竞争一把独立的锁
                synchronized (to) {
                    synchronized (from) {
                        transfer(from, to, amount);
                    }
                }
            }
        }

    }

    /**
     * 转账逻辑
     *
     * @param from   源账户
     * @param to     目标账户
     * @param amount 金额
     */
    private static void transfer(Account from, Account to, int amount) {
        if (from.balance - amount < 0) {
            System.out.println("余额不足，转账失败。");
            return;
        }
        from.balance -= amount;
        to.balance = to.balance + amount;
        System.out.println("成功转账" + amount + "元");
    }

    static class Account {

        public Account(int balance) {
            this.balance = balance;
        }

        int balance;

    }
}
