package com.thinkgem.jeesite.modules.project.service.line;

import com.thinkgem.jeesite.common.service.TreeService;
import com.thinkgem.jeesite.modules.project.dao.line.LineDao;
import com.thinkgem.jeesite.modules.project.entity.line.Line;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 条线Service
 * @author rgz
 * @version 2018-04-10
 */
@Service
@Transactional(readOnly = true)
public class LineService extends TreeService<LineDao, Line> {

	public List<Line> findAll(){
		return dao.findAllList(new Line());
	}

	@Transactional(readOnly = false)
	public void save(Line line) {
		super.save(line);
		// UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Line line) {
		super.delete(line);
		// UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
}
