/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.customer.entity.CustomerInvoice;

/**
 * 开票客户信息DAO接口
 * @author jicdata
 * @version 2016-02-21
 */
@MyBatisDao
public interface CustomerInvoiceDao extends CrudDao<CustomerInvoice> {
	
	/**
	 * 根据客户名称查询客户
	 * @return
	 */
	public CustomerInvoice getByName(CustomerInvoice customerInvoice);
	
}