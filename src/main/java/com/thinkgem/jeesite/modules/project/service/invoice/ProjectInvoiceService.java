/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.invoice;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceItemDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceReturnDao;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceReturn;
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

	// @Autowired
	// ActTaskService actTaskService;

	@Autowired
    ProjectInvoiceItemDao itemDao;

	@Autowired
    ProjectInvoiceReturnDao returnDao;
	
	// @Autowired
	// MailService mailService;
	
	// @Autowired
	// private ProjectApplyExternalService projectApplyExternalService;

    private boolean isNewRecord;


    @Override
    public ProjectInvoice get(String id) {
        // return super.get(id);
        ProjectInvoice invoice = super.get(id);
        // in case param id is not invoice's id.
        if (invoice == null)
            return invoice;

        invoice.setInvoiceItemList(itemDao.findList(new ProjectInvoiceItem(invoice)));
        return invoice;
    }

    public List<ProjectInvoice> findListByContractId(ProjectInvoice projectInvoice) {
        return dao.findListByContractId(projectInvoice);
    }

    public List<ProjectInvoiceReturn> findReturnByContractId(ProjectInvoice projectInvoice) {
        return returnDao.findListByContractId(projectInvoice);
    }

    /**
	 * 保存并结束流程
	 * @param projectInvoice
	 */
	@Transactional(readOnly = false)
	public void saveFinishProcess(ProjectInvoice projectInvoice) {
        // 保存
        // save(projectInvoice);
        // 开启流程
        // String procInsId = launchWorkflow(projectInvoice);
        String procInsId = saveLaunch(projectInvoice);
        // 结束流程
        endProcess(procInsId);
	}

    /**
     * 保存表单数据，并启动流程
     *
     * 申请人发起流程，申请人重新发起流程入口
     * 在form界面
     *
     * @param projectInvoice
     */
    @Transactional(readOnly = false)
    public String saveLaunch(ProjectInvoice projectInvoice) {
        if (projectInvoice.getIsNewRecord()) {
            // 启动流程的时候，把业务数据放到流程变量里
            Map<String, Object> varMap = new HashMap<String, Object>();
            varMap.put(ActUtils.VAR_PRJ_ID, projectInvoice.getApply().getId());

            // varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_invoice);
            varMap.put(ActUtils.VAR_PRJ_TYPE, projectInvoice.getApply().getCategory());

            varMap.put(ActUtils.VAR_TITLE, projectInvoice.getApply().getProjectName());

            return launch(projectInvoice, varMap);
        } else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
            // 只要不是启动流程，其它任意节点的跳转都当成节点审批
            saveAudit(projectInvoice);
            return null;
        }
    }

    /**
     * 保存表单数据
     * @param projectInvoice
     */
    @Override
    @Transactional(readOnly = false)
    public void save(ProjectInvoice projectInvoice) {
        // isNewRecord = projectInvoice.getIsNewRecord();
        super.save(projectInvoice);
        saveItem(projectInvoice);
    }

    // /**
    //  * 审批人审批入口
    //  * @param projectInvoice
    //  */
    // @Transactional(readOnly = false)
    // public void auditSave(ProjectInvoice projectInvoice) {
    //     // 设置意见
    //     projectInvoice.getAct().setComment((projectInvoice.getAct().getFlagBoolean() ?
    //             "[同意] ":"[驳回] ") + projectInvoice.getAct().getComment());
    //     Map<String, Object> vars = Maps.newHashMap();
    //     vars.put(ActUtils.VAR_PASS, projectInvoice.getAct().getFlagNumber());
    //
    //     // 对不同环节的业务逻辑进行操作
    //     String taskDefKey = projectInvoice.getAct().getTaskDefKey();
    //
    //     if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)){
    //
    //         if ("03".equals(projectInvoice.getApply().getCategory()) ) {
    //             vars.put("type", "2");
    //         } else {
    //             vars.put("type", "1");
    //         }
    //         // 项目类型
    //         vars.put(ActUtils.VAR_PRJ_TYPE, projectInvoice.getApply().getCategory());
    //         // 都需要总经理审批
    //         vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
    //
    //     } else if ("".equals(taskDefKey)) {
    //
    //     }
    //     // 提交流程任务
    //     saveAuditBase(projectInvoice, vars);
    // }


    // private String launchWorkflow(ProjectInvoice projectInvoice) {
    //     // 设置流程变量
    //     Map<String, Object> varMap = new HashMap<String, Object>();
    //     varMap.put(ActUtils.VAR_PRJ_ID, projectInvoice.getApply().getId());
    //
    //     varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_invoice);
    //     varMap.put(ActUtils.VAR_PRJ_TYPE, projectInvoice.getApply().getCategory());
    //     varMap.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
    //
    //     varMap.put(ActUtils.VAR_SKIP_inout, 0); // 1为skip
    //
    //     String title = projectInvoice.getApply().getProjectName();
    //
    //     return launchWorkflowBase(projectInvoice, isNewRecord, title, varMap);
    // }

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