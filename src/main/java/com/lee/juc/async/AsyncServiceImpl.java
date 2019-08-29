package com.lee.juc.async;


import com.lee.juc.future.demo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 实际是 spring 在扫描 bean 的时候会扫描方法上是否包含@Async的注解，
 * 如果包含的，spring 会为这个 bean 动态的生成一个子类，我们称之为代理类(jdkProxy)，
 * 代理类是继承我们所写的 bean 的，然后把代理类注入进来，那此时，在执行此方法的时候，会到代理类中，
 * 代理类判断了此方法需要异步执行，就不会调用父类 (我们原本写的 bean )的对应方法。
 * spring 自己维护了一个队列，他会把需要执行的方法，放入队列中，等待线程池去读取这个队列，完成方法的执行， 从而完成了异步的功能。
 *
 * 当调用在同一个类中的 @Async方法时，会导致异步失效，因为：
 * 调用者其实是this,是当前对象,不是真正的代理对象 xxxService ,spring 无法截获这个 @Async 方法调用，进而无法分配新的线程去执行
 * 所以不能在本类中去调用,网上的解决方法有：
 * 1、applicationContext.getBean(UserService.class)
 * 2、AopContext.currentProxy() + 启动类加 @EnableAspectJAutoProxy(exposeProxy = true) 来 暴露当前代理对象到当前线程绑定才能取得真正的代理对象
 */
@Service
@Async("taskExecutor")   //使用指定的线程池来作为任务的载体
public class AsyncServiceImpl{

    @Autowired
    private UserService userService;

    public Future<Long> queryUserMsgCount(final Long userId) {
        System.out.println("当前线程:" + Thread.currentThread().getName() + "=-=====queryUserMsgCount");
        long countByUserId = userService.countMsgCountByUserId(userId);
        return new AsyncResult<>(countByUserId);
    }

    public Future<Long> queryCollectCount(final Long userId) {
        System.out.println("当前线程:" + Thread.currentThread().getName() + "=-====queryCollectCount");
        long collectCount = userService.countCollectCountByUserId(userId);
        return new AsyncResult<>(collectCount);
    }

}
