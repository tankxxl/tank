
package com.thinkgem.jeesite.modules.project.service.purchase;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.dao.purchase.ProjectPurchaseDao;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
import com.thinkgem.jeesite.modules.project.entity.purchase.ProjectPurchase;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目合同采购Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class ProjectPurchaseService extends JicActService<ProjectPurchaseDao, ProjectPurchase> {

//    @Autowired
//    private ProjectPurchaseItemDao itemDao;

    @Autowired
    ActTaskService actTaskService;

    @Autowired
    MailService mailService;

    @Autowired
    private ProjectApplyExternalService applyService;

    private boolean isNewRecord;

    @Override
    public ProjectPurchase get(String id) {
//		return super.get(id);
        ProjectPurchase purchase = super.get(id);
        // in case param id is not purchase's id.
        if (purchase == null)
            return purchase;
//        purchase.setExecutionItemList(itemDao.findList(new ProjectPurchaseItem(purchase)));
        return purchase;
    }

    /**
     * 保存并结束流程
     * @param purchase
     */
    @Transactional(readOnly = false)
    public void saveFinishProcess(ProjectPurchase purchase) {
        // 保存
        save(purchase);
        // 开启流程
        String procInsId = launchWorkflow(purchase);
        // 结束流程
        endProcess(procInsId);
    }

    /**
     * 保存表单数据，并启动流程
     * @param purchase
     */
    @Transactional(readOnly = false)
    public void saveLaunch(ProjectPurchase purchase) {
        save(purchase);
        launchWorkflow(purchase);
    }

    /**
     * 保存表单数据，并启动流程
     * @param purchase
     */
    @Override
    @Transactional(readOnly = false)
    public void save(ProjectPurchase purchase) {
    //
    isNewRecord = purchase.getIsNewRecord();
    super.save(purchase);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(ProjectPurchase purchase) {
        super.delete(purchase);
//        itemDao.delete(new ProjectExecutionItem(purchase));
    }

    /**
     * 审批人审批入口
     *
     * @param purchase
     */
    @Transactional(readOnly = false)
    public void auditSave(ProjectPurchase purchase) {
        // 设置意见
        purchase.getAct().setComment((purchase.getAct().getFlagBoolean() ?
                "[同意] ":"[驳回] ") + purchase.getAct().getComment());
        Map<String, Object> vars = Maps.newHashMap();
        vars.put(ActUtils.VAR_PASS, purchase.getAct().getFlagNumber());

        String taskDefKey = purchase.getAct().getTaskDefKey();

        if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)){

            if ("03".equals(purchase.getApply().getCategory()) ) {
                vars.put("type", "2");
            } else {
                vars.put("type", "1");
            }
            // 项目类型
            vars.put(ActUtils.VAR_PRJ_TYPE, purchase.getApply().getCategory());
            // 都需要总经理审批
            vars.put(ActUtils.VAR_BOSS_AUDIT, "1");

        } else if ("".equals(taskDefKey)) {

        }
        // 提交流程任务
        saveAuditBase(purchase, vars);
    }

    private String launchWorkflow(ProjectPurchase purchase) {
        // 设置流程变量
        Map<String, Object> varMap = new HashMap<String, Object>();
        varMap.put(ActUtils.VAR_PRJ_ID, purchase.getApply().getId());
        varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_purchase);
        varMap.put(ActUtils.VAR_PRJ_TYPE, purchase.getApply().getCategory());
        varMap.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);

        varMap.put(ActUtils.VAR_SKIP_BOSS, MyDictUtils.isPurchaseAuditInt(purchase.getAmount()));
        varMap.put(ActUtils.VAR_SKIP_HR, MyDictUtils.isHrAuditInt(purchase.getContractType()));


        String title = purchase.getApply().getProjectName();

        return launchWorkflowBase(purchase, isNewRecord, title, varMap);
    }

}