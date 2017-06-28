/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.dao.manhour;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.statistic.entity.manhour.DeptManhour4pro;
import com.thinkgem.jeesite.modules.statistic.entity.manhour.StatisticManhour;

import java.util.List;
import java.util.Map;

/**
 * 工时统计DAO接口
 * @author jicdata
 * @version 2016-03-15
 */
@MyBatisDao
public interface StatisticManhourDao extends CrudDao<StatisticManhour> {
	
	public List<StatisticManhour> insetTempStatisticManhourTable(StatisticManhour statisticManhour);
	public List<DeptManhour4pro> findList4DeptManhour4pro(DeptManhour4pro deptManhour4pro);
	public void createTempStatisticManHour();
	public Integer findCount4First(Map<String,Object> obj);
	public List<StatisticManhour> findList4First(Map<String,Object> obj);
}