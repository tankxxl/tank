/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.finish;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.finish.ProjectFinishApproval;

/**
 * 结项DAO接口
 * @author jicdata
 * @version 2016-03-11
 */
@MyBatisDao
public interface ProjectFinishApprovalDao extends JicDao<ProjectFinishApproval> {
	
}