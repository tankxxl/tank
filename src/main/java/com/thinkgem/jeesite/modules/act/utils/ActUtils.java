/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.act.utils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.annotation.FieldName;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.ObjectUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程工具
 * @author ThinkGem
 * @version 2013-11-03
 */
public class ActUtils {

//	private static Logger logger = LoggerFactory.getLogger(ActUtils.class);
	
	/**
	 * 定义流程定义KEY，必须以“PD_”开头
	 * 组成结构：string[]{"流程标识","业务主表表名"}
	 */
//	public static final String[] PD_LEAVE = new String[]{"leave", "oa_leave"};
//	public static final String[] PD_TEST_AUDIT = new String[]{"test_audit", "oa_test_audit"};


    /**
     * 实体名、流程定义名、业务表名相关。
     * 目前实体名和流程定义名相同。
     */
    /**
     * 定义流程定义KEY，必须以“PD_”开头
     * 组成结构：string[]{"流程标识","业务主表表名"}
     */
    public static final String[] PD_LEAVE = new String[]{"leave", "oa_leave"};
    public static final String[] PD_TEST_AUDIT = new String[]{"test_audit", "oa_test_audit"};
    public static final String[] PD_PROJECTAPPLYEXTERNAL = new String[]{"ProjectApplyExternal", "project_apply_external"};
    public static final String[] PD_PROJECTBIDDING = new String[]{"ProjectBidding", "project_bidding"};
	public static final String[] PD_BIDDINGARCHIVE = new String[]{"BiddingArchive", "project_bidding_archive"};
    public static final String[] PD_PROJECTCONTRACT = new String[]{"ProjectContract", "project_contract"};
    public static final String[] PD_execution = new String[]{"ProjectExecution", "project_execution"};
    public static final String[] PD_purchase = new String[]{"ProjectPurchase", "project_purchase"};
    public static final String[] PD_invoice = new String[]{"ProjectInvoice", "project_invoice"};
    public static final String[] PD_PROJECTFINISHAPPROVAL = new String[]{"ProjectFinishApproval", "project_finish_approval"};
    public static final String[] PD_TECHAPPLY = new String[]{"Techapply", "project_techapply"};

	public static final String[] PD_contract_1 = new String[]{"ContractServ", "project_contract"};
	public static final String[] PD_contract_2 = new String[]{"ContractManage", "project_contract"};
	public static final String[] PD_contract_3 = new String[]{"ContractSale", "project_contract"};
	public static final String[] PD_contract_4 = new String[]{"ContractPurchase", "project_contract"};
	public static final String[] PD_contract_5 = new String[]{"ContractConsumerFinance", "project_contract"};

    // 同意 or 驳回 申请单，不用此变量
    // public static final String VAR_PASS = "pass";
    // 流程节点标题
    public static final String VAR_TITLE = "title";
    // 项目类型，字典中key值，在后期审批节点中需要用项目类型来查找审批角色 *
    public static final String VAR_PRJ_TYPE = "prjType";

	// 根据项目类型来设置，软件类项目为2，其它类项目为1 *
	public static final String VAR_TYPE = "type";
    // 申请单类名
    public static final String VAR_CLASS_TYPE = "classType";
    // 申请单id *
    public static final String VAR_OBJ_ID = "objId";
    // 项目id *
    public static final String VAR_PRJ_ID = "prjId";

    // 流程定义key，用于启动流程
	public static final String VAR_PROC_DEF_KEY = "proc_def_key";

    // 强制结束流程标识，用于表示直接退出流程，可以用在流程监听器中，用来区分流程是否正常结束
	public static final String VAR_FORCE_END = "force_end";

    // 申请人loginName 在后期审批节点中需要用申请人来查找审批角色 *
    public static final String VAR_APPLY = "apply";
    // 需求部门分管领导，相对于采购部门分管领导，专用于采购流程，因为采购部门与需求部门不一样。
	public static final String VAR_REQ_BOSS = "requirementBoss";

    // 是否需要总经理审批，此变量不再使用，使用意思相反的skip_boss变量
    // @Deprecated
    // public static final String VAR_BOSS_AUDIT = "boss";

    // 是否需要人力部负责人审批，此变量不再使用，使用意思相反的skip_hr变量
    // @Deprecated
    // public static final String VAR_HR_AUDIT = "hr";

    // 发邮件时使用，用于生成邮件标题，此变量不需要了，可以通过ProcessDefCache.get(task.getProcessDefinitionId())获得
    // public static final String VAR_PROC_NAME = "procName";

    // 1为skip
    public static final String VAR_SKIP_BOSS = "skip_boss";
    // 1为skip，是否跳过风控部门
	public static final String VAR_SKIP_RISK = "skip_risk";
    public static final String VAR_SKIP_HR = "skip_hr";
    // 传入流程中的金额，用于在流程中进行数字判断，如：金额>10W需要分管领导节点审批等，这是在流程图进行判断，
	// 如果在流程图中写表达式来判断，就不用在程序中设置skip_vp等字段了。

    public static final String VAR_AMOUNT = "amount";
	public static final String VAR_SKIP_inout = "skip_inout";
	// 1为skip，bjkj，是否需要技术开发中心审批，是否自研项目
	public static final String VAR_SKIP_DEV = "skip_dev";
	// 1为直接跳转到结束节点，0为逐级审批，用在投标备案流程
	public static final String VAR_END = "end";

	// 此变量不需要了，可以通过ProcessDefCache.get(task.getProcessDefinitionId())获得
	@Deprecated
    public static final String PROC_NAME_APPLY = "项目立项审批流程";
    public static final String PROC_NAME_BIDDING = "项目投标审批流程";
    public static final String PROC_NAME_CONTRACT = "项目合同审批流程";
    public static final String PROC_NAME_execution = "合同执行审批流程";
    public static final String PROC_NAME_purchase = "采购审批流程";
    public static final String PROC_NAME_invoice = "开票审批流程";
    public static final String PROC_NAME_FINISH = "项目结项审批流程";
    public static final String PROC_NAME_TECH = "技术资源申请流程";
	// public static final String PROC_NAME_TECH = "技术资源申请流程";

	/**
	 * 流程定义Map（自动初始化）
	 */
	private static Map<String, String> procDefMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			for (Field field : ActUtils.class.getFields()){
				if(StringUtils.startsWith(field.getName(), "PD_")){
					try{
						String[] ss = (String[])field.get(null);
						put(ss[0], ss[1]);
					}catch (Exception e) {
//						logger.debug("load pd error: {}", field.getName());
					}
				}
			}
		}
	};

	public static String getBusinessTableByClassName(String className) {
	    String businessTable = procDefMap.get(className);
	    return businessTable;
    }
//	
//	/**
//	 * 获取流程执行（办理）URL
//	 * @param procId
//	 * @return
//	 */
//	public static String getProcExeUrl(String procId) {
//		String url = procDefMap.get(StringUtils.split(procId, ":")[0]);
//		if (StringUtils.isBlank(url)){
//			return "404";
//		}
//		return url;
//	}
	
	@SuppressWarnings({ "unused" })
	public static Map<String, Object> getMobileEntity(Object entity,String spiltType){
		if(spiltType==null){
			spiltType="@";
		}
		Map<String, Object> map = Maps.newHashMap();

		List<String> field = Lists.newArrayList();
		List<String> value = Lists.newArrayList();
		List<String> chinesName =Lists.newArrayList();
		
		try{
			for (Method m : entity.getClass().getMethods()){
				if (m.getAnnotation(JsonIgnore.class) == null && m.getAnnotation(JsonBackReference.class) == null && m.getName().startsWith("get")){
					if (m.isAnnotationPresent(FieldName.class)) {
						Annotation p = m.getAnnotation(FieldName.class);
						FieldName fieldName=(FieldName) p;
						chinesName.add(fieldName.value());
					}else{
						chinesName.add("");
					}
					if (m.getName().equals("getAct")){
						Object act = m.invoke(entity, new Object[]{});
						Method actMet = act.getClass().getMethod("getTaskId");
						map.put("taskId", ObjectUtils.toString(m.invoke(act, new Object[]{}), ""));
					}else{
						field.add(StringUtils.uncapitalize(m.getName().substring(3)));
						value.add(ObjectUtils.toString(m.invoke(entity, new Object[]{}), ""));
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		map.put("beanTitles", StringUtils.join(field, spiltType));
		map.put("beanInfos", StringUtils.join(value, spiltType));
		map.put("chineseNames", StringUtils.join(chinesName, spiltType));
		
		return map;
	}
	
	/**
	 * 获取流程表单URL
	 * @param formKey
	 * @param act 表单传递参数
	 * @return
	 */
	public static String getFormUrl(String formKey, Act act){
		
		StringBuilder formUrl = new StringBuilder();
		
		String formServerUrl = Global.getConfig("activiti.form.server.url");
		if (StringUtils.isBlank(formServerUrl)){
			formUrl.append(Global.getAdminPath());
		}else{
			formUrl.append(formServerUrl);
		}
		
		formUrl.append(formKey).append(formUrl.indexOf("?") == -1 ? "?" : "&");
		formUrl.append("act.taskId=").append(act.getTaskId() != null ? act.getTaskId() : "");
		formUrl.append("&act.taskName=").append(act.getTaskName() != null ? Encodes.urlEncode(act.getTaskName()) : "");
		formUrl.append("&act.taskDefKey=").append(act.getTaskDefKey() != null ? act.getTaskDefKey() : "");
		formUrl.append("&act.procInsId=").append(act.getProcInsId() != null ? act.getProcInsId() : "");
		formUrl.append("&act.procDefId=").append(act.getProcDefId() != null ? act.getProcDefId() : "");
		formUrl.append("&act.status=").append(act.getStatus() != null ? act.getStatus() : "");
		formUrl.append("&id=").append(act.getBusinessId() != null ? act.getBusinessId() : "");
		
		return formUrl.toString();
	}
	
	/**
	 * 转换流程节点类型为中文说明
	 * @param type 英文名称
	 * @return 翻译后的中文名称
	 */
	public static String parseToZhType(String type) {
		Map<String, String> types = new HashMap<String, String>();
		types.put("userTask", "用户任务");
		types.put("serviceTask", "系统任务");
		types.put("startEvent", "开始节点");
		types.put("endEvent", "结束节点");
		types.put("exclusiveGateway", "条件判断节点(系统自动根据条件处理)");
		types.put("inclusiveGateway", "并行处理任务");
		types.put("callActivity", "子流程");
		return types.get(type) == null ? type : types.get(type);
	}

	public static UserEntity toActivitiUser(User user){
		if (user == null){
			return null;
		}
		UserEntity userEntity = new UserEntity();
		userEntity.setId(user.getLoginName());
		userEntity.setFirstName(user.getName());
		userEntity.setLastName(StringUtils.EMPTY);
		userEntity.setPassword(user.getPassword());
		userEntity.setEmail(user.getEmail());
		userEntity.setRevision(1);
		return userEntity;
	}
	
	public static GroupEntity toActivitiGroup(Role role){
		if (role == null){
			return null;
		}
		GroupEntity groupEntity = new GroupEntity();
		groupEntity.setId(role.getEnname());
		groupEntity.setName(role.getName());
		groupEntity.setType(role.getRoleType());
		groupEntity.setRevision(1);
		return groupEntity;
	}
	
	public static void main(String[] args) {
		 User user = new User();
		 System.out.println(getMobileEntity(user, "@"));
	}
}
