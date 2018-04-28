/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.invoice;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceItemDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceReturnDao;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 项目开票Service
 * @author jicdata
 * @version 2016-03-08
 *
 */
@Service
@Transactional(readOnly = true)
public class ProjectInvoiceService extends JicActService<ProjectInvoiceDao, ProjectInvoice> {

	@Autowired
    ProjectInvoiceItemDao itemDao;

	@Autowired
    ProjectInvoiceReturnDao returnDao;

    @Override
    public ProjectInvoice get(String id) {
        ProjectInvoice invoice = super.get(id);
        if (invoice == null)
            return new ProjectInvoice();

        invoice.setInvoiceItemList(itemDao.findList(new ProjectInvoiceItem(invoice)));
        return invoice;
    }

    public List<ProjectInvoice> findListByContractId(ProjectInvoice projectInvoice) {
        return dao.findListByContractId(projectInvoice);
    }

    public List<ProjectInvoiceReturn> findReturnByContractId(ProjectInvoice projectInvoice) {
        return returnDao.findListByContractId(projectInvoice);
    }

    @Override
    public void setupVariable(ProjectInvoice projectInvoice, Map<String, Object> vars) {
        vars.put(ActUtils.VAR_TITLE, projectInvoice.getRemarks() );
    }

    @Override
    public void processAudit(ProjectInvoice projectInvoice, Map<String, Object> vars) {
        String taskDefKey = projectInvoice.getAct().getTaskDefKey();
        if ( UserTaskType.UT_SPECIALIST.equals(taskDefKey) ) {
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void save(ProjectInvoice projectInvoice) {
        super.save(projectInvoice);
        saveItems(projectInvoice);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(ProjectInvoice projectInvoice) {
        super.delete(projectInvoice);
        itemDao.delete(new ProjectInvoiceItem(projectInvoice));
    }

    // 保存申请单中所有的开票
    private void saveItems(ProjectInvoice projectInvoice) {

        for (ProjectInvoiceItem item : projectInvoice.getInvoiceItemList()) {

//            if (StringUtils.isBlank(item.getId())) {
//                continue;
//            }

            // if (item.getId() == null){
            //     continue;
            // }

            if (ProjectInvoiceItem.DEL_FLAG_NORMAL.equals(item.getDelFlag())) {
                if (StringUtils.isBlank(item.getId())) {
                    item.setInvoice(projectInvoice);
                    item.preInsert();
                    itemDao.insert(item);
                } else {
                    System.out.println();
                    item.preUpdate();
                    itemDao.update(item);
                }
            } else {
                itemDao.delete(item);
            }
        }

        // 另一子表
        for (ProjectInvoiceReturn item : projectInvoice.getReturnList()){

//            if (StringUtils.isBlank(item.getId())) {
//                continue;
//            }

            if (item.getId() == null){
                continue;
            }

            if (ProjectInvoiceReturn.DEL_FLAG_NORMAL.equals(item.getDelFlag())){
                if (StringUtils.isBlank(item.getId())){
                    item.setInvoice(projectInvoice);
                    item.preInsert();
                    returnDao.insert(item);
                }else{
                    item.preUpdate();
                    returnDao.update(item);
                }
            }else{
                returnDao.delete(item);
            }
        }
    }

}