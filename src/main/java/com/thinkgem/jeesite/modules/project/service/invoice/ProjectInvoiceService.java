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
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;
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
 * 为了使用基于@Transactional的事务管理，需要在Spring中进行如下的配置：
 * 注释：声明使用注解式事务
 * <tx:annotation-driven transaction-manager="transactionManager" />
 *
 * 本质上，@Transactional使用了JDBC的事务来进行事务控制的。
 * <annotation-driven>标签的声明，则是在Spring内部启用@Transactional来进行事务管理，类似开关之类的声明。
 *
 * spring支持编程式事务管理和声明式事务管理两种方式。
 *
 * 声明式事务管理建立在AOP之上的。其本质是对方法前后进行拦截，然后在目标方法开始之前创建或者加入一个事务，
 * 在执行完目标方法之后根据执行情况提交或者回滚事务。声明式事务最大的优点就是不需要通过编程的方式管理事务，
 * 这样就不需要在业务逻辑代码中掺杂事务管理的代码，只需在配置文件中做相关的事务规则声明(或通过基于@Transactional注解的方式)，
 * 便可以将事务规则应用到业务逻辑中。
 *
 * 显然声明式事务管理要优于编程式事务管理，这正是spring倡导的非侵入式的开发方式。
 * 声明式事务管理使业务代码不受污染，一个普通的POJO对象，只要加上注解就可以获得完全的事务支持。
 *
 * 默认情况下，只有来自外部的方法调用才会被AOP代理捕获，
 * 也就是，类内部方法调用本类内部的其他方法并不会引起事务行为，即使被调用方法使用@Transactional注解进行修饰。
 *
 */
@Service
// 放在类级别上相当于该类的所有方法都加上了Transactional事务管理
@Transactional(readOnly = true) // 查询使用只读
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

    // private boolean isNewRecord;


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

    /**
     * 单独获得item进行编辑，弹出dialog专门编辑item
     * @param id itemId，从前台传入
     * @return
     */
    public ProjectInvoiceItem getItem(String id) {
        // return super.get(id);
        ProjectInvoiceItem item = itemDao.get(id);
        // in case param id is not invoiceItem's id.
        if (item == null) {
            item = new ProjectInvoiceItem();
        }
        return item;
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
        vars.put(ActUtils.VAR_RESIGN, "0");

        // vars.put(ActUtils.VAR_PRJ_TYPE, projectInvoice.getApply().getCategory());

        // vars.put(ActUtils.VAR_PROC_DEF_KEY, projectInvoice.getDictRemarks());

        // vars.put(ActUtils.VAR_TITLE, projectInvoice.getApply().getProjectName());

        // if (StringUtils.isEmpty(projectInvoice.getApply().getProjectName())) {
            // vars.put(ActUtils.VAR_TITLE, projectInvoice.getClientName());
        // }

        // 设置合同金额
        // vars.put(ActUtils.VAR_AMOUNT, projectInvoice.getAmount());

        // if ("03".equals(projectInvoice.getApply().getCategory()) ) {
        //     System.out.println("");
        //     // 分支上使用，没在节点上使用
        //     vars.put(ActUtils.VAR_TYPE, "2");
        // } else {
        //     vars.put(ActUtils.VAR_TYPE, "1");
        // }
    }

    // 审批过程中
    @Override
    public void processAudit(ProjectInvoice projectInvoice, Map<String, Object> vars) {
        // 对不同环节的业务逻辑进行操作
        String taskDefKey = projectInvoice.getAct().getTaskDefKey();
        if ( UserTaskType.UT_SPECIALIST.equals(taskDefKey) ) {
            // 	// 保存合同编号
            // 	save(projectContract);
        }
    }

    // 审批结束时，更新实体的字段
    @Override
    public void processAuditEnd(ProjectInvoice projectInvoice) {
    }


    //
    // /**
	//  * 保存并结束流程
	//  * @param projectInvoice
	//  */
	// @Transactional(readOnly = false)
	// public void saveFinishProcess(ProjectInvoice projectInvoice) {
     //    // 保存
     //    // save(projectInvoice);
     //    // 开启流程
     //    // String procInsId = launchWorkflow(projectInvoice);
     //    String procInsId = saveLaunch(projectInvoice);
     //    // 结束流程
     //    endProcess(procInsId);
	// }

    /**
     * 保存表单数据，并启动流程
     *
     * 申请人发起流程，申请人重新发起流程入口
     * 在form界面
     *
     * @param projectInvoice
     */
    // @Transactional(readOnly = false)
    // public String saveLaunch(ProjectInvoice projectInvoice) {
    //     if (projectInvoice.getIsNewRecord()) {
    //         // 启动流程的时候，把业务数据放到流程变量里
    //         Map<String, Object> varMap = new HashMap<String, Object>();
    //         // varMap.put(ActUtils.VAR_PRJ_ID, projectInvoice.getApply().getId());
    //         // varMap.put(ActUtils.VAR_PRJ_TYPE, projectInvoice.getApply().getCategory());
    //         // varMap.put(ActUtils.VAR_TITLE, projectInvoice.getApply().getProjectName());
    //         return launch(projectInvoice, varMap);
    //     } else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
    //         // 只要不是启动流程，其它任意节点的跳转都当成节点审批
    //         saveAudit(projectInvoice);
    //         return null;
    //     }
    // }

    /**
     * 保存表单数据
     * @param projectInvoice
     */
    @Override
    // 方法上的Transactional会覆盖类上声明的事务
    @Transactional(readOnly = false) // 增删改都要写readOnly=false
    public void save(ProjectInvoice projectInvoice) {
        // isNewRecord = projectInvoice.getIsNewRecord();
        super.save(projectInvoice);
        saveItem(projectInvoice);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(ProjectInvoice projectInvoice) {
        super.delete(projectInvoice);
        itemDao.delete(new ProjectInvoiceItem(projectInvoice));
    }

    @Transactional(readOnly = false)
    public void deleteItemByIds(String[] ids) {
        itemDao.deleteByIds(ids);
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

            // if (item.getId() == null){
            //     continue;
            // }

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