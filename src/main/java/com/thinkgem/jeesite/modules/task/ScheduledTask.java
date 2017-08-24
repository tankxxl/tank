package com.thinkgem.jeesite.modules.task;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.dao.external.ProjectApplyExternalDao;
import com.thinkgem.jeesite.modules.oa.entity.OaNotify;
import com.thinkgem.jeesite.modules.oa.entity.OaNotifyRecord;
import com.thinkgem.jeesite.modules.oa.service.OaNotifyService;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 定时任务不能是懒加载
 */
//@Service
// @Service
// @EnableScheduling
@Component
@Lazy(false)
public class ScheduledTask {
	
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
    ProjectApplyExternalDao applyExternalDao;

	@Autowired
    ProjectContractService contractService;

	@Autowired
    OaNotifyService notifyService;

//    private final Logger logger = Logger.getLogger(ServiceTask3.class.getName());

    @Loggable
    Logger logger;

    // @Scheduled(fixedDelay = 100)
    // 0 0 2 * * ?
    // 0 0 1 * * ?
    @Scheduled(cron="0 0 1 * * ? ")   // 每天凌晨1点执行一次
    public void pushQuestionna() {

        System.out.println("定时任务1，自动执行:" + format.format(new Date()));
        logger.info("定时任务1，自动执行:" + format.format(new Date()));

        applyExternalDao.updatePorCode("1");
    }

    @Scheduled(cron="0 0 1 * * ? ")   // 每天凌晨1点执行一次
    public void addToNotify() {

        contractService.findContractToNotify();

//        ProjectContract contract = new ProjectContract();
//        List<ProjectContract> contracts = contractService.findPreEndList(contract);
//        OaNotify notify;
//        List<OaNotifyRecord> oaNotifyRecordList;
//        OaNotifyRecord record;
//        String type;
//        List<User> userList = Collections.emptyList();
//
//        Role role = UserUtils.getRoleByEnname("usertask_specialist");
//        if (role != null && role.getUserList() != null && !role.getUserList().isEmpty()) {
//            userList.addAll(role.getUserList());
//        }
//        for (ProjectContract contract1 : contracts) {
//            System.out.println("addToNotify");
//            // 新建
//            notify = new OaNotify();
//            oaNotifyRecordList = Lists.newArrayList();
//            notify.setOaNotifyRecordList(oaNotifyRecordList);
//
//            // master
//            type = DictUtils.getDictValue("合同预警", "oa_notify_type", "4");
//            notify.setType(type);
//            String title = StringUtils.isEmpty(contract1.getContractCode()) ? contract1.getClientName() : contract1.getContractCode();
//            notify.setTitle(title);
//            notify.setContent(contract1.getApply().getProjectName()
//                    + "\n项目编号：" + contract1.getApply().getProjectCode()
//                    + "\n合同编号：" + contract1.getContractCode()
//                    + "\n客户名称：" + contract1.getClientName()
//                    + "\n合同到期日期：" + DateUtils.formatDateTime(contract1.getEndDate()));
//            notify.setStatus("1");
//            notify.setCreateBy(UserUtils.get("1"));
//
//            // slave 可以有多个接收人
//            record = new OaNotifyRecord();
//            record.setId(IdGen.uuid());
//            record.setOaNotify(notify);
//            record.setUser(contract1.getCreateBy());
//            record.setReadFlag("0");
//            oaNotifyRecordList.add(record);
//
//            for (User user : userList) {
//                record = new OaNotifyRecord();
//                record.setId(IdGen.uuid());
//                record.setOaNotify(notify);
//                record.setUser(user);
//                record.setReadFlag("0");
//                oaNotifyRecordList.add(record);
//            }
//            // 保存
//            notifyService.save(notify);
//        }
    }


    // @Scheduled(cron = "0 10-30 9 * * ?")
    public void pushQuestionnaire() {  
  
        System.out.println("定时任务1，自动执行:" + format.format(new Date()));
        logger.info("定时任务1，自动执行:" + format.format(new Date()));
    }  
  
    // @Scheduled(cron = "0 10-30 9 * * ?")
    public void pushQuestionnaire2() {  
  
        System.out.println(Thread.currentThread().getName() +
                "定时任务2，自动执行:" + format.format(new Date()));
        // logger.info("定时任务2，自动执行:" + format.format(new Date()));
    }  
    
    // @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
    public void pushQuestionnaire3() {
        // System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getName() +
                "定时任务3，自动执行:" + format.format(new Date()));
        // logger.info("定时任务3，自动执行:" + format.format(new Date()));
    }

    // @Scheduled(cron = "0 * * * * ?")
    public void run() {
//        logger.info("MyFirstSpringJob trigger...");
        logger.info("trigger...");

        /* 模拟此Job需耗时5秒 */
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    // @Scheduled(cron = "3 * * * * ?")
    public void run2() {
        logger.info("MySecondSpringJob trigger...");
    }
}
