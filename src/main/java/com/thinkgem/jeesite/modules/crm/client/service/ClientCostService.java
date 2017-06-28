package com.thinkgem.jeesite.modules.crm.client.service;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.crm.client.dao.ClientCostDao;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientCost;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 拜访记录-花费 Service
 * @author jicdata
 * @version 2016-02-21
 */
@Service
@Transactional(readOnly = true)
public class ClientCostService extends CrudService<ClientCostDao, ClientCost> {

}