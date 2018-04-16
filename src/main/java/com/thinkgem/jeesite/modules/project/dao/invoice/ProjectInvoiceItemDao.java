/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.invoice;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;

import java.util.List;

/**
 * 项目合同执行DAO接口
 * @author jicdata
 * @version 2016-03-08
 */
@MyBatisDao
public interface ProjectInvoiceItemDao extends CrudDao<ProjectInvoiceItem> {

    /**
     * 批量删除数据
     * @param ids
     * @return
     */
    public int deleteByIds(String[] ids);

    /**
     * 按合同号查找发票项，查重使用
     * @param item
     * @return
     */
    public ProjectInvoiceItem findByContractCode(ProjectInvoiceItem item);


    /**
     * 按开票申请单查最新版本的所有开票项
     * @param item
     * @return
     */
    public List<ProjectInvoiceItem> findHeadList(ProjectInvoiceItem item);

    /**
     * 按合同号查询所有的开票版本
     * @param item
     * @return
     */
    public List<ProjectInvoiceItem> findVerList(ProjectInvoiceItem item);


}