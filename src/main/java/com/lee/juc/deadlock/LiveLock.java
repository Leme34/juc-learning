package com.lee.juc.deadlock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 活锁示例 -- 夫妻共同就餐问题
 * 活锁：线程间相互通信，但互相谦让谁都不先执行
 * 死锁：线程间没有通信，谁都不先释放锁
 * <p>
 * 真正工程中可能出现在消息队列服务的消息处理策略中
 * 例如：
 *  队列中的消息处理失败就放在【队头】进行重试，而此时处理消息的依赖服务出了问题，则处理消息会一直失败，那么即使程序没有阻塞，程序也无法继续执行，导致活锁
 * 解决办法：
 *  队列中的消息处理失败应该放在【队尾】进行重试，而且要加上重试次数限制，若超出次数则持久化到数据库定时再去重试
 *
 * Created by lsd
 * 2020-04-26 08:50
 */
public class LiveLock {

    public static void main(String[] args) {
        Diner husband = new Diner("牛郎");
        Diner wife = new Diner("织女");
        Spoon spoon = new Spoon(husband);
        new Thread(() -> husband.eatWith(spoon, wife)).start();
        new Thread(() -> wife.eatWith(spoon, husband)).start();
    }

}


/**
 * 勺子
 */
@Slf4j
@AllArgsConstructor
@Data
class Spoon {
    private Diner owner;  //当前持有者

    /**
     * 加锁并使用勺子就餐
     */
    public synchronized void use() {
        log.debug("{}已就餐", owner.getName());
    }
}

/**
 * 就餐者
 */
@Slf4j
@Data
class Diner {
    private String name;
    private boolean isHungry;  //是否需要就餐

    public Diner(String name) {
        this.name = name;
        this.isHungry = true;
    }

    /**
     * 夫妻共同就餐
     *
     * @param spoon  勺子
     * @param spouse 一起就餐的人
     */
    public void eatWith(Spoon spoon, Diner spouse) {
        while (isHungry) {  //需要就餐时一直循环
            if (spoon.getOwner() != this) {  //当前没拿到勺子，1s后重试
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                continue;
            }
            // 当前持有勺子，若对方需要就餐则先让给对方，必须增加条件去打破僵局，否则一直互相谦让（活锁）
            Random random = new Random();
            if (spouse.isHungry && random.nextInt(10) < 9) {    //90%的概率相互谦让
                spoon.setOwner(spouse);
                log.debug("{}把勺子让给了{}", name, spouse.name);
                continue;
            }
            // 持有勺子 && 对方不饿
            spoon.use();
            isHungry = false;
            spoon.setOwner(spouse);  //勺子交给对方
        }
    }
}
