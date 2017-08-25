/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.contract;

import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;

/**
 * 合同DAO接口
 *
 * dao只定义接口，不用去实现，这种对dao的实现方式称为mapper动态代理，
 * 前提：映射文件sql语句id与dao接口方法名一致。
 * mapper动态代理无需程序员实现dao接口，接口由MyBatis结合映射文件自动生成的动态代理实现的。
 *
 * mybatis：一级缓存、二级缓存
 * 缓存作用域是根据映射文件mapper的namespace划分的，相同namespace的mapper查询数据存放在同一个缓存区域。
 * 一级缓存是在同一线程(同一SqlSession)间共享数据，而二级缓存是在不同线程(不同的SqlSession)间共享数据。
 *
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
	
}