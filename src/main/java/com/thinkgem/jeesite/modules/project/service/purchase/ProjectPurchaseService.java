
package com.thinkgem.jeesite.modules.project.service.purchase;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.project.dao.purchase.ProjectPurchaseDao;
import com.thinkgem.jeesite.modules.project.entity.purchase.ProjectPurchase;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 项目合同采购Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class ProjectPurchaseService extends JicActService<ProjectPurchaseDao, ProjectPurchase> {

    @Override
    public ProjectPurchase get(String id) {
        ProjectPurchase purchase = super.get(id);
        if (purchase == null)
            return new ProjectPurchase();
//        purchase.setExecutionItemList(itemDao.findList(new ProjectPurchaseItem(purchase)));
        return purchase;
    }

    @Override
    public void setupVariable(ProjectPurchase purchase, Map<String, Object> varMap) {
        varMap.put(ActUtils.VAR_PRJ_ID, purchase.getApply().getId());

        varMap.put(ActUtils.VAR_PRJ_TYPE, purchase.getApply().getCategory());

        varMap.put(ActUtils.VAR_TITLE, purchase.getApply().getProjectName());

        varMap.put(ActUtils.VAR_SKIP_BOSS, MyDictUtils.isPurchaseAuditInt(purchase.getAmount()));
        varMap.put(ActUtils.VAR_SKIP_HR, MyDictUtils.isHrAuditInt(purchase.getContractType()));
    }
}