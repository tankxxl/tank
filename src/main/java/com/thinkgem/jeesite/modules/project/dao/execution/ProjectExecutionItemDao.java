/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.execution;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;

/**
 * 项目合同执行item DAO接口
 * @author jicdata
 * @version 2016-03-08
 */
@MyBatisDao
public interface ProjectExecutionItemDao extends CrudDao<ProjectExecutionItem> {
	
}