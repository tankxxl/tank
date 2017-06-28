/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.customer.entity.CustomerContact;

/**
 * 客户联系人信息DAO接口
 * @author jicdata
 * @version 2016-02-21
 */
@MyBatisDao
public interface CustomerContactDao extends CrudDao<CustomerContact> {
	
}