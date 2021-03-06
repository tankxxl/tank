package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.service.approval.AssigneeService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.midi.Soundbank;


public class ActivitiTest2 extends BaseTestCase {

//	@Autowired
//	MailService mailService;

//	@Autowired
//	OfficeService officeService;

    @Autowired
    AssigneeService assigneeService;

    @Test
    public void testLeader() throws Exception {

        String leader = assigneeService.findLeader("shanglifang");
        System.out.println("leader=" + leader);

    }


    @Test
	public void testInfo() throws Exception {
		User user = UserUtils.getByLoginName("chenxiangli");
		System.out.println(user.getOffice().getPrimaryPerson().getLoginName());
	}

	@Test
	public void testDeploy() throws Exception {

//		Email email = new Email();
//		email.setAddressee("rgz03407@163.com;tank2xxl@gmail.com");
//		email.setSubject("项目审批结束2");
//		email.setContent("项目编号：" + "asdfas" + "已立项，请尽快录入ERP。");
//		mailService.sendMailBySynchronizationMode(email);
//		System.out.println("activiti");

	}

	@Test
	public void testOffice() {
//		ProjectApplyExternalService projectApplyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);
//		Office office = new Office();
//		office.setName("软件开发部");
//		office = officeService.get(office);
//		User user = UserUtils.get(office.getPrimaryPerson().getId());
//		System.out.println(user.getLoginName());
	}

	@Override
	protected void beforeTest() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void afterTest() throws Exception {
		// TODO Auto-generated method stub

	}
}
