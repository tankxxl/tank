/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.invoice;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;

import java.util.List;

/**
 * 项目开票DAO接口
 * @author jicdata
 * @version 2016-03-08
 */
@MyBatisDao
public interface ProjectInvoiceDao extends JicDao<ProjectInvoice> {

    // 根据合同id查询已开票信息
    public List<ProjectInvoice> findListByContractId(ProjectInvoice entity);
	
}