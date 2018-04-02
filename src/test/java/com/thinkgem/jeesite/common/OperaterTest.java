package com.thinkgem.jeesite.common;

import org.junit.Test;

public class OperaterTest {

    @Test
    public void testOr() {
        String a = "123";
        String b = "456";
        boolean b1 = true;
        boolean b2 = false;
        if (b1 || b2) {
            System.out.println(true);
        }
    }
}
