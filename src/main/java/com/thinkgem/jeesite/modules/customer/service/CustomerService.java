/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.customer.dao.CustomerContactDao;
import com.thinkgem.jeesite.modules.customer.dao.CustomerDao;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.entity.CustomerContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户联系人信息Service
 * @author jicdata
 * @version 2016-02-21
 */
@Service
@Transactional(readOnly = true)
public class CustomerService extends CrudService<CustomerDao, Customer> {

	@Autowired
	private CustomerContactDao customerContactDao;
	
	
	public CustomerContact getCustomerConcat(String customerConcatId){
		return customerContactDao.get(customerConcatId);
	}
	
	
	@Override
	public Customer get(String id) {
		Customer customer = super.get(id);

		// In case id is not customerId.
		if (customer == null)
			return customer;

		customer.setCustomerContactList(customerContactDao.findList(new CustomerContact(customer)));
		return customer;
	}
	
	@Override
	public List<Customer> findList(Customer customer) {
		return super.findList(customer);
	}
	
	public List<CustomerContact> findContatList(Customer customer) {
		return customerContactDao.findList(new CustomerContact(customer));
	}
	
	@Override
	public Page<Customer> findPage(Page<Customer> page, Customer customer) {
		return super.findPage(page, customer);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(Customer customer) {
		
		super.save(customer);
		for (CustomerContact customerContact : customer.getCustomerContactList()){
			if (customerContact.getId() == null){
				continue;
			}
			if (CustomerContact.DEL_FLAG_NORMAL.equals(customerContact.getDelFlag())){
				if (StringUtils.isBlank(customerContact.getId())){
					customerContact.setCustomer(customer);
					customerContact.preInsert();
					customerContactDao.insert(customerContact);
				}else{
					customerContact.preUpdate();
					customerContactDao.update(customerContact);
				}
			}else{
				customerContactDao.delete(customerContact);
			}
		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(Customer customer) {
		super.delete(customer);
		customerContactDao.delete(new CustomerContact(customer));
	}
	
	public Customer getByName(String customerName) {
		Customer customer = new Customer();
		customer.setCustomerName(customerName);
		customer = dao.getByName(customer);
		if (customer == null){
			return null;
		}
		return customer;
	}
	
}