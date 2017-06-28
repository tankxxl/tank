package com.thinkgem.jeesite.modules.project.entity.tech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单-工时 2维数组封装类
 * Created by ArthurZhang on 2016/3/15.
 */
public class WorkorderManhourList implements Serializable {

    public WorkorderManhourList() {

    }

    public WorkorderManhourList(List<ManhourList> workorderManhourList) {
        this.workorderManhourList = workorderManhourList;
    }

    // 工单-工时2维数组
    private List<ManhourList> workorderManhourList = new ArrayList<ManhourList>();

    public List<ManhourList> getWorkorderManhourList() {
        return workorderManhourList;
    }

    public void setWorkorderManhourList(List<ManhourList> workorderManhourList) {
        this.workorderManhourList = workorderManhourList;
    }
}
