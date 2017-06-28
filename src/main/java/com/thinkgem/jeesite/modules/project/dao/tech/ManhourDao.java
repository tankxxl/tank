/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.dao.tech;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Manhour;

import java.util.HashMap;
import java.util.List;

/**
 * 工时DAO接口
 * @author jicdata
 * @version 2016-03-11
 */
@MyBatisDao
public interface ManhourDao extends CrudDao<Manhour> {

    /**
     * 执行存储过程生成临时日期表
     * @param paramMap
     */
    void execuDateGenProc(HashMap<String, Object> paramMap);

    /**
     * 查询工单-工时二维数组数据
     * @param manhour
     */
    List<Manhour> findList4FillIn(Manhour manhour);
    
    
    /**
	 * 删除数据 真的从数据库移除
	 * @param entity
	 * @return
	 */
	public int delete4Real(Manhour manhour);
}