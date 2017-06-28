package com.thinkgem.jeesite.modules.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.logging.Logger;

public class ServiceTask2 implements JavaDelegate {

	private final Logger logger = Logger.getLogger(ServiceTask2.class.getName());
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Thread.sleep(10000);
		
		logger.info("variables=" + execution.getVariables());
		execution.setVariable("task2", "I am task 2");
		logger.info("I am task 2.");	
	}

}
