/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.supplier.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.supplier.dao.SupplierDao;
import com.thinkgem.jeesite.modules.supplier.entity.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 供应商Service
 * @author jicdata
 * @version 2016-02-25
 */
@Service
@Transactional(readOnly = true)
public class SupplierService extends CrudService<SupplierDao, Supplier> {

	public Supplier get(String id) {
		return super.get(id);
	}
	
	public List<Supplier> findList(Supplier supplier) {
		return super.findList(supplier);
	}
	
	public Page<Supplier> findPage(Page<Supplier> page, Supplier supplier) {
		return super.findPage(page, supplier);
	}
	
	@Transactional(readOnly = false)
	public void save(Supplier supplier) {
		super.save(supplier);
	}
	
	@Transactional(readOnly = false)
	public void delete(Supplier supplier) {
		super.delete(supplier);
	}
	
}