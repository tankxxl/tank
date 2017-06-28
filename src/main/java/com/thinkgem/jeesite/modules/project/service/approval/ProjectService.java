/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.approval;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.project.dao.approval.ProjectDao;
import com.thinkgem.jeesite.modules.project.entity.approval.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 立项Service
 * @author jicdata
 * @version 2016-02-22
 */
@Service
@Transactional(readOnly = true)
public class ProjectService extends CrudService<ProjectDao, Project> {

	public Project get(String id) {
		return super.get(id);
	}
	
	public List<Project> findList(Project project) {
		return super.findList(project);
	}
	
	public Page<Project> findPage(Page<Project> page, Project project) {
		return super.findPage(page, project);
	}
	
	@Transactional(readOnly = false)
	public void save(Project project) {
		
		project.preInsert4Pro();//若是新建 添加当前用户为 销售：否则不添加
		
		super.save(project);
	}
	
	@Transactional(readOnly = false)
	public void delete(Project project) {
		super.delete(project);
	}
	
}