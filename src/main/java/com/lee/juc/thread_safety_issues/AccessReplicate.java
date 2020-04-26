package com.lee.juc.thread_safety_issues;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 解决共享对象访问的不安全 -- 副本
 * <p>
 * Created by lsd
 * 2020-04-25 12:50
 */
public class AccessReplicate {

    @Getter
    private Map<String, String> weekday;

    AccessReplicate() {
        weekday = new HashMap<>();
        weekday.put("1", "周一");
        weekday.put("2", "周二");
        weekday.put("3", "周三");
        weekday.put("4", "周四");
    }

    /**
     * 访问副本避免直接操作共享变量，保证线程安全
     * @return 共享变量的副本
     */
    public Map<String, String> getWeekdaySafe() {
        return new HashMap<>(weekday);
    }

    public static void main(String[] args) {
        AccessReplicate a = new AccessReplicate();
        // 模拟一个操作了共享变量，例如删除了weekday中的星期一
//        a.getWeekday().remove("1");
//        System.out.println(a.getWeekday().get("1").toString());  //模拟另一个线程正在使用weekday中的星期一
        a.getWeekdaySafe().remove("1");
        System.out.println(a.getWeekdaySafe().get("1").toString());  //访问副本避免直接操作共享变量，保证线程安全

    }

}
