/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.dao.relation;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.salary.entity.relation.UserSalaryRelation;

import java.util.HashMap;
import java.util.List;

/**
 * 人员薪资管理DAO接口
 * @author jicdata
 * @version 2016-03-12
 */
@MyBatisDao
public interface UserSalaryRelationDao extends CrudDao<UserSalaryRelation> {

    /**
     * 根据参数查询指定关联对象
     * @author Arthur
     * @param paramMap
     * @return
     */
    List<UserSalaryRelation> getByParam(HashMap paramMap);
}