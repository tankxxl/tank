/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.contract;

import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;

import java.util.List;

/**
 * 合同DAO接口
 * @author jicdata
 * @version 2016-03-09
 */
@MyBatisDao
public interface ProjectContractDao extends JicDao<ProjectContract> {
	
	/**
	 * 必须保证一个项目只出现在合同审批表中一次
	 * @param prjId
	 * @return
	 */
	public ProjectContract findContractByPrjId(String prjId);


	public ProjectContract getByCode(ProjectContract entity);

	public List<ProjectContract> findPreEndList(ProjectContract entity);

	/**
	 *
	 * @param entity
	 * @return
	 */
	public Long findPreEndCount(ProjectContract entity);


	/**
	 * 修改续签状态为已续签
	 * @param entity
	 * @return
	 */
	public int handled(ProjectContract entity);

	/**
	 * 查找 30 < x < 60 天 到期的合同
	 * @param entity
	 * @return
	 */
	public List<ProjectContract> findNotify1List(ProjectContract entity);

	/**
	 * 查找 0 < x < 30 天 到期的合同
	 * @param entity
	 * @return
	 */
	public List<ProjectContract> findNotify2List(ProjectContract entity);

	/**
	 * 查找 x < 0 天 已到期的合同
	 * @param entity
	 * @return
	 */
	public List<ProjectContract> findNotify3List(ProjectContract entity);



}