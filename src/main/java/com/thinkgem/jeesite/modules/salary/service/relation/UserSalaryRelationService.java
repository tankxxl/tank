/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.service.relation;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.salary.dao.relation.UserSalaryRelationDao;
import com.thinkgem.jeesite.modules.salary.entity.relation.UserSalaryRelation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * 人员薪资管理Service
 * @author jicdata
 * @version 2016-03-12
 */
@Service
@Transactional(readOnly = true)
public class UserSalaryRelationService extends CrudService<UserSalaryRelationDao, UserSalaryRelation> {

	public UserSalaryRelation get(String id) {
		return super.get(id);
	}
	
	public List<UserSalaryRelation> findList(UserSalaryRelation userSalaryRelation) {
		return super.findList(userSalaryRelation);
	}
	
	public Page<UserSalaryRelation> findPage(Page<UserSalaryRelation> page, UserSalaryRelation userSalaryRelation) {
		return super.findPage(page, userSalaryRelation);
	}
	
	/**
	 * 
	 * @param userSalaryRelation
	 * @param hasSalary 用于判断 是插入，还是更新。。若hasSalary=ture 表示，用户已经设过薪资了，现在只是更新
	 */
	@Transactional(readOnly = false)
	public void save(UserSalaryRelation userSalaryRelation,boolean hasSalary) {
		if(hasSalary){
			userSalaryRelation.preUpdate();
			dao.update(userSalaryRelation);
		}else{
			userSalaryRelation.preInsert();
			dao.insert(userSalaryRelation);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(UserSalaryRelation userSalaryRelation) {
		super.delete(userSalaryRelation);
	}

	/**
	 * 根据参数信息获取用户薪资级别信息；返回单个对象
	 * @author Arthur
	 * @param paramMap
	 * @return
	 */
	public UserSalaryRelation getByParam(HashMap paramMap) {
		List<UserSalaryRelation> list = dao.getByParam(paramMap);
		if (1 != list.size()) {
			return null;
		}
		return list.get(0);
	}
}