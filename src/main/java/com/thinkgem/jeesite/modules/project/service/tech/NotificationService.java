/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.tech;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.project.dao.tech.NotificationDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Assigning;
import com.thinkgem.jeesite.modules.project.entity.tech.Notification;
import com.thinkgem.jeesite.modules.project.entity.tech.Workorder;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 知会Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class NotificationService extends CrudService<NotificationDao, Notification> {

	@Autowired
	private AssigningService assigningService;
	@Autowired
	private WorkorderService workorderService;

	public Notification get(String id) {
		return super.get(id);
	}
	
	public List<Notification> findList(Notification notification) {
		return super.findList(notification);
	}
	
	public Page<Notification> findPage(Page<Notification> page, Notification notification) {
		return super.findPage(page, notification);
	}
	
	@Transactional(readOnly = false)
	public void save(Notification notification) {
		super.save(notification);
	}
	
	@Transactional(readOnly = false)
	public void delete(Notification notification) {
		super.delete(notification);
	}

	/**
	 * 保存知会；同时设置工期完成
	 * @param notification
	 */
	@Transactional(readOnly = false)
	public void saveNotification(Notification notification) {
		Assigning assigning = assigningService.get(notification.getAssigning().getId());
		Workorder workorder = new Workorder(assigning);
		List<Workorder> workorderList = workorderService.findList(workorder);
		String completedValue = DictUtils.getDictValue("已完成", "workorder_completed", "1");
		assigning.setCompleted(completedValue);
		assigningService.save(assigning);
		for (Workorder tempWorkorder: workorderList) {
			tempWorkorder.setCompleted(completedValue);
			workorderService.save(tempWorkorder);
		}
		save(notification);
	}
}