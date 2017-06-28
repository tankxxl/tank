/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.tech;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.project.dao.tech.WorkorderDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Workorder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 派工Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class WorkorderService extends CrudService<WorkorderDao, Workorder> {

	public Workorder get(String id) {
		return super.get(id);
	}
	
	public List<Workorder> findList(Workorder workorder) {
		return super.findList(workorder);
	}
	
	public Page<Workorder> findPage(Page<Workorder> page, Workorder workorder) {
		return super.findPage(page, workorder);
	}
	
	@Transactional(readOnly = false)
	public void save(Workorder workorder) {
		super.save(workorder);
	}
	
	@Transactional(readOnly = false)
	public void delete(Workorder workorder) {
		super.delete(workorder);
	}
	
}