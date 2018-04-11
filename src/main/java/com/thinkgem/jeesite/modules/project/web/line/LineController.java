package com.thinkgem.jeesite.modules.project.web.line;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.project.entity.line.Line;
import com.thinkgem.jeesite.modules.project.service.line.LineService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 条线Controller
 * @author rgz
 * @version 2018-04-10
 */
@Controller
@RequestMapping(value = "${adminPath}/project/line")
public class LineController extends BaseController {

	static String prefix = "modules/project/line/";
	static String vList = "lineList";
	static String vForm = "lineForm";


	@Autowired
	private LineService lineService;
	
	@ModelAttribute("line")
	public Line get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return lineService.get(id);
		}else{
			return new Line();
		}
	}

	// @RequiresPermissions("sys:area:view")
	@RequestMapping(value = {"list", ""})
	public String list(Line line, Model model) {
		model.addAttribute("list", lineService.findAll());
		// return "modules/sys/areaList";
		return prefix + vList;
	}

	// @RequiresPermissions("sys:area:view")
	@RequestMapping(value = "form")
	public String form(Line line, Model model) {
		if (line.getParent()==null||line.getParent().getId()==null){ // 如果没有父节点
			//todo rgz line.setParent(UserUtils.getUser().getOffice().getArea());

		} else {
			line.setParent(lineService.get(line.getParent().getId()));
		}


		model.addAttribute("line", line);
		// return "modules/sys/areaForm";
		return prefix + vForm;
	}
	
	@RequiresPermissions("sys:area:edit")
	@RequestMapping(value = "save")
	public String save(Line line, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/project/line";
		}
		if (!beanValidator(model, line)){
			return form(line, model);
		}
		lineService.save(line);
		addMessage(redirectAttributes, "保存条线'" + line.getName() + "'成功");
		return "redirect:" + adminPath + "/project/line/";
	}
	
	@RequiresPermissions("sys:area:edit")
	@RequestMapping(value = "delete")
	public String delete(Line line, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/project/line";
		}
//		if (Area.isRoot(id)){
//			addMessage(redirectAttributes, "删除区域失败, 不允许删除顶级区域或编号为空");
//		}else{
			lineService.delete(line);
			addMessage(redirectAttributes, "删除条线成功");
//		}
		return "redirect:" + adminPath + "/project/line/";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Line> list = lineService.findAll();
		for (int i=0; i<list.size(); i++){
			Line e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
}
