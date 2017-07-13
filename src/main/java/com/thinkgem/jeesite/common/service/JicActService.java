/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.service;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 带流程操作的Service基类
 * 子类定义泛型
 * 父类使用子类定义的泛型
 * 把子类中定义的泛型给父类。
 * 父类要具体化的泛型。
 * @author ThinkGem
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class JicActService<D extends JicDao<T>, T extends ActEntity<T>>
        extends CrudService<D, T> {


    @Autowired
    ActTaskService actTaskService;

    @Loggable
    protected Logger logger;
	
//	/**
//	 * 持久层对象
//	 */
//	@Autowired
//	protected D dao;
//
//	/**
//	 * 获取单条数据
//	 * @param id
//	 * @return
//	 */
//	public T get(String id) {
//		return dao.get(id);
//	}
//
//	/**
//	 * 获取单条数据
//	 * @param entity
//	 * @return
//	 */
//	public T get(T entity) {
//		return dao.get(entity);
//	}
//
//	/**
//	 * 查询列表数据
//	 * @param entity
//	 * @return
//	 */
//	public List<T> findList(T entity) {
//		return dao.findList(entity);
//	}
//
//	/**
//	 * 查询分页数据
//	 * @param page 分页对象
//	 * @param entity
//	 * @return
//	 */
//	public Page<T> findPage(Page<T> page, T entity) {
//		entity.setPage(page);
//		page.setList(dao.findList(entity));
//		return page;
//	}
//
//	/**
//	 * 保存数据（插入或更新）
//	 * @param entity
//	 */
//	@Transactional(readOnly = false)
//	public void save(T entity) {
//		if (entity.getIsNewRecord()){
//			entity.preInsert();
//			dao.insert(entity);
//		}else{
//			entity.preUpdate();
//			dao.update(entity);
//		}
//	}
//
//	/**
//	 * 删除数据
//	 * @param entity
//	 */
//	@Transactional(readOnly = false)
//	public void delete(T entity) {
//		dao.delete(entity);
//	}


    /**
     * 修改业务申请表中的流程状态字段
     * 流程listener中使用，用于改变业务表中的状态
     *
     * @param id
     * @param audit
     */
    @Transactional(readOnly = false)
    public void auditTo(String id, String audit) {
        T t = dao.get(id);
        if (t == null) {
            return;
        }
        t.setProcStatus(audit); // 设置业务申请表的流程状态
        save(t);
    }

    public T findByProcInsId(String procInsId) {

        if (StringUtils.isEmpty(procInsId)) {
            return null;
        } else {
            return dao.findByProcInsId(procInsId);
        }
    }

    public T findByProcInsId(T entity) {
        if (!entity.hasAct()) {
            return null;
        }
        Act oldAct = entity.getAct();
        entity = findByProcInsId(oldAct.getProcInsId());
        if (entity == null) {
            return entity;
        }
        entity.setAct(oldAct);
        return entity;
    }



    /**
     * 只启动流程，不保存业务数据
     *
     * @param entity
     * @param vars
     * @return
     */
    protected String launch(T entity, Map<String, Object> vars) {
        return actTaskService.startProcEatFirstTask( entity,
                null, vars );
    }

    // 重新启动流程
    // 算一次特殊的审批，可以修改业务数据(不是主要的)，还可以销毁流程(跟审批的主要区别)
    // 可以把申请人的重新提交跟审批人的审批合并。
    // protected String reLaunch(T entity, Map<String, Object> vars) {
    //     // 先保存业务数据
    //     save(entity);
    //     // 格式化审批意见
    //     entity.getAct().setComment(
    //             (entity.getAct().getFlagBoolean()?"[重申] ":"[销毁] ")
    //                     + entity.getAct().getComment()
    //     );
    //     // 前端传过来的值
    //     if (entity.getAct().getFlagBoolean()) { // 重新提交申请单
    //         // 完成流程任务
    //         actTaskService.complateByAct(entity.getAct(), vars);
    //     } else {
    //         delete((T) entity);
    //         actTaskService.deleteProcIns(entity.getProcInsId(), "");
    //     }
    //     return entity.getProcInsId();
    // }

    // public String launchWorkflowBase(ActEntity actEntity,
    //                boolean isNewRecord,
    //                String title,
    //                Map<String, Object> vars) {
    //
    //     if (isNewRecord) {  // 新建申请单
    //         return actTaskService.startProcEatFirstTask(
    //                 actEntity,
    //                 title,
    //                 vars
    //         );
    //
    //     } else {  // 修改申请单
    //         actEntity.getAct().setComment(
    //                 (actEntity.getAct().getFlagBoolean()?"[重申] ":"[销毁] ")
    //                         + actEntity.getAct().getComment()
    //         );
    //         // 前端传过来的值
    //         if (actEntity.getAct().getFlagBoolean()) { // 重新提交申请单
    //             // 完成流程任务
    //             vars.put(ActUtils.VAR_PASS, actEntity.getAct().getFlagNumber());
    //             actTaskService.complateByAct(actEntity.getAct(), vars);
    //         } else {
    //             delete((T) actEntity);
    //             actTaskService.deleteProcIns(actEntity.getProcInsId(), "");
    //         }
    //
    //         return actEntity.getProcInsId();
    //     }
    // }

    /**
     * 结束整个流程
     *
     * @param procInsId
     */
    public void endProcess(String procInsId) {
        if (StringUtils.isEmpty(procInsId))
            return;
        actTaskService.deleteProcIns(procInsId, "");
    }

    /**
     * 前端入口
     * 保存并结束流程
     *
     * @param entity
     */
    @Transactional(readOnly = false)
    public void saveFinishProcess(T entity) {
        String procInsId = saveLaunch(entity);
        // 结束流程
        endProcess(procInsId);
    }

    /**
     * 前端入口
     * 先保存业务数据，再启动流程
     *
     * @param entity
     * @return
     */
    @Transactional(readOnly = false)
    public String saveLaunch(T entity) {
        if (entity.getIsNewRecord()) {
            Map<String, Object> varMap = new HashMap<String, Object>();
            setupVariable(entity, varMap);
            // 先保存业务数据
            save(entity);
            return launch(entity, varMap);
        } else {
            saveAudit(entity);
            return null;
        }
    }

    /**
     * 前端入口
     * 审批人入口
     *
     * @param entity
     */
    @Transactional(readOnly = false)
    public void saveAudit(T entity) {
        // 设置意见
        entity.getAct().setComment((entity.getAct().getFlagBoolean() ?
                "[同意] ":"[驳回] ") + entity.getAct().getComment());
        Map<String, Object> vars = Maps.newHashMap();
        //
        processAudit(entity, vars);
        // 普通审批人：通过、驳回
        // 申请人：发起、销毁*
        String taskDefKey = entity.getAct().getTaskDefKey();
        // 把申请人重新发起、销毁流程特殊处理
        if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
            // 看看是不是想销毁表单
            if (entity.getAct().getFlagBoolean()) { // 申请人特有的comment
                // 必须保存业务数据，其它审批节点处的业务数据在子类中保存。
                save(entity);
                entity.getAct().setComment(
                                        (entity.getAct().getFlagBoolean()?"[重申] ":"[销毁] ")
                                                + entity.getAct().getComment() );
            } else { // 申请人特有的操作
                delete(entity);
                actTaskService.deleteProcIns(entity.getProcInsId(), "");
                return;
            }
        } // end if taskDefKey
        // 提交流程任务
        actTaskService.complateByAct(entity.getAct(), vars);
    }

    /**
     * 审批之前执行方法，子类实现
     * 主要用来在某个审批节点，判断业务数据-添加流程变量，保存comment到业务表。
     * 如果此审批单需要保存业务数据，请在此处实现。
     *
     */
    public void processAudit(T entity, Map<String, Object> vars) {

    }

    /**
     * 子类在启动流程之前，设置流程参数
     *
     * @param entity
     * @param vars
     */
    public void setupVariable(T entity, Map<String, Object> vars) {

    }
}
