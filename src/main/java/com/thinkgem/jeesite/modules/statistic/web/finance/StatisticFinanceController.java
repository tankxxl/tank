/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.web.finance;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinance;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinance4Pro;
import com.thinkgem.jeesite.modules.statistic.entity.finance.StatisticFinanceItem4Pro;
import com.thinkgem.jeesite.modules.statistic.service.finance.StatisticFinanceService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 财务统计Controller
 * @author jicdata
 * @version 2016-03-17
 */
@Controller
@RequestMapping(value = "${adminPath}/statistic/finance/statisticFinance")
public class StatisticFinanceController extends BaseController {

	@Autowired
	private StatisticFinanceService statisticFinanceService;
	
	
	/**
	 * 对应财务统计一级页面
	 */
	@RequiresPermissions("statistic:finance:statisticFinance:view")
	@RequestMapping(value = {"list", ""})
	public String list(StatisticFinance statisticFinance, HttpServletRequest request, HttpServletResponse response, Model model) {
		statisticFinance.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));//业务数据范围过滤，根据的关联项目表（销售、对应u4）（销售部门 对应 s5）
		Page<StatisticFinance> page = statisticFinanceService.findPage(new Page<StatisticFinance>(request, response), statisticFinance); 
		model.addAttribute("page", page);
		return "modules/statistic/finance/statisticFinanceList";
	}
	/**
	 * 查询的是 以 申请编号、技术人员编号 分组  sql:group by ta.id,tm.engineer_id
	 * 对应财务统计二级页面
	 */
	@RequiresPermissions("statistic:finance:statisticFinance:view")
	@RequestMapping(value = "financeList4pro")
	public String financeList4pro(StatisticFinance4Pro statisticFinance4Pro, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StatisticFinanceItem4Pro> page = statisticFinanceService.findFinanceItemPage4Pro(new Page<StatisticFinanceItem4Pro>(request, response), statisticFinance4Pro); 
		model.addAttribute("page", page);
		return "modules/statistic/finance/statisticFinanceItemList4Pro";
	}

}