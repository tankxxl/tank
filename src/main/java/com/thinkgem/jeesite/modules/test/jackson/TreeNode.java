package com.thinkgem.jeesite.modules.test.jackson;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @JsonBackReference和@JsonManagedReference：这两个标注通常配对使用，通常用在父子关系中。
 * @JsonBackReference标注的属性在序列化（serialization，即将对象转换为json数据）时，
 * 会被忽略（即结果中的json数据不包含该属性的内容）。
 * @JsonManagedReference标注的属性则会被序列化。
 *
 * 在序列化时，@JsonBackReference的作用相当于@JsonIgnore，此时可以没有@JsonManagedReference。
 * 但在反序列化（deserialization，即json数据转换为对象）时，如果没有@JsonManagedReference，
 * 则不会自动注入@JsonBackReference标注的属性（被忽略的父或子）；如果有@JsonManagedReference，
 * 则会自动注入自动注入@JsonBackReference标注的属性。
 *
 */
public class TreeNode {
    String name;

    @JsonBackReference
//  @JsonIgnore
    TreeNode parent;

    @JsonManagedReference
    List<TreeNode> children;

    public TreeNode() {
    }

    public TreeNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public void addChild(TreeNode child) {
        if (children == null)
            children = new ArrayList<TreeNode>();
        children.add(child);
    }
}