/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.invoice;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceItemDao;
import com.thinkgem.jeesite.modules.project.dao.invoice.ProjectInvoiceReturnDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceReturn;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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

	@Autowired
    ProjectInvoiceItemDao itemDao;

	@Autowired
    ProjectInvoiceReturnDao returnDao;

	@Autowired
    SystemService systemService ;


    @Override
    public ProjectInvoice get(String id) {
        // return super.get(id);
        ProjectInvoice invoice = super.get(id);
        // in case param id is not invoice's id.
        if (invoice == null)
            return invoice;

        // invoice.setInvoiceItemList(itemDao.findList(new ProjectInvoiceItem(invoice)));
        // 只查最新版本
        invoice.setInvoiceItemList(itemDao.findHeadList(new ProjectInvoiceItem(invoice)));
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

    /**
     * 根据item id 获取发票，并关联一个申请单
     * 这些发票是给前端重开票用的，所以要把发票号给去掉
     * @param projectInvoice
     * @param itemIds
     * @return
     */
    public ProjectInvoice getItems(ProjectInvoice projectInvoice, String[] itemIds) {
        if (projectInvoice == null) {
            projectInvoice = new ProjectInvoice();
        }
        List<ProjectInvoiceItem> itemList = Lists.newArrayList();
        ProjectInvoiceItem item ;
        for (int i = 0; i < itemIds.length; i++) {
            item = getItem(itemIds[i]);
            item.setInvoiceNo(""); // 给前端返回时，去掉发票号，好让前端重新填写发票号
            itemList.add(item);
        }
        projectInvoice.setInvoiceItemList(itemList);
        return projectInvoice;
    }

    // 根据某个发票id查找此发票的全部版本
    public List<ProjectInvoiceItem> findVerList(String itemId) {
        if (StringUtils.isEmpty(itemId)) {
            return new ArrayList<>();
        }

        ProjectInvoiceItem item = new ProjectInvoiceItem();
        // ProjectContract contract = new ProjectContract();
        // contract.setId(itemId);
        // item.setContract(contract);
        item.setId(itemId);

        List<ProjectInvoiceItem> items = itemDao.findVerList(item);
        if (items == null) {
            return new ArrayList<>();
        }
        if (items.isEmpty()) {
            return items;
        }
        return items;
    }


    public List<ProjectInvoice> findListByContractId(ProjectInvoice projectInvoice) {
        return dao.findListByContractId(projectInvoice);
    }

    public List<ProjectInvoiceReturn> findReturnByContractId(ProjectInvoice projectInvoice) {
        return returnDao.findListByContractId(projectInvoice);
    }

    // 流程相关，流程启动之前，设置map
    @Override
    public void setupVariable(ProjectInvoice projectInvoice, Map<String, Object> vars) {
        vars.put(ActUtils.VAR_TITLE, projectInvoice.getRemarks() );
        if ("resign".equals(projectInvoice.getFunc())) {
            vars.put(ActUtils.VAR_RESIGN, "1");
        } else {
            vars.put(ActUtils.VAR_RESIGN, "0");
        }
    }

    // 审批过程中，可以保存业务数据
    @Override
    public void processAudit(ProjectInvoice projectInvoice, Map<String, Object> vars) {
        // 对不同环节的业务逻辑进行操作
        String taskDefKey = projectInvoice.getAct().getTaskDefKey();
        if ( UserTaskType.UT_SPECIALIST.equals(taskDefKey) ) {
            // 保存合同编号
            // save(projectInvoice);
        }
    }

    // 审批结束时，可以保存业务数据
    @Override
    public void processAuditEnd(ProjectInvoice projectInvoice) {
        // save(projectInvoice);
    }

    /**
     * 保存表单数据
     * @param projectInvoice
     */
    @Override
    // 方法上的Transactional会覆盖类上声明的事务
    @Transactional(readOnly = false) // 增删改都要写readOnly=false
    public void save(ProjectInvoice projectInvoice) {

        if ( "resign".equals(projectInvoice.getFunc()) ) {
            projectInvoice.setId(""); // 把各个entity的id去掉，数据库就是新增记录了
        }
        // 父类保存自己
        super.save(projectInvoice);

        // ，再保存自己的孩子，如果是重开流程
        if ( "resign".equals(projectInvoice.getFunc()) ) {
            saveAddVerItem(projectInvoice);
        } else {
            saveItems(projectInvoice);
        }
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

    /**
     * 根据合同号获取开票项是否重了
     * 一个合同号只能发起一次开票流程，之后只能走重开票流程，
     * 说错了，可以任意发起开票、重开流程
     * @param contractCode
     * @return
     */
    public ProjectInvoiceItem getItemByContractCode(String contractCode) {
        ProjectInvoiceItem item = new ProjectInvoiceItem();
        ProjectContract contract = new ProjectContract();
        item.setContract(contract);
        contract.setContractCode(contractCode);

        return itemDao.findByContractCode(item);
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

    // 增加一条开票，并增加版本号
    private void saveAddVerItem(ProjectInvoice projectInvoice) {
        ProjectInvoiceItem invoiceItem = null;
        String itemId = null;
        for (ProjectInvoiceItem item : projectInvoice.getInvoiceItemList()) {
            itemId = item.getId(); // 保存原发票id
            item.setId(""); // 置空，以便重新生成id

            item.setInvoice(projectInvoice); // 重新设置一下申请单id
            item.incVer(); // ver + 1

            item.preInsert();
            itemDao.insert(item); // 新增发票，并增加版本号

            // 处理作废的发票，更新一下状态
            invoiceItem = getItem(itemId); // 从数据库中把老的再取出来
            invoiceItem.setInvalid("1"); // 作废
            invoiceItem.setpId(item.getId()); // 关联新的记录id
            invoiceItem.preUpdate();
            itemDao.update(invoiceItem); // 更新老发票
        }
    }

    /**
     * 单独修改一个item
     *
     * @param invoiceItem
     */
    @Transactional(readOnly = false)
    public void saveItem(ProjectInvoiceItem invoiceItem) {

        if (invoiceItem.getIsNewRecord()){
            invoiceItem.preInsert();
            itemDao.insert(invoiceItem);
        }else{
            invoiceItem.preUpdate();
            itemDao.update(invoiceItem);
        }
    }


    /**
     * bean方式给ureport提供数据，方法一定要有以下三个参数
     * @param dsName
     * @param datasetName
     * @param parameters
     * @return
     */
    public List<User> buildReport(String dsName, String datasetName, Map<String, Object> parameters) {
        List<User> users = systemService.findUser(new User());
        return users;
    }

    public List<Map<String, Object>> loadReport(String dsName, String datasetName, Map<String, Object> parameters) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", 1);
            m.put("name", RandomStringUtils.random(10, true, false));
            m.put("salary", RandomUtils.nextInt(10000) + i);
        }
        return list;
    }

}