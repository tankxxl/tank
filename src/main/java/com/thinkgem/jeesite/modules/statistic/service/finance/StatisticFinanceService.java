/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.service.finance;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.statistic.dao.finance.StatisticFinanceDao;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinance;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinance4Pro;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinanceItem4Pro;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 财务统计Service
 * @author jicdata
 * @version 2016-03-17
 */
@Service
@Transactional(readOnly = true)
public class StatisticFinanceService extends CrudService<StatisticFinanceDao, StatisticFinance> {

	public List<StatisticFinance> findList(StatisticFinance statisticFinance) {
		return super.findList(statisticFinance);
	}
	
	public Page<StatisticFinance> findPage(Page<StatisticFinance> page, StatisticFinance statisticFinance) {
		return super.findPage(page, statisticFinance);
	}
	
	/**
	 * 二级页面
	 * @param page
	 * @param statisticFinance4Pro
	 * @return
	 */
	public Page<StatisticFinanceItem4Pro> findFinanceItemPage4Pro(Page<StatisticFinanceItem4Pro> page, StatisticFinance4Pro statisticFinance4Pro) {
		statisticFinance4Pro.setPage(page);
		page.setList(dao.findFinanceItem4Pro(statisticFinance4Pro));
		return page;
	}
	
	
}