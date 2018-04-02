package com.thinkgem.jeesite.modules.project.utils;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 本质是个bean，给ureport提供数据源，dataSource是由spring管理的
 */

@Component
public class TestBuildinDataSource implements BuildinDatasource {

    @Autowired
    private DataSource dataSource;

    @Override
    public String name() {
        return "内置数据源";
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
