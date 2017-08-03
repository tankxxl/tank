package com.thinkgem.jeesite.modules.test.listtotree;

import com.thinkgem.jeesite.common.mapper.JsonMapper;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        TreeNode treeNode1 = new TreeNode("1","广州","0");
        TreeNode treeNode2 = new TreeNode("2","深圳","0");

        TreeNode treeNode3 = new TreeNode("3","天河区",treeNode1);
        TreeNode treeNode4 = new TreeNode("4","越秀区",treeNode1);
        TreeNode treeNode5 = new TreeNode("5","黄埔区",treeNode1);
        TreeNode treeNode6 = new TreeNode("6","石牌",treeNode3);
        TreeNode treeNode7 = new TreeNode("7","百脑汇",treeNode6);


        TreeNode treeNode8 = new TreeNode("8","南山区",treeNode2);
        TreeNode treeNode9 = new TreeNode("9","宝安区",treeNode2);
        TreeNode treeNode10 = new TreeNode("10","科技园",treeNode8);


        List<TreeNode> list = new ArrayList<TreeNode>();

        list.add(treeNode1);
        list.add(treeNode2);
        list.add(treeNode3);
        list.add(treeNode4);
        list.add(treeNode5);
        list.add(treeNode6);
        list.add(treeNode7);
        list.add(treeNode8);
        list.add(treeNode9);
        list.add(treeNode10);

        // List<TreeNode> trees = TreeBuilder.bulid(list);
        // List<TreeNode> trees_ = TreeBuilder.buildByRecursive(list);
        List<TreeNode> tree1 = TreeBuilder.buildLoop1(list);
        List<TreeNode> tree2 = TreeBuilder.buildLoop2(list);
        List<TreeNode> tree3 = TreeBuilder.buildByRecursive(list);

        System.out.println("============");
        String jsonTree = JsonMapper.toJsonString(tree1);
        System.out.println(jsonTree);

        System.out.println("======2======");
        jsonTree = JsonMapper.toJsonString(tree2);
        System.out.println(jsonTree);

        System.out.println("======3======");
        jsonTree = JsonMapper.toJsonString(tree3);
        System.out.println(jsonTree);

    }
}
