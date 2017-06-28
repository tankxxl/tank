/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.tech;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Need;

/**
 * 资源申请DAO接口
 * @author jicdata
 * @version 2016-03-11
 */
@MyBatisDao
public interface NeedDao extends CrudDao<Need> {
	
}