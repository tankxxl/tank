
package com.thinkgem.jeesite.modules.project.service.purchase;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.purchase.ProjectPurchaseDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
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

    // @Autowired
    // ActTaskService actTaskService;

    // @Autowired
    // MailService mailService;

     @Autowired
     private ProjectApplyExternalService applyService;

    // private boolean isNewRecord;

    @Override
    public ProjectPurchase get(String id) {
//		return super.get(id);
        ProjectPurchase purchase = super.get(id);
        // in case param id is not purchase's id.
        if (purchase == null)
            return null;
//        purchase.setExecutionItemList(itemDao.findList(new ProjectPurchaseItem(purchase)));
        return purchase;
    }

    /**
     * 保存并结束流程
     // * @param purchase
     */
    // @Transactional(readOnly = false)
    // public void saveFinishProcess(ProjectPurchase purchase) {
    //     // 保存
    //     // save(purchase);
    //     // 开启流程
    //     // String procInsId = launchWorkflow(purchase);
    //     String procInsId = saveLaunch(purchase);
    //     // 结束流程
    //     endProcess(procInsId);
    // }

    // 流程启动之前，设置map
    @Override
    public void setupVariable(ProjectPurchase purchase, Map<String, Object> vars) {
        fillApply(purchase);
        vars.put(ActUtils.VAR_PRJ_ID, purchase.getApply().getId());
        // varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_purchase);
        vars.put(ActUtils.VAR_PRJ_TYPE, purchase.getApply().getCategory());
        vars.put(ActUtils.VAR_TITLE, purchase.getApply().getProjectName());
        vars.put(ActUtils.VAR_SKIP_BOSS, MyDictUtils.isPurchaseAuditInt(purchase.getAmount()));
        vars.put(ActUtils.VAR_SKIP_HR, MyDictUtils.isHrAuditInt(purchase.getContractType()));

        // 设置合同金额
        // vars.put(ActUtils.VAR_AMOUNT, purchase.getAmount());

        if ("03".equals(purchase.getApply().getCategory()) ) {
            System.out.println("");
            // 分支上使用，没在节点上使用
            vars.put(ActUtils.VAR_TYPE, "2");
        } else {
            vars.put(ActUtils.VAR_TYPE, "1");
        }
    }


    // 审批过程中
    @Override
    public void processAudit(ProjectPurchase purchase, Map<String, Object> vars) {
        // 对不同环节的业务逻辑进行操作
        String taskDefKey = purchase.getAct().getTaskDefKey();
        if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)) {
            System.out.println();
        } else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
            // 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
//            fillApply(purchase);
//            myProcVariable(purchase, vars);
            setupVariable(purchase, vars);
        } else if ("".equalsIgnoreCase(taskDefKey)) {
        }

    }

    public void fillApply(ProjectPurchase purchase) {
        ProjectApplyExternal apply = applyService.get(purchase.getApply().getId());
        purchase.setApply(apply);
    }


    /**
     * 保存表单数据，并启动流程
     *
     * 申请人发起流程，申请人重新发起流程入口
     * 在form界面
     *
     * @param purchase
     */
    // @Transactional(readOnly = false)
    // public String saveLaunch(ProjectPurchase purchase) {
    //     if (purchase.getIsNewRecord()) {
    //         // 启动流程的时候，把业务数据放到流程变量里
    //         Map<String, Object> varMap = new HashMap<String, Object>();
    //         varMap.put(ActUtils.VAR_PRJ_ID, purchase.getApply().getId());
    //
    //         // varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_purchase);
    //         varMap.put(ActUtils.VAR_PRJ_TYPE, purchase.getApply().getCategory());
    //
    //         varMap.put(ActUtils.VAR_TITLE, purchase.getApply().getProjectName());
    //
    //         varMap.put(ActUtils.VAR_SKIP_BOSS, MyDictUtils.isPurchaseAuditInt(purchase.getAmount()));
    //         varMap.put(ActUtils.VAR_SKIP_HR, MyDictUtils.isHrAuditInt(purchase.getContractType()));
    //
    //         // 在父类中保存
    //         // save(purchase);
    //         return launch(purchase, varMap);
    //     } else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
    //         // 只要不是启动流程，其它任意节点的跳转都当成节点审批
    //         saveAudit(purchase);
    //         return null;
    //     }
    // }

    // /**
    //  * 保存表单数据，并启动流程
    //  * @param purchase
    //  */
    // @Override
    // @Transactional(readOnly = false)
    // public void save(ProjectPurchase purchase) {
    // //
    // isNewRecord = purchase.getIsNewRecord();
    // super.save(purchase);
    // }

//     @Override
//     @Transactional(readOnly = false)
//     public void delete(ProjectPurchase purchase) {
//         super.delete(purchase);
// //        itemDao.delete(new ProjectExecutionItem(purchase));
//     }

    // /**
    //  * 审批人审批入口
    //  *
    //  * @param purchase
    //  */
    // @Transactional(readOnly = false)
    // public void auditSave(ProjectPurchase purchase) {
    //     // 设置意见
    //     purchase.getAct().setComment((purchase.getAct().getFlagBoolean() ?
    //             "[同意] ":"[驳回] ") + purchase.getAct().getComment());
    //     Map<String, Object> vars = Maps.newHashMap();
    //     vars.put(ActUtils.VAR_PASS, purchase.getAct().getFlagNumber());
    //
    //     String taskDefKey = purchase.getAct().getTaskDefKey();
    //
    //     if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)){
    //
    //         if ("03".equals(purchase.getApply().getCategory()) ) {
    //             vars.put("type", "2");
    //         } else {
    //             vars.put("type", "1");
    //         }
    //         // 项目类型
    //         vars.put(ActUtils.VAR_PRJ_TYPE, purchase.getApply().getCategory());
    //         // 都需要总经理审批
    //         vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
    //
    //     } else if ("".equals(taskDefKey)) {
    //
    //     }
    //     // 提交流程任务
    //     saveAuditBase(purchase, vars);
    // }

    // private String launchWorkflow(ProjectPurchase purchase) {
    //     // 设置流程变量
    //     Map<String, Object> varMap = new HashMap<String, Object>();
    //     varMap.put(ActUtils.VAR_PRJ_ID, purchase.getApply().getId());
    //     varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_purchase);
    //     varMap.put(ActUtils.VAR_PRJ_TYPE, purchase.getApply().getCategory());
    //     varMap.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
    //
    //     varMap.put(ActUtils.VAR_SKIP_BOSS, MyDictUtils.isPurchaseAuditInt(purchase.getAmount()));
    //     varMap.put(ActUtils.VAR_SKIP_HR, MyDictUtils.isHrAuditInt(purchase.getContractType()));
    //
    //
    //     String title = purchase.getApply().getProjectName();
    //
    //     return launchWorkflowBase(purchase, isNewRecord, title, varMap);
    // }
}