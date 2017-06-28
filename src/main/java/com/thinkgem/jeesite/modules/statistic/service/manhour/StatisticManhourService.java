/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.service.manhour;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.statistic.dao.manhour.StatisticManhourDao;
import com.thinkgem.jeesite.modules.statistic.entity.manhour.DeptManhour4pro;
import com.thinkgem.jeesite.modules.statistic.entity.manhour.StatisticManhour;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工时统计Service
 * @author jicdata
 * @version 2016-03-15
 */
@Service
@Transactional(readOnly = true)
public class StatisticManhourService extends CrudService<StatisticManhourDao, StatisticManhour> {

	
//	public StatisticManhour get(String id) {
//		return super.get(id);
//	}
	
//	public List<StatisticManhour> findList(StatisticManhour statisticManhour) {
//		return super.findList(statisticManhour);
//	}
	
	
	@Transactional(readOnly = false)
	public List<StatisticManhour> findList(StatisticManhour statisticManhour) {
		/**
		 * 在查询之前，先像 工时统计临时表中插入记录(调用了存储过程)
		 */
		dao.insetTempStatisticManhourTable(statisticManhour);
		return dao.findList(statisticManhour);
	}
	@Transactional(readOnly = false)
	public Page<StatisticManhour> findPage2(Page<StatisticManhour> page, StatisticManhour statisticManhour) {
		
		List<StatisticManhour> findList2 = null;
		
		/**
		 * 在查询之前，先像 工时统计临时表中插入记录(调用了存储过程)
		 */
		dao.insetTempStatisticManhourTable(statisticManhour);
//		List<StatisticManhour> findList = dao.findList(statisticManhour);
//		System.out.println(findList);
		
		/**
		 * 设置页码
		 */
		statisticManhour.setPage(page);
		
		try{
			findList2 = dao.findList(statisticManhour);
			System.out.println(findList2);
		}catch(Exception e){
			/** TODO :当一个connect第一次执行的时候，若不被page拦截器拦截（即参数bean没有设置page对象）之前执行不会出现 找不到临时表
			 * 		现在情况是：connect第一次使用的时候、且bean对象设置了page属性。那么执行findlist会被 拦截器拦截。 出现找不到 临时表。。。SQLHelp类抛出异常
			 * 	临时解决方案： 若findList抛出异常。说明 没找到临时表。那么  手动查询sql，查找list与查询个数
			*/
			Map<String,Object> paremMap = new HashMap<String, Object>();
			paremMap.put("dsf", statisticManhour.getSqlMap().get("dsf"));
			page.setCount(dao.findCount4First(paremMap));
			
			paremMap.put("start",(page.getPageNo()-1)*page.getPageSize() );
			paremMap.put("pageSize", page.getPageSize());
			findList2 =dao.findList4First(paremMap);
		}
		
		page.setList(findList2);
		return page;
	}
	
	@Transactional(readOnly = false)
	public Page<DeptManhour4pro> findPage4DeptManhour4pro(Page<DeptManhour4pro> page, DeptManhour4pro deptManhour4pro) {
		deptManhour4pro.setPage(page);
		page.setList(dao.findList4DeptManhour4pro(deptManhour4pro));
		return page;
	}
	
	public List<DeptManhour4pro> findList4DeptManhour4pro(DeptManhour4pro deptManhour4pro) {
		return dao.findList4DeptManhour4pro(deptManhour4pro);
	}
	
//	public Page<StatisticManhour> findPage(Page<StatisticManhour> page, StatisticManhour statisticManhour) {
//		return super.findPage(page, statisticManhour);
//	}
//	
//	@Transactional(readOnly = false)
//	public void save(StatisticManhour statisticManhour) {
//		super.save(statisticManhour);
//	}
//	
//	@Transactional(readOnly = false)
//	public void delete(StatisticManhour statisticManhour) {
//		super.delete(statisticManhour);
//	}
	
	
}