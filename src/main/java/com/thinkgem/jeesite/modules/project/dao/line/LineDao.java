package com.thinkgem.jeesite.modules.project.dao.line;

import com.thinkgem.jeesite.common.persistence.TreeDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.line.Line;

/**
 * 条线DAO接口
 * @author rgz
 * @version 2018-04-10
 */
@MyBatisDao
public interface LineDao extends TreeDao<Line> {
	
}
