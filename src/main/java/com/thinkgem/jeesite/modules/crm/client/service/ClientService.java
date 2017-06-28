package com.thinkgem.jeesite.modules.crm.client.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.crm.client.dao.ClientContactDao;
import com.thinkgem.jeesite.modules.crm.client.dao.ClientDao;
import com.thinkgem.jeesite.modules.crm.client.entity.Client;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientContact;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户联系人信息Service
 * @author jicdata
 * @version 2016-02-21
 */
@Service
@Transactional(readOnly = true)
public class ClientService extends CrudService<ClientDao, Client> {

	@Autowired
	private ClientContactDao customerContactDao;
	
	
	public ClientContact getCustomerConcat(String customerConcatId){
		return customerContactDao.get(customerConcatId);
	}
	
	
	@Override
	public Client get(String id) {
		Client client = super.get(id);
		if (client == null)
			return client;
		client.setCustomerContactList(customerContactDao.findList(new ClientContact(client)));
		return client;
	}
	
	@Override
	public List<Client> findList(Client client) {
		return super.findList(client);
	}
	
	public List<ClientContact> findContatList(Client client) {
		return customerContactDao.findList(new ClientContact(client));
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(Client client) {
		super.save(client);
		User user = UserUtils.get("1");
		for (ClientContact customerContact : client.getCustomerContactList()){
			if (ClientContact.DEL_FLAG_NORMAL.equals(customerContact.getDelFlag())){
				if (StringUtils.isBlank(customerContact.getId())){
					customerContact.setClient(client);
					customerContact.preInsert();
					customerContact.setCreateBy(user);
					customerContact.setUpdateBy(user);
					customerContactDao.insert(customerContact);
				}else{
					customerContact.preUpdate();
					customerContactDao.update(customerContact);
				}
			}else{
				customerContactDao.delete(customerContact);
			}
		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(Client client) {
		super.delete(client);
		customerContactDao.delete(new ClientContact(client));
	}
	
	public Client getByName(String customerName) {
		Client client = new Client();
		client.setCustomerName(customerName);
		client = dao.getByName(client);
		if (client == null){
			return null;
		}
		return client;
	}
	
}