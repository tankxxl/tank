/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thinkgem.jeesite.modules.act.entity.Act;

import java.io.Serializable;
import java.util.List;

/**
 * Activiti Entity类
 * @author ThinkGem
 * @version 2013-05-28
 */
public abstract class ActEntity<T> extends DataEntity<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Act act; 		// 流程任务对象

    // rgz
    protected List<Act> acts; // 用于保存流程全部的审批节点信息，目前只保存审批通过的节点，用于打印审批单
    public List<Act> getActs() {
        return acts;
    }

    public void setActs(List<Act> acts) {
        this.acts = acts;
    }


    protected String procStatus;		// 流程审批状态
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    // end

	public ActEntity() {
		super();
	}
	
	public ActEntity(String id) {
		super(id);
	}
	
	@JsonIgnore
	public Act getAct() {
		if (act == null){
			act = new Act();
		}
		return act;
	}

	public void setAct(Act act) {
		this.act = act;
	}

	public boolean hasAct() {
    	if (act == null) {
    		return false;
		} else {
    		return true;
		}
	}

	/**
	 * 获取流程实例ID
	 * @return
	 */
	public String getProcInsId() {
		return this.getAct().getProcInsId();
	}

	/**
	 * 设置流程实例ID
	 * @param procInsId
	 */
	public void setProcInsId(String procInsId) {
		this.getAct().setProcInsId(procInsId);
	}
}
