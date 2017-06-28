package com.thinkgem.jeesite.modules.project.entity.tech;

import java.io.Serializable;

/**
 * 工单-工时 2维数组封装类
 * Created by ArthurZhang on 2016/3/15.
 */
public class WorkorderManhourArray implements Serializable {

    public WorkorderManhourArray() {

    }

    public WorkorderManhourArray(Manhour[][] workorderManhourArray) {
        this.workorderManhourArray = workorderManhourArray;
    }

    /*
     * 工单-工时2维数组
     * 考虑工时填报的特殊性
     */
    private Manhour[][] workorderManhourArray;

    public Manhour[][] getWorkorderManhourArray() {
        return workorderManhourArray;
    }

    public void setWorkorderManhourArray(Manhour[][] workorderManhourArray) {
        this.workorderManhourArray = workorderManhourArray;
    }
}
