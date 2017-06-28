/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.dao.finance;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinance;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinance4Pro;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinanceItem4Pro;

import java.util.List;

/**
 * 财务统计DAO接口
 * @author jicdata
 * @version 2016-03-17
 */
@MyBatisDao
public interface StatisticFinanceDao extends CrudDao<StatisticFinance> {
	/**
	 * 二级页面的sql
	 * @param statisticFinance4Pro 主要参数是。 项目id
	 * @return
	 */
	public  List<StatisticFinanceItem4Pro> findFinanceItem4Pro(StatisticFinance4Pro statisticFinance4Pro);
}