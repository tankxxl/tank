package com.thinkgem.jeesite.modules.test.activiti;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 我们应该深深体会到Stream设计的原则--做什么，而不是怎么去做.
 * 当你使用Stream时，你会通过三个阶段来建立一个操作流水线
 * 1，创建一个Stream
 * 2，在一个或者多个步骤中，指定将初始的Stream转换为另外一个Stream的中间操作
 * 3，使用一个终端操作来产生一个结果，该操作会强制它之前的延迟操作立即执行，在这之后，该Stream就不会再被使用了。
 *
 *
 */
public class Java8Stream {

    public static void main(String[] args) {

        List<Integer> list = Lists.newArrayList(1, 2, 3);
        System.out.println(list.stream().filter(value -> value > 2).count());
    }
}
