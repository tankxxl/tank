/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.tech;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.project.dao.tech.ManhourDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Manhour;
import com.thinkgem.jeesite.modules.project.entity.tech.WorkorderManhourArray;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 工时Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class ManhourService extends CrudService<ManhourDao, Manhour> {

	@Autowired
	private ManhourDao manhourDao;

	public Manhour get(String id) {
		return super.get(id);
	}
	
	public List<Manhour> findList(Manhour manhour) {
		return super.findList(manhour);
	}
	
	public Page<Manhour> findPage(Page<Manhour> page, Manhour manhour) {
		return super.findPage(page, manhour);
	}
	
	@Transactional(readOnly = false)
	public void save(Manhour manhour) {
		super.save(manhour);
	}
	
	@Transactional(readOnly = false)
	public void delete(Manhour manhour) {
		super.delete(manhour);
	}
	
	/**
	 * 真的从数据库移除
	 * @param manhour
	 */
	@Transactional(readOnly = false)
	public void delete4Real(Manhour manhour) {
		dao.delete4Real(manhour);
	}

	
	/**
	 * 执行存储过程生成临时日期表
	 * @param paramMap
	 */
	@Transactional(readOnly = false)
	public void execuDateGenProc(HashMap<String, Object> paramMap) {
		manhourDao.execuDateGenProc(paramMap);
	}

	/**
	 * 查询工单-工时二维数组数据
	 * @param manhour
	 */
	@Transactional(readOnly = false)
	public List<Manhour> findList4FillIn(Manhour manhour, Date paramDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(paramDate);
		int paramDateDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		calendar.add(Calendar.DAY_OF_WEEK, 0 - paramDateDayOfWeek);
		Date startDate = calendar.getTime();
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("paramDate", startDate);
		execuDateGenProc(paramMap);
		return findList4FillIn(manhour);
	}

	/**
	 * 查询工单-工时二维数据数据
	 * @param manhour
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<Manhour> findList4FillIn(Manhour manhour) {
		return manhourDao.findList4FillIn(manhour);
	}

	/**
	 * 批量保存工时记录
	 * @param workorderManhourArray
	 */
	@Transactional(readOnly = false)
	public void saveInPatch(WorkorderManhourArray workorderManhourArray) {
		for (Manhour[] manhourArray : workorderManhourArray.getWorkorderManhourArray()) {
			for (Manhour manhour : manhourArray) {
				if(manhour.getAuditState().equals(DictUtils.getDictValue("审核通过", "audit_state", "1"))){
					continue;
				}else if (manhour.getAuditState().equals(DictUtils.getDictValue("审批未通过", "audit_state", "2"))) {
					manhour.setAuditState(DictUtils.getDictValue("未审批", "audit_state", "0"));
				}
				if(StringUtils.isBlank(manhour.getManhour())){
					if(StringUtils.isNotBlank(manhour.getId())){
						delete4Real(manhour);
					}else{
						continue;
					}
					
				}
				save(manhour);
			}
		}
	}

	/**
	 * 批量审批工时记录
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void save4AuditInPatch(String ids, String auditState) {
		String[] idArray = ids.split(",");
		for (String id: idArray) {
			Manhour manhour = get(id);
			manhour.setAuditState(auditState);
			save(manhour);
		}
	}
}