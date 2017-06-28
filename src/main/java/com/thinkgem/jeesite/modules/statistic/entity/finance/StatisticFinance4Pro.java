/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.entity.finance;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;

/**
 * 财务统计Entity(2级页面对应)
 * 
 * @author jicdata
 * @version 2016-03-17
 */
public class StatisticFinance4Pro extends DataEntity<StatisticFinanceItem4Pro> {

	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal pro;// 查询的项目

	public StatisticFinance4Pro() {
		super();
	}

	public ProjectApplyExternal getPro() {
		return pro;
	}

	public void setPro(ProjectApplyExternal pro) {
		this.pro = pro;
	}

}