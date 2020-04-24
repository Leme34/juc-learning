package com.lee.juc.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

/**
 * 利用5个线程并发执行，num数字累加计数到10000，并打印
 * <p>
 * Created by lsd
 * 2019-09-30 11:20
 */

public class TestSynchronized {

    public static void main(String[] args) {
        final MyRunnableDemo runnable = new MyRunnableDemo();
        IntStream.range(0, 5).forEach(i ->
                new Thread(runnable).start()
        );
    }
}

@Slf4j
class MyRunnableDemo implements Runnable {

    private int number;

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (number >= 10000) {
                    break;
                }
                log.info("time=" + System.currentTimeMillis() + "  " + (++number));
            }
        }
    }

}
