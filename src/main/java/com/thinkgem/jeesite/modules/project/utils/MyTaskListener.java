package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.utils.Collections3;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.Soundbank;
import java.util.*;

/**
 * 当流程运转到usertask2我们看一下程序的输出：
 * assignment========
 * create=========
 * 因为usertask2节点配置了处理人所以触发assignment事件监听，当任务运转到usertask2的时候触发了create事件
 * 这里我们也可以得出一个结论：assignment事件比create先执行
 * 使用代码结束任务，代码如下：
 * String taskId="127515";
 * demo.getTaskService().complete(taskId);
 * 当我们结束usertask2我们看一下程序的输出：
 * complete===========
 * delete=============
 * 任务完成的时候，触发complete事件，因为任务完成之后，要从ACT_RU_TASK中删除这条记录，所以触发delete事件
 */
public class MyTaskListener extends DefaultTaskListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        System.out.println();
        // 增加多个审批人
        // 如果指定了办理人assignee，则此处增加的候选人无效
        delegateTask.addCandidateUser("thinkgem");
        // or
        // delegateTask.addCandidateUsers(list);
        // 指定任务的办理人
        // delegateTask.setAssignee("张一");

        sendEmail(delegateTask);
    }

    @Override
    public void onAssignment(DelegateTask delegateTask) throws Exception {
        System.out.println();
    }

    @Override
    public void onComplete(DelegateTask delegateTask) throws Exception {
        // 实际审批人
        delegateTask.getAssignee();
        System.out.println();
    }

    @Override
    public void onDelete(DelegateTask delegateTask) throws Exception {
        System.out.println();
    }

    private void sendEmail(DelegateTask delegateTask) {

        ProjectApplyExternalService applyService =
                SpringContextHolder.getBean(ProjectApplyExternalService.class);

        applyService.sendMail(delegateTask );
    }
}
