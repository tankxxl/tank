package com.thinkgem.jeesite.guava;

import org.junit.Test;
import com.google.common.base.Preconditions;

/**
 * Guava学习笔记：Preconditions优雅的检验参数
 */
public class PreconditionsTest {
    
    @Test
    public void Preconditions() throws Exception { 
        
        getPersonByPrecondition(8,"peida");
        
        try {
            getPersonByPrecondition(-9,"peida");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        try {
            getPersonByPrecondition(8,"");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        try {
            getPersonByPrecondition(8,null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
    }
    
    public static void getPersonByPrecondition(int age,String neme)throws Exception {
        Preconditions.checkNotNull(neme, "neme为null");
        Preconditions.checkArgument(neme.length()>0, "neme为\'\'");
        Preconditions.checkArgument(age>0, "age 必须大于0");
        System.out.println("a person age:" + age + ",neme:" + neme);
    }
}