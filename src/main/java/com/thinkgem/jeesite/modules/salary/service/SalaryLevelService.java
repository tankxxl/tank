/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.salary.dao.SalaryLevelDao;
import com.thinkgem.jeesite.modules.salary.entity.SalaryLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 薪资等级Service
 * @author jicdata
 * @version 2016-02-19
 */
@Service
@Transactional(readOnly = true)
public class SalaryLevelService extends CrudService<SalaryLevelDao, SalaryLevel> {

	public SalaryLevel get(String id) {
		return super.get(id);
	}
	
	public List<SalaryLevel> findList(SalaryLevel salaryLevel) {
		return super.findList(salaryLevel);
	}
	
	public Page<SalaryLevel> findPage(Page<SalaryLevel> page, SalaryLevel salaryLevel) {
		return super.findPage(page, salaryLevel);
	}
	
	@Transactional(readOnly = false)
	public void save(SalaryLevel salaryLevel) {
		super.save(salaryLevel);
	}
	
	@Transactional(readOnly = false)
	public void delete(SalaryLevel salaryLevel) {
		super.delete(salaryLevel);
	}
	
}