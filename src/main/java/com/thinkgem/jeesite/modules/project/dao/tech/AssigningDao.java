/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.tech;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Assigning;

/**
 * 派工DAO接口
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@MyBatisDao
public interface AssigningDao extends CrudDao<Assigning> {
	
}