package com.lee.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

public class CallableDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CallableImpl callable = new CallableImpl();
        FutureTask<Integer> futureTask = new FutureTask<>(callable);

        new Thread(futureTask).start();

        // 会阻塞当前线程，相当于闭锁
        Integer result = futureTask.get();
        System.out.println("result=" + result);
        System.out.println("程序结束---------");


    }


}


class CallableImpl implements Callable<Integer> {

    private AtomicInteger i = new AtomicInteger();

    @Override
    public Integer call() throws Exception {
        for (int j = 0; j < 10; j++){
            i.getAndIncrement();
        }
        return i.get();
    }

}
