package com.thinkgem.jeesite.modules.test.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

// @RunWith(SpringJUnit4ClassRunner.class)
// @SpringApplicationConfiguration(
// 		classes = { TestMultiInstance.MultiInstanceTestActivitiConfig.class},
// 		locations="org.kritek.activiti.test.multiInstance")
public class TestMultiInstance {

	@Configuration
	public static class MultiInstanceTestActivitiConfig {
		@Bean
		public DataSource dataSource() {
			BasicDataSource basicDS = new BasicDataSource();
			basicDS.setUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000");
			basicDS.setDriverClassName("org.h2.Driver");
			basicDS.setUsername("sa");
			basicDS.setPassword(null);
			return basicDS;
		}

		@Bean()
		public ProcessEngineFactoryBean processEngine(DataSource ds) {
			SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
			configuration.setDatabaseSchemaUpdate("true");
			configuration.setHistory(HistoryLevel.AUDIT.getKey());
			configuration.setJobExecutorActivate(true);
			configuration.setDataSource(ds);
			configuration.setTransactionManager(new DataSourceTransactionManager(ds));

			ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
			factoryBean.setProcessEngineConfiguration(configuration);

			return factoryBean;
		}

		@Bean
		public PeopleService peopleService() {
			return new PeopleService();
		}
	}

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private PeopleService peopleService;

	@Test
	public void testSimpleMultiInstanceProcess() throws Exception {
		this.deployProcessDefinition( "myMultiInstanceProcess.bpmn20.xml",
						"org/kritek/activiti/test/multiInstance/MyMultiInstanceProcess.bpmn");


		String location = "US";
		int numPeopleAtLocation = peopleService.getPeopleByLocation(location).size();

		HashMap<String, Object> vars = new HashMap<String, Object>();
		vars.put("location", location);

		processEngine.getRuntimeService().startProcessInstanceByKey("myMultiInstanceProcess", vars);

		int numPeopleLoggedInScript = LogCallsToScriptTask.peopleLogged.size();
		assertEquals("Scripts should have logged "+numPeopleAtLocation+"times.", numPeopleAtLocation,
				numPeopleLoggedInScript);

		assertEquals("PeopleService called more times than expected for location",
				1, PeopleService.callCount.get());

	}

	public ProcessDefinition getProcessDefinition(String processDefinitionName) {
		ProcessDefinition processDefinition = processEngine.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionName).singleResult();
		return processDefinition;
	}

	public Deployment deployProcessDefinition(
			String processDefinition, String bpmnFileName)
			throws FileNotFoundException {
		// This loads the bpmn.xml file from the classpath
		InputStream deploymentFile = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(bpmnFileName);

		Deployment deployment = processEngine.getRepositoryService().createDeployment()
				.addInputStream(processDefinition, deploymentFile).deploy();

		return deployment;
	}

}
