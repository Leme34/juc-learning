package com.lee.juc.thread_safety_issues.escape;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 对象发布的线程安全问题 -- 开启子线程初始化共享变量导致NPE
 */
public class MultiThreadsInitEscape {

    @Getter
    private Map<String, String> weekday;

    MultiThreadsInitEscape() {
        // 错误做法：不应该开启子线程去初始化共享变量，会导致其他线程访问可能NPE
        new Thread(() -> {
            weekday = new HashMap<>();
            weekday.put("1", "周一");
            weekday.put("2", "周二");
            weekday.put("3", "周三");
            weekday.put("4", "周四");
        }).start();
    }

    public static void main(String[] args) {
        MultiThreadsInitEscape mtie = new MultiThreadsInitEscape();
        System.out.println(mtie.getWeekday().get("1"));  //会导致NPE
    }
}
