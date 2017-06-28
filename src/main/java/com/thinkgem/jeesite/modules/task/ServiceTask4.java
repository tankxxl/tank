package com.thinkgem.jeesite.modules.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.logging.Logger;

public class ServiceTask4 implements JavaDelegate {

	private final Logger logger = Logger.getLogger(ServiceTask4.class.getName());
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Thread.sleep(10000);
		
		logger.info("variables=" + execution.getVariables());
		execution.setVariable("task4", "I am task 4");
		logger.info("I am task 4.");	
	}

}
