/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.persistence.JicDao;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    // 审批流程
    public void saveAuditBase(ActEntity actEntity, Map<String, Object> vars) {
        // 如果业务数据没有变化，在此处的保存多余。
        save((T) actEntity); // 更新一下记录，有些审批节点有多个数据需要填写
        //
        actTaskService.complateByAct(actEntity.getAct(), vars);
    }

    // 启动流程
    // 来源：1、发起人新建流程。2、发起人修改表单后，重新发起流程。
    public String launchWorkflowBase(ActEntity actEntity,
                   boolean isNewRecord,
                   String title,
                   Map<String, Object> vars) {

        if (isNewRecord) {  // 新建申请单

            vars.put(ActUtils.VAR_OBJ_ID, actEntity.getId());

            return actTaskService.startProcEatFirstTask(
                    actEntity,
                    title,
                    vars
            );

        } else {  // 修改申请单
            actEntity.getAct().setComment(
                    (actEntity.getAct().getFlagBoolean()?"[重申] ":"[销毁] ")
                            + actEntity.getAct().getComment()
            );
            // 前端传过来的值
            if (actEntity.getAct().getFlagBoolean()) { // 重新提交申请单
                // 完成流程任务
                vars.put(ActUtils.VAR_PASS, actEntity.getAct().getFlagNumber());
                actTaskService.complateByAct(actEntity.getAct(), vars);
            } else {
                delete((T) actEntity);
                actTaskService.deleteProcIns(actEntity.getProcInsId(), "发起人主动销毁");
            }

            return actEntity.getProcInsId();
        }
    }

    public void endProcess(String procInsId) {
        actTaskService.deleteProcIns(procInsId, "");
    }



}
