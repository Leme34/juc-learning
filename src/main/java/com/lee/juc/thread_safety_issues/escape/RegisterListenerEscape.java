package com.lee.juc.thread_safety_issues.escape;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 对象发布的线程安全问题 -- 注册监听器隐晦的对象逸出导致线程不安全
 * 解决办法：1.监听器中不要引用共享变量，否则也会导致共享变量溢出 2.提供工厂方法保证类共享变量初始化完成后再发布对象，再注册监听器等...
 * <p>
 * Created by lsd
 * 2020-04-25 11:05
 */
public class RegisterListenerEscape {

    private static List<Button.Event> listOfEvents;  //逸出的对象

    public static void main(String[] args) {
        Button btn = new Button();
        // 1. 睡眠等待主线程注册完监听器，但还没执行到初始化共享变量语句的时候触发事件，而事件处理中操作了共享变量【因此将导致NPE】
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException ignored) {
            }
            btn.click(new Button.Event() {
            });
        }).start();
        // 2. 注册事件监听，事件处理中操作了共享变量
        btn.registerEventListener((e) -> {
            System.out.println("\n我被触发了~");
            listOfEvents.add(e);
        });
        // 3. 模拟其他耗时操作
        for (int i = 0; i < 10000; i++) {
            System.out.print(" ");
        }
        // 4. 初始化共享变量
        listOfEvents = new ArrayList<>();
    }

}

@Data
class Button {

    private EventListener listener;

    public void registerEventListener(EventListener listener) {
        this.listener = listener;
    }

    public void click(Event e) {
        if (listener == null) {
            throw new RuntimeException("listener not registered!");
        }
        listener.on(e);
    }

    interface EventListener {
        void on(Event e);
    }

    interface Event {
    }
}
