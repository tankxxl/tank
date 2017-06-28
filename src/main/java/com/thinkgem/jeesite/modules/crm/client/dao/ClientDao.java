/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.client.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.crm.client.entity.Client;

/**
 * 客户联系人信息DAO接口
 * @author jicdata
 * @version 2016-02-21
 */
@MyBatisDao
public interface ClientDao extends CrudDao<Client> {
	
	/**
	 * 根据客户名称查询客户
	 * @param client
	 * @return
	 */
	public Client getByName(Client client);
	
}