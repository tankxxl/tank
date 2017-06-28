package com.thinkgem.jeesite.test;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring-context*.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
@Transactional
public abstract class BaseTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	@Before
    public void initialize() throws Exception {
        beforeTest();
    }

    @After
    public void clean() throws Exception {
        afterTest();
    }

    protected abstract void beforeTest() throws Exception;

    protected abstract void afterTest() throws Exception;
}
