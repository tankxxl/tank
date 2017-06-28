/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.act.service.ext;

import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;

/**
 * 对不同表的CRUD操作均由一个对应的实体管理器(XxxEntityManager，有接口和实现类)
 * 引擎的7个Service接口在需要CRUD实体时会根据接口获取注册的实体管理器实现类(初始化引擎时用Map对象维护两者的映射关系)
 * 并且引擎允许我们覆盖内部的实体管理器，查看源码后可以知道有关用户操作的两个接口分别为：UserEntityManager和GroupEntityManager.
 *
 * 查看引擎配置对象ProcessEngineConfigurationImpl类可以找到一个名称为"customSessionFactories"的属性，
 * 该属性可以用来自定义SessionFactory(每一个XxxManager类都是一个Session<实现Session接口>，由SessionFactory来管理)，
 * 为了能替代内部的实体管理器的实现我们可以自定义一个SessionFactory并注册到引擎。
 *
 * 这种自定义SessionFactory的方式适用于公司内部有独立的身份系统或者公共的身份模块的情况，所有和用户、角色、权限的服务均通过一个统一的接口获取，
 * 而业务系统则不保存这些数据，此时引擎的身份模块表就没必要存在(ACT_ID_*)。
 * 所以，重写了其增删改查等方法后，就摒弃了Activiti的用户表。
 *
 * Activiti Group Entity Service
 * @author ThinkGem
 * @version 2013-12-05
 */
@Service
public class ActGroupEntityService extends GroupEntityManager {

	private SystemService systemService;

	public SystemService getSystemService() {
		if (systemService == null){
			systemService = SpringContextHolder.getBean(SystemService.class);
		}
		return systemService;
	}

	@Override
	public Group createNewGroup(String groupId) {
		return new GroupEntity(groupId);
	}

	@Override
	public void insertGroup(Group group) {
//		getDbSqlSession().insert((PersistentObject) group);
		throw new RuntimeException("not implement method.");
	}

	public void updateGroup(GroupEntity updatedGroup) {
//		CommandContext commandContext = Context.getCommandContext();
//		DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
//		dbSqlSession.update(updatedGroup);
		throw new RuntimeException("not implement method.");
	}

	@Override
	public void deleteGroup(String groupId) {
//		GroupEntity group = getDbSqlSession().selectById(GroupEntity.class, groupId);
//		getDbSqlSession().delete("deleteMembershipsByGroupId", groupId);
//		getDbSqlSession().delete(group);
		throw new RuntimeException("not implement method.");
	}

	@Override
	public GroupQuery createNewGroupQuery() {
//		return new GroupQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
		throw new RuntimeException("not implement method.");
	}

//	@SuppressWarnings("unchecked")
	@Override
	public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
//		return getDbSqlSession().selectList("selectGroupByQueryCriteria", query, page);
		throw new RuntimeException("not implement method.");
	}

	@Override
	public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
//		return (Long) getDbSqlSession().selectOne("selectGroupCountByQueryCriteria", query);
		throw new RuntimeException("not implement method.");
	}

	@Override
	public List<Group> findGroupsByUser(String userId) {
//		return getDbSqlSession().selectList("selectGroupsByUserId", userId);
		List<Group> list = Lists.newArrayList();
		User user = getSystemService().getUserByLoginName(userId);
		if (user != null && user.getRoleList() != null){
			for (Role role : user.getRoleList()){
				list.add(ActUtils.toActivitiGroup(role));
			}
		}
		return list;
	}

	@Override
	public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
//		return getDbSqlSession().selectListWithRawParameter("selectGroupByNativeQuery", parameterMap, firstResult, maxResults);
		throw new RuntimeException("not implement method.");
	}

	@Override
	public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
//		return (Long) getDbSqlSession().selectOne("selectGroupCountByNativeQuery", parameterMap);
		throw new RuntimeException("not implement method.");
	}

}