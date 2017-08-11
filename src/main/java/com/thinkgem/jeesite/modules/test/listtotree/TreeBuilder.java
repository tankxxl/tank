package com.thinkgem.jeesite.modules.test.listtotree;

import com.thinkgem.jeesite.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeBuilder {


    /**
     * 有3种方法：
     * 1、双层for循环。
     * 2、递归
     * 3、以空间换时间，定义一个中间数据结构。最终用一个for循环实现。
     *
     * @param list
     * @return
     */
    public static List<TreeNode> buildLoop1(List<TreeNode> list) {
        List<TreeNode> tree = new ArrayList<TreeNode>();
        // 各自的children列表，key是item.id，这个数据结构是为了以空间换时间，把各节点的children信息记录下来
        Map<String, List<TreeNode>> childrenOfMap = new HashMap<String, List<TreeNode>>();

        TreeNode item; // 当前item
        String id; //
        String parentId; //
        List<TreeNode> childrenOf; //

        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            // 分解当前item
            id = item.getId();
            parentId = item.getParentId();

            // 得到当前item的孩子列表
            childrenOf = childrenOfMap.get(id);
            // 如果当前item的children列表为空，则新建并关联之
            if (childrenOf == null) {
                childrenOf = new ArrayList<TreeNode>();
                childrenOfMap.put(id, childrenOf);
            }
            // 当前item关联到孩子列表
            item.setChildren(childrenOfMap.get(id));

            // 如果当前item的parentId不为空，则此item一定是别人的child，提前设置
            if (StringUtils.isNotEmpty(parentId) && !parentId.equals("0")) {
                // 拿到他的父结点的children列表，把自己放进去。
                childrenOf = childrenOfMap.get(parentId);
                // 如果父结点还没有children列表，则要新建一个children List，并关联之
                if (childrenOf == null) {
                    childrenOf = new ArrayList<TreeNode>();
                    childrenOfMap.put(parentId, childrenOf);
                }
                // childrenOfMap.get(parentId).add(item);
                childrenOf.add(item); // *
            } else {
                tree.add(item); // *
            }
        }
        return tree;
    }

    public static List<TreeNode> buildLoop2(List<TreeNode> list) {
        List<TreeNode> tree = new ArrayList<TreeNode>();

        // 外层循环
        for (TreeNode node1 : list) {
            if ("0".equals(node1.getParentId()) || StringUtils.isEmpty(node1.getParentId())) {
                tree.add(node1);
            }

            // 内层循环查找孩子
            for (TreeNode node2 : list) {
                if (node2.getParentId() == node1.getId()) {
                    if (node1.getChildren() == null) {
                        node1.setChildren(new ArrayList<TreeNode>());
                    }
                    node1.getChildren().add(node2);
                }
            }
        }

        return tree;
    }

    public static List<TreeNode> buildByRecursive(List<TreeNode> list) {
        List<TreeNode> tree = new ArrayList<TreeNode>();
        for (TreeNode node : list) {
            if ("0".equals(node.getParentId()) || StringUtils.isEmpty(node.getParentId())) {
                tree.add(findChildren(node, list));
            }
        }
        return tree;
    }

    public static TreeNode findChildren(TreeNode treeNode, List<TreeNode> treeNodes) {

        for (TreeNode it : treeNodes) {
            if(treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<TreeNode>());
                }
                treeNode.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return treeNode;
    }
}
