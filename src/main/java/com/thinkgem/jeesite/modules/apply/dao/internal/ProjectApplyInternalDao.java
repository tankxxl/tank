/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.dao.internal;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.apply.entity.internal.ProjectApplyInternal;

/**
 * 内部立项申请DAO接口
 * @author jicdata
 * @version 2016-02-23
 */
@MyBatisDao
public interface ProjectApplyInternalDao extends CrudDao<ProjectApplyInternal> {
	
}