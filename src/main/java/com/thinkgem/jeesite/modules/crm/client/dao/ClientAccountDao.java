package com.thinkgem.jeesite.modules.crm.client.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientAccount;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientContact;

/**
 * 客户联系人信息DAO接口
 * @author jicdata
 * @version 2016-02-21
 */
@MyBatisDao
public interface ClientAccountDao extends CrudDao<ClientAccount> {
	
}