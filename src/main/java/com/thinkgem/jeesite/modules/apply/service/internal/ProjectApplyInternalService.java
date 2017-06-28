/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.service.internal;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.apply.dao.internal.ProjectApplyInternalDao;
import com.thinkgem.jeesite.modules.apply.entity.internal.ProjectApplyInternal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 内部立项申请Service
 * @author jicdata
 * @version 2016-02-23
 */
@Service
@Transactional(readOnly = true)
public class ProjectApplyInternalService extends CrudService<ProjectApplyInternalDao, ProjectApplyInternal> {

	public ProjectApplyInternal get(String id) {
		return super.get(id);
	}
	
	public List<ProjectApplyInternal> findList(ProjectApplyInternal projectApplyInternal) {
		return super.findList(projectApplyInternal);
	}
	
	public Page<ProjectApplyInternal> findPage(Page<ProjectApplyInternal> page, ProjectApplyInternal projectApplyInternal) {
		return super.findPage(page, projectApplyInternal);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectApplyInternal projectApplyInternal) {
		
		projectApplyInternal.preInsert4ProInteralApply();//判断是否是插入还是修改，若是插入那么添加当前用户为立项申请人
		super.save(projectApplyInternal);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectApplyInternal projectApplyInternal) {
		super.delete(projectApplyInternal);
	}
	
}