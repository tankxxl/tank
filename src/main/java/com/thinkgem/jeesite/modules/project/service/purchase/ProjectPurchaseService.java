
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

    @Override
    public ProjectPurchase get(String id) {
        ProjectPurchase purchase = super.get(id);
        // in case param id is not purchase's id.
        if (purchase == null)
            return new ProjectPurchase();
//        purchase.setExecutionItemList(itemDao.findList(new ProjectPurchaseItem(purchase)));
        return purchase;
    }

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
        } else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
            // 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
            setupVariable(purchase, vars);
        } else if ("".equalsIgnoreCase(taskDefKey)) {
        }

    }

    public void fillApply(ProjectPurchase purchase) {
        ProjectApplyExternal apply = applyService.get(purchase.getApply().getId());
        purchase.setApply(apply);
    }
}