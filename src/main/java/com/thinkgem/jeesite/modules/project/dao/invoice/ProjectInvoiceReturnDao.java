/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.invoice;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceReturn;

import java.util.List;

/**
 * 开票回款DAO接口
 * @author jicdata
 * @version 2016-03-08
 */
@MyBatisDao
public interface ProjectInvoiceReturnDao extends CrudDao<ProjectInvoiceReturn> {

    // 根据合同id查询已回款信息
    List<ProjectInvoiceReturn> findListByContractId(ProjectInvoice entity);
	
}