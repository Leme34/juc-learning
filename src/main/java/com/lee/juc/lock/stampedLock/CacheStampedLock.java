package com.lee.juc.lock.stampedLock;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 * 对于读多写少的高并发场景 StampedLock的性能很好，
 * 通过乐观读模式很好的解决了写线程“饥饿”的问题，我们可以使用StampedLock 来代替ReentrantReadWriteLock ，
 * 但是需要注意的是 StampedLock 的功能仅仅是 ReadWriteLock 的子集，在使用的时候，还是有几个地方需要注意一下：
 * 1）StampedLock是不可重入锁，使用过程中一定要注意；
 * 2）悲观读、写锁都不支持条件变量 Conditon ，当需要这个特性的时候需要注意；
 * 3）如果线程阻塞在 StampedLock 的 readLock() 或者 writeLock() 上时，此时调用该阻塞线程的 interrupt() 方法，会导致 CPU 飙升。所以，使用 StampedLock 一定不要调用中断操作，如果需要支持中断功能，一定使用可中断的悲观读锁 readLockInterruptibly() 和写锁 writeLockInterruptibly()。这个规则一定要记清楚。
 */
public class CacheStampedLock {
    /**
     * 共享变量数据
     */
    private final Map<Integer, String> idMap = new HashMap<>();
    private final StampedLock lock = new StampedLock();

    /**
     * 添加数据，独占写锁
     */
    public void put(Integer key, String value) {
        long stamp = lock.writeLock();
        try {
            idMap.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 读取数据，只读方法
     * 先乐观读（读写可以并发执行），若乐观读期间缓存已被修改则上悲观读锁并重新读取缓存
     */
    public String get(Integer key) {
        // 1. 尝试通过乐观读模式读取数据，非阻塞
        long stamp = lock.tryOptimisticRead();
        // 2. 读取数据到当前线程栈
        String currentValue = idMap.get(key);
        // 3. 校验是否被其他线程修改过,true 表示未修改，否则需要加悲观读锁
        if (!lock.validate(stamp)) {
            // 4. 上悲观读锁，并重新读取数据到当前线程局部变量
            stamp = lock.readLock();
            try {
                currentValue = idMap.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        // 5. 若校验通过，则直接返回数据
        return currentValue;
    }

    /**
     * 如果数据不存在则从数据库读取添加到 map 中,锁升级运用
     * @param key
     * @param value 可以理解成从数据库读取的数据，假设不会为 null
     * @return
     */
    public String putIfNotExist(Integer key, String value) {
        // 获取读锁，也可以直接调用 get 方法使用乐观读
        long stamp = lock.readLock();
        String currentValue = idMap.get(key);
        // 缓存为空则尝试上写锁从数据库读取数据并写入缓存
        try {
            while (Objects.isNull(currentValue)) {
                // 尝试升级写锁
                long wl = lock.tryConvertToWriteLock(stamp);
                // 不为 0 升级写锁成功
                if (wl != 0L) {
                    // 模拟从数据库读取数据, 写入缓存中
                    stamp = wl;
                    currentValue = value;
                    idMap.put(key, currentValue);
                    break;
                } else {
                    // 升级失败，释放之前加的读锁并上写锁，通过循环再试
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                }
            }
        } finally {
            // 释放最后加的锁
            lock.unlock(stamp);
        }
        return currentValue;
    }
}
