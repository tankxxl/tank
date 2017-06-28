package com.thinkgem.jeesite.modules.project.utils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class ServiceTaskDelegate implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("Executed process with key "+
				execution.getProcessBusinessKey() + 
				" with process definition Id " + 
				execution.getProcessDefinitionId() + 
				" with process instance Id " + execution.getProcessInstanceId()+
				" and current task name is " + 
				execution.getCurrentActivityName());
		
	}

}
