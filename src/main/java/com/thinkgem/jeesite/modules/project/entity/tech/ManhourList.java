package com.thinkgem.jeesite.modules.project.entity.tech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工时Array封装类(按周计算)
 * Created by ArthurZhang on 2016/3/15.
 */
public class ManhourList implements Serializable {

    public ManhourList() {

    }

    public ManhourList(List<Manhour> manhourList) {
        this.manhourList = manhourList;
    }

    // 一周工时
    private List<Manhour> manhourList = new ArrayList<Manhour>();

    public List<Manhour> getManhourList() {
        return manhourList;
    }

    public void setManhourList(List<Manhour> manhourList) {
        this.manhourList = manhourList;
    }
}
