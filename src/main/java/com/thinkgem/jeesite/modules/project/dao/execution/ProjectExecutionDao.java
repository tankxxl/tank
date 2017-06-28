/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.execution;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;

/**
 * 项目合同执行DAO接口
 * @author jicdata
 * @version 2016-03-08
 */
@MyBatisDao
public interface ProjectExecutionDao extends JicDao<ProjectExecution> {
    /**
     * 根据执行合同号查找合同执行信息
     *
     * @param executionContractNo 执行合同号=合同号，数据库中要唯一
     * @return
     */
    public ProjectExecution findByExecutionContractNo(String executionContractNo);
}