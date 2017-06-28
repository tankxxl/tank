/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.salary.entity.SalaryLevel;

/**
 * 薪资等级DAO接口
 * @author jicdata
 * @version 2016-02-19
 */
@MyBatisDao
public interface SalaryLevelDao extends CrudDao<SalaryLevel> {
	
}