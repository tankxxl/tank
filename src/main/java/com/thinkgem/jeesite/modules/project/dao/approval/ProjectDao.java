/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.approval;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.approval.Project;

/**
 * 立项DAO接口
 * @author jicdata
 * @version 2016-02-22
 */
@MyBatisDao
public interface ProjectDao extends CrudDao<Project> {
	
}