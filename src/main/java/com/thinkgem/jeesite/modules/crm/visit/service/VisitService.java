package com.thinkgem.jeesite.modules.crm.visit.service;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.crm.client.service.ClientCostService;
import com.thinkgem.jeesite.modules.crm.visit.dao.ClientVisitDao;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientCost;
import com.thinkgem.jeesite.modules.crm.visit.entity.ClientVisit;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientContact;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 拜访记录 Service
 * @author jicdata
 * @version 2016-02-21
 */
@Service
@Transactional(readOnly = true)
public class VisitService extends CrudService<ClientVisitDao, ClientVisit> {

	@Autowired
	private ClientCostService costService;

	@Override
	public ClientVisit get(String id) {
		ClientVisit visit = super.get(id);
		if (visit == null)
			return visit;
		visit.setCostList(costService.findList(new ClientCost(visit)));
		return visit;
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(ClientVisit visit) {
		super.save(visit);
		User user = UserUtils.get("1");

		for (ClientCost cost : visit.getCostList()) {
			if (ClientContact.DEL_FLAG_NORMAL.equals(cost.getDelFlag())) {
				if (StringUtils.isBlank(cost.getId())) {
					cost.setCreateBy(user);
					cost.setUpdateBy(user);
				}
				cost.setClient(visit.getClient());
				cost.setContact(visit.getContact());

				costService.save(cost);
			}else{
				costService.delete(cost);
			}
		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ClientVisit visit) {
		super.delete(visit);
		costService.delete(new ClientCost(visit));
	}
	
	public ClientVisit getByName(String customerName) {
		ClientVisit client = new ClientVisit();
		// client.setCustomerName(customerName);
		// client = dao.getByName(client);
		if (client == null){
			return null;
		}
		return client;
	}
	
}