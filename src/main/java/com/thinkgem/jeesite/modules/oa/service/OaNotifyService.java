/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.oa.service;

import java.util.Date;
import java.util.List;

import com.thinkgem.jeesite.modules.mail.service.MailService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.oa.entity.OaNotify;
import com.thinkgem.jeesite.modules.oa.entity.OaNotifyRecord;
import com.thinkgem.jeesite.modules.oa.dao.OaNotifyDao;
import com.thinkgem.jeesite.modules.oa.dao.OaNotifyRecordDao;

/**
 * 通知通告Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OaNotifyService extends CrudService<OaNotifyDao, OaNotify> {

	@Autowired
	private OaNotifyRecordDao oaNotifyRecordDao;

	@Autowired
	private MailService mailService;

	public OaNotify get(String id) {
		OaNotify entity = dao.get(id);
		return entity;
	}
	
	/**
	 * 获取通知发送记录
	 * @param oaNotify
	 * @return
	 */
	public OaNotify getRecordList(OaNotify oaNotify) {
		oaNotify.setOaNotifyRecordList(oaNotifyRecordDao.findList(new OaNotifyRecord(oaNotify)));
		return oaNotify;
	}
	
	public Page<OaNotify> find(Page<OaNotify> page, OaNotify oaNotify) {
		oaNotify.setPage(page);
		page.setList(dao.findList(oaNotify));
		return page;
	}
	
	/**
	 * 获取通知数目
	 * @param oaNotify
	 * @return
	 */
	public Long findCount(OaNotify oaNotify) {
		return dao.findCount(oaNotify);
	}
	
	@Transactional(readOnly = false)
	public void save(OaNotify oaNotify) {
		super.save(oaNotify);
		
		// 更新发送接受人记录
		oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
		if (oaNotify.getOaNotifyRecordList().size() > 0){
			oaNotifyRecordDao.insertAll(oaNotify.getOaNotifyRecordList());
		}
	}

	// rgz 只保存notify主表
	@Transactional(readOnly = false)
	public void sendNotifyEmail(OaNotify oaNotify) {
		super.save(oaNotify);

		oaNotify = getRecordList(oaNotify);
		mailService.sendNotifyEmail(oaNotify);

		// 更新发送接受人记录
		// oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
		// if (oaNotify.getOaNotifyRecordList().size() > 0){
		// 	oaNotifyRecordDao.insertAll(oaNotify.getOaNotifyRecordList());
		// }
	}
	
	/**
	 * 更新阅读状态
	 */
	@Transactional(readOnly = false)
	public void updateReadFlag(OaNotify oaNotify) {
		OaNotifyRecord oaNotifyRecord = new OaNotifyRecord(oaNotify);
		oaNotifyRecord.setUser(oaNotifyRecord.getCurrentUser());
		oaNotifyRecord.setReadDate(new Date());
		oaNotifyRecord.setReadFlag("1");
		oaNotifyRecordDao.update(oaNotifyRecord);
	}

	/**
	 * rgz
	 * 根据消息类型 删除通知数据 及子表数据
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void deleteByType(OaNotify entity) {

		List<OaNotify> oaNotifyList = dao.findList(entity);
		if (oaNotifyList == null || oaNotifyList.isEmpty()) {
			return;
		}
		// 先删除子表
		for (OaNotify oaNotify : oaNotifyList) {
			oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
		}
		// 再删除主表
		dao.deleteByType(entity);
	}
}