/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.contract;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.oa.entity.OaNotify;
import com.thinkgem.jeesite.modules.oa.entity.OaNotifyRecord;
import com.thinkgem.jeesite.modules.oa.service.OaNotifyService;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractDao;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractItemDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import jdk.nashorn.internal.ir.ReturnNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 合同Service
 * @author jicdata
 * @version 2016-03-09
 */
@Service
@Transactional(readOnly = true)
public class ProjectContractService extends JicActService<ProjectContractDao, ProjectContract> {

	@Autowired
	private ProjectContractItemDao itemDao;

	@Autowired
	private OaNotifyService notifyService;

	@Autowired
	private MailService mailService;
	
	public ProjectContractItem getContractItem(String itemId){
		return itemDao.get(itemId);
	}

    public List<ProjectContractItem> findItemList(ProjectContractItem entity){
	    return itemDao.findList(entity);
    }
	
	@Override
	public ProjectContract get(String id) {
		ProjectContract projectContract = super.get(id);
		// in case param id is not contract's id.
		if (projectContract == null)
		    return null;

		projectContract.setProjectContractItemList(itemDao.findList(new ProjectContractItem(projectContract)));
		return projectContract;
	}

	public Page<ProjectContract> findPage(Page<ProjectContract> page, ProjectContract projectContract) {

		// 设置默认时间范围，默认当前月
		if (projectContract.getQueryBeginDate() == null){
			projectContract.setQueryBeginDate(DateUtils.setDays(DateUtils.parseDate(DateUtils.getDate()), 1));
		}
		if (projectContract.getQueryEndDate() == null){
			projectContract.setQueryEndDate(DateUtils.addMonths(projectContract.getQueryBeginDate(), 1));
		}

		return super.findPage(page, projectContract);

	}

	// 流程启动之前，设置map
	@Override
	public void setupVariable(ProjectContract projectContract, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, projectContract.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectContract.getApply().getCategory());

		vars.put(ActUtils.VAR_PROC_DEF_KEY, projectContract.getDictRemarks());

		vars.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());

		if (StringUtils.isEmpty(projectContract.getApply().getProjectName())) {
			vars.put(ActUtils.VAR_TITLE, projectContract.getClientName());
		}

		// 设置合同金额
		vars.put(ActUtils.VAR_AMOUNT, projectContract.getAmount());

		if ("03".equals(projectContract.getApply().getCategory()) ) {
			System.out.println("");
			// 分支上使用，没在节点上使用
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(projectContract.getAmount(),
				projectContract.getProfitMargin());
		if (isBossAudit) { // 需要总经理审批
			// 节点上使用
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectContract projectContract) {
		super.save(projectContract);
		saveItem(projectContract);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectContract projectContract) {
		super.delete(projectContract);
		itemDao.delete(new ProjectContractItem(projectContract));
	}

	/**
	 * 根据合同编号获取合同
	 * @param contractCode
	 * @return
	 */
	public ProjectContract getByCode(String contractCode) {
		if (StringUtils.isEmpty(contractCode)) {
			return null;
		}
		ProjectContract contract = new ProjectContract();
		contract.setContractCode(contractCode);
		return dao.getByCode(contract);
	}

	// 自动更新此状态，用来流程结束时，更新原合同的续签状态
	public void handledByOriginCode(String id) {
		ProjectContract contract = get(id);
		if (contract == null) {
			return;
		}
		String originCode = contract.getOriginCode();
		contract = getByCode(originCode);
		if (contract == null) {
			logger.error("此合同编号不存在" + originCode );
			return;
		}
		dao.handled(contract);
	}

	public List<ProjectContract> findNotify1List(ProjectContract entity) {
		return dao.findNotify1List(entity);
	}

	public List<ProjectContract> findNotify2List(ProjectContract entity) {
		return dao.findNotify2List(entity);
	}

	public List<ProjectContract> findNotify3List(ProjectContract entity) {
		return dao.findNotify3List(entity);
	}

	public List<ProjectContract> findPreEndList(ProjectContract projectContract) {
		return dao.findPreEndList(projectContract);
	}

	// page在Service中组装
	public Page<ProjectContract> findPreEndPage(Page<ProjectContract> page, ProjectContract entity) {
		entity.setPage(page);
		page.setList(dao.findPreEndList(entity));
		return page;
	}


	public Long findPreEndCount(ProjectContract projectContract) {
		return dao.findPreEndCount(projectContract);
	}

	// 审批过程中
	@Override
	public void processAudit(ProjectContract projectContract, Map<String, Object> vars) {
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectContract.getAct().getTaskDefKey();

		if ( UserTaskType.UT_SPECIALIST.equals(taskDefKey) ) {
			if(StringUtils.isNotBlank(projectContract.getContractCode())){
				// 保存合同编号
				save(projectContract);
			}
		} else if ( UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey) ) {
			if ("4".equals(projectContract.getContractType())) {
				// 保存服务合同的毛利率
				save(projectContract);
			}
		}
		// else if (UserTaskType.UT_COMMERCE_LEADER.equalsIgnoreCase(taskDefKey)) {
		// 	// 保存合同编号
		// 	save(projectContract);
		// }
	}

	// 审批结束时，更新实体的字段
	@Override
	public void processAuditEnd(ProjectContract contract) {
		contract.setAuditEndDate(new Date()) ;
	}

	@Transactional(readOnly = false)
	public void findContractToNotify() {

		OaNotify oaNotify = new OaNotify();
		oaNotify.setType("4");
		notifyService.deleteByType(oaNotify);

		ProjectContract contract = new ProjectContract();

		List<ProjectContract> contracts = findNotify1List(contract);
		notifyList(contracts, "1");

		contracts = findNotify2List(contract);
		notifyList(contracts, "2");

		contracts = findNotify3List(contract);
		notifyList(contracts, "3");
	}

	private void notifyList(List<ProjectContract> contractList, String type) {

		if (contractList == null || contractList.isEmpty()) {
			return;
		}

		OaNotify notify ;
		List<OaNotifyRecord> oaNotifyRecordList;
		OaNotifyRecord record;
//		List<User> userList = Collections.emptyList();
		List<User> userList = new ArrayList<>();

		User officeLeader, officeBoss;

		// 共有的人员
		Role role = UserUtils.getRoleByEnname("usertask_specialist");
		if (role != null && role.getUserList() != null && !role.getUserList().isEmpty()) {
			userList.addAll(role.getUserList());
		}

		StringBuilder sb = new StringBuilder();
		for (ProjectContract contract : contractList) {
			System.out.println();
			// 新建
			notify = new OaNotify();
			oaNotifyRecordList = Lists.newArrayList();
			notify.setOaNotifyRecordList(oaNotifyRecordList);

			// master通知
			notify.setType(DictUtils.getDictValue("合同预警", "oa_notify_type", "4") );
            // 通知标题：客户名称、合同号
			String title = StringUtils.isEmpty(contract.getContractCode()) ? contract.getClientName() : contract.getContractCode();
			notify.setTitle(title);
			double x = DateUtils.getDistanceOfTwoDate(new Date(), contract.getEndDate() ) + 1;
			if (x > 0) {
				title = "距离合同到期还有" + x + "天！";
			} else {
				title = "合同已超期" + Math.abs(x) + "天！";
			}
			notify.setContent(contract.getApply().getProjectName()
					+ "\n项目编号：" + contract.getApply().getProjectCode()
					+ "\n合同编号：" + contract.getContractCode()
					+ "\n客户名称：" + contract.getClientName()
					+ "\n合同到期日期：" + DateUtils.formatDateTime(contract.getEndDate())
					+ "\n" + title);
			notify.setStatus("1");
			notify.setCreateBy(UserUtils.get("1"));
			// 把合同ID带入通知实体
			notify.setRemarks(contract.getId());


			// 30天内
			if ("1".equals(type)) { // 只通知自己 30 < x < 60
				userList.add(UserUtils.get(contract.getCreateBy().getId()));
			} else if ("2".equals(type)) { // 自己和直接领导 0< x < 30
				officeLeader = UserUtils.get(contract.getCreateBy().getId()).getOffice().getPrimaryPerson();
				userList.add(officeLeader);
			} else if ("3".equals(type)) { // 自己、直接领导、分管 x > 0
				officeBoss = UserUtils.get(contract.getCreateBy().getId()).getOffice().getDeputyPerson();
				userList.add(officeBoss);
			}
			// 接收通知的人
			for (User user : userList) {
				System.out.println();
				record = new OaNotifyRecord();
				record.setId(IdGen.uuid());
				record.setOaNotify(notify);
				record.setUser(user);
				record.setReadFlag("0");
				oaNotifyRecordList.add(record);
			}

			// 发邮件
			mailService.sendNotifyEmail(notify, userList);
			// 保存通知
			notifyService.save(notify);
		} // end for list
	}

	private boolean shouldBossAudit(ProjectContract projectContract) {
		boolean shouldAudit = false;
		for (ProjectContractItem projectContractItem : projectContract.getProjectContractItemList()){

			shouldAudit = MyDictUtils.isBossAudit(projectContractItem.getContractAmount(),
					projectContractItem.getGrossProfitMargin());

			if (shouldAudit) {
				return shouldAudit;
			}
		}

		return false;
	}


	private void saveItem(ProjectContract projectContract) {
        for (ProjectContractItem projectContractItem : projectContract.getProjectContractItemList()){

//            if (StringUtils.isBlank(projectContractItem.getId())) {
//                continue;
//            }

            if (projectContractItem.getId() == null){
                continue;
            }

            if (ProjectContractItem.DEL_FLAG_NORMAL.equals(projectContractItem.getDelFlag())){
                if (StringUtils.isBlank(projectContractItem.getId())){
                    projectContractItem.setContract(projectContract);
                    projectContractItem.preInsert();
                    itemDao.insert(projectContractItem);
                }else{
                    projectContractItem.preUpdate();
                    itemDao.update(projectContractItem);
                }
            }else{
                itemDao.delete(projectContractItem);
            }
        }
    }

}