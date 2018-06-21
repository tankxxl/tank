/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.invoice;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceItemDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceReturnDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceReturn;
import com.thinkgem.jeesite.modules.project.entity.purchase.ProjectPurchase;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目开票Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class ProjectInvoiceService extends JicActService<ProjectInvoiceDao, ProjectInvoice> {

	@Autowired
    ProjectInvoiceItemDao itemDao;

	@Autowired
    ProjectInvoiceReturnDao returnDao;
	
	@Autowired
    private ProjectApplyExternalService applyService;

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

    // 流程启动之前，设置map
    @Override
    public void setupVariable(ProjectInvoice projectInvoice, Map<String, Object> vars) {
        fillApply(projectInvoice);

        vars.put(ActUtils.VAR_PRJ_ID, projectInvoice.getApply().getId());
        vars.put(ActUtils.VAR_PRJ_TYPE, projectInvoice.getApply().getCategory());
        vars.put(ActUtils.VAR_TITLE, projectInvoice.getApply().getProjectName());

        if ("03".equals(projectInvoice.getApply().getCategory()) ) {
            System.out.println("");
            // 分支上使用，没在节点上使用
            vars.put(ActUtils.VAR_TYPE, "2");
        } else {
            vars.put(ActUtils.VAR_TYPE, "1");
        }
    }

    // 审批过程中
    @Override
    public void processAudit(ProjectInvoice projectInvoice, Map<String, Object> vars) {
        // 对不同环节的业务逻辑进行操作
        String taskDefKey = projectInvoice.getAct().getTaskDefKey();
        if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)) {
        } else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
            // 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
            setupVariable(projectInvoice, vars);
        } else if ("".equalsIgnoreCase(taskDefKey)) {
        }

    }

    public void fillApply(ProjectInvoice projectInvoice) {
        ProjectApplyExternal apply = applyService.get(projectInvoice.getApply().getId());
        projectInvoice.setApply(apply);
    }

    /**
     * 保存表单数据
     * @param projectInvoice
     */
    @Override
    @Transactional(readOnly = false)
    public void save(ProjectInvoice projectInvoice) {
        super.save(projectInvoice);
        saveItem(projectInvoice);
    }

    private void saveItem(ProjectInvoice projectInvoice) {
        for (ProjectInvoiceItem item : projectInvoice.getInvoiceItemList()){

//            if (StringUtils.isBlank(item.getId())) {
//                continue;
//            }

            if (item.getId() == null){
                continue;
            }

            if (ProjectExecutionItem.DEL_FLAG_NORMAL.equals(item.getDelFlag())){
                if (StringUtils.isBlank(item.getId())){
                    item.setInvoice(projectInvoice);
                    item.preInsert();
                    itemDao.insert(item);
                }else{
                    item.preUpdate();
                    itemDao.update(item);
                }
            }else{
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