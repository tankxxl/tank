/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.bidding;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;

import java.util.List;

/**
 * 项目投标DAO接口
 * @author jicdata
 * @version 2016-03-08
 */
@MyBatisDao
public interface ProjectBiddingDao extends CrudDao<ProjectBidding> {

    public ProjectBidding findByProcInsId(String procInsId);
	
}