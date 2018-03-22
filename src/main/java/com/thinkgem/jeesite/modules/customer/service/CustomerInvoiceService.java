/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.service;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.customer.dao.CustomerInvoiceDao;
import com.thinkgem.jeesite.modules.customer.entity.CustomerInvoice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 开票客户信息Service
 * @author jicdata
 * @version 2016-02-21
 */
@Service
@Transactional(readOnly = true)
public class CustomerInvoiceService extends CrudService<CustomerInvoiceDao, CustomerInvoice> {

	@Override
	public CustomerInvoice get(String id) {
		CustomerInvoice customerInvoice = super.get(id);
		// In case id is not customerId.
		if (customerInvoice == null)
			return customerInvoice;

		// customer.setCustomerContactList(customerContactDao.findList(new CustomerContact(customer)));
		return customerInvoice;
	}
}