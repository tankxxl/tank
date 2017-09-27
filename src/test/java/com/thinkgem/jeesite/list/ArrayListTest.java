package com.thinkgem.jeesite.list;

import java.util.ArrayList;
import java.util.List;

public class ArrayListTest {

    public static Example example = new Example();

    public static void main(String[] args) throws InterruptedException {

        //只是声明了不可变，但是没有进行线程封闭，实现监视器模式
        final List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < 10; i++) {
            new Thread() {
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        list.add(1000 * j + j);  // 线程情况出现在这里
                        example.increment();
                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName);
                    }
                }
            }.start();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 结论：
        // 因为list.add和example.increment同时在线程中执行，
        // 本来list.size()和example.value应该是一样的。
        // 由于list不是线程安全的，所以在add时有可能出问题。
        System.out.println("list size: " + list.size());
        System.out.println("Example: " + example.getValue());
    }
}

// 用一个类(class Example)来包装一个数据(int value)
// 此时类相当于里面数据的代理。代理类可以发挥想象，做很多的工作，如：线程安全的操作。
//
// 再抽象一点可以说，类是数据(可以包装很多数据)的包装，对数据的访问形成了类的方法。
// 也可以说，有多少种数据就有多少个类。

class Example {
    private int value;
    //一个单纯的线程锁，但基本保证并发
    public synchronized int getValue() {
        return value;
    }

    public synchronized int increment() {
        return ++value;
    }
}