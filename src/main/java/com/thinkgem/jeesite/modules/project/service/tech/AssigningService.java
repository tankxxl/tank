/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.tech;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.project.dao.tech.AssigningDao;
import com.thinkgem.jeesite.modules.project.dao.tech.TechapplyDao;
import com.thinkgem.jeesite.modules.project.dao.tech.WorkorderDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Assigning;
import com.thinkgem.jeesite.modules.project.entity.tech.Techapply;
import com.thinkgem.jeesite.modules.project.entity.tech.Workorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 派工Service
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class AssigningService extends CrudService<AssigningDao, Assigning> {

	@Autowired
	private WorkorderDao workorderDao;
	
	@Autowired
	private TechapplyDao techapplyDao;

	public Assigning get(String id) {
		Assigning assigning = super.get(id);
		assigning.setWorkorderList(workorderDao.findList(new Workorder(assigning)));
		return assigning;
	}
	
	public List<Assigning> findList(Assigning assigning) {
		return super.findList(assigning);
	}
	
	public Page<Assigning> findPage(Page<Assigning> page, Assigning assigning) {
		return super.findPage(page, assigning);
	}
	
	@Transactional(readOnly = false)
	public void save(Assigning assigning) {
		super.save(assigning);
		/**
		 * 下面3行 设置申请单的派工标记为已经派工
		 */
		Techapply techapply = assigning.getTechapply();
		techapply.setAssignFlag(Techapply.ASSIGN_FLAG_4_IS_ASSIGNED);
		techapplyDao.update4AssignFlag(techapply);
		
		for (Workorder workorder : assigning.getWorkorderList()){
			if (workorder.getId() == null){
				continue;
			}
			if (Workorder.DEL_FLAG_NORMAL.equals(workorder.getDelFlag())){
				if (StringUtils.isBlank(workorder.getId())){
					workorder.setAssigning(assigning);
					workorder.preInsert();
					workorderDao.insert(workorder);
				}else{
					workorder.preUpdate();
					workorderDao.update(workorder);
				}
			}else{
				workorderDao.delete(workorder);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(Assigning assigning) {
		super.delete(assigning);
		workorderDao.delete(new Workorder(assigning));
	}
}