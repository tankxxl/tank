/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.dao.external;

import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;

/**
 * 外部立项申请DAO接口
 * @author jicdata
 * @version 2016-02-23
 */
@MyBatisDao
public interface ProjectApplyExternalDao extends JicDao<ProjectApplyExternal> {
	
	/**
	 * 获取当前数据库中项目编号
	 * @return
	 */
	public String getCurrentCode();
	
	/**
	 * 更新项目编码
	 */
	public void updatePorCode(String code);
	
	// public List<ProjectApplyExternal> findList4LargerMainStage(ProjectApplyExternal projectApplyExternal);

	// public List<ProjectApplyExternal> findAllList4LargerMainStage(ProjectApplyExternal projectApplyExternal);

}