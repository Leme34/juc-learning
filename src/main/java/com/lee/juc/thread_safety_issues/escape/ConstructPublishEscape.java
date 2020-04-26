package com.lee.juc.thread_safety_issues.escape;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 对象发布的线程安全问题 -- 构造函数不安全发布对象（逸出）
 * <p>
 * Created by lsd
 * 2020-04-25 10:44
 */
@Slf4j
public class ConstructPublishEscape {

    public static Point p;

    @SneakyThrows
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            new Point(1, 1);
        });
        thread.start();
        TimeUnit.MILLISECONDS.sleep(500); //模拟刚好执行到构造函数中ObjectPublish.p = this;的时机
        log.debug(p.getY().toString());          //使用未初始化的y，抛出NPE
    }

}

@Data
class Point {
    private final Integer x, y;

    /**
     * 不安全发布的构造函数
     */
    @SneakyThrows
    Point(int x, int y) {
        this.x = x;
        ConstructPublishEscape.p = this;            //未完成初始化就发布对象
        TimeUnit.SECONDS.sleep(1);  //模拟其他逻辑耗时
        this.y = y;
    }
}
