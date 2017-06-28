/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.tech;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.project.entity.tech.*;
import com.thinkgem.jeesite.modules.project.service.tech.AssigningService;
import com.thinkgem.jeesite.modules.project.service.tech.ManhourService;
import com.thinkgem.jeesite.modules.project.service.tech.TechapplyService;
import com.thinkgem.jeesite.modules.project.service.tech.WorkorderService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 工时填报/审批Controller
 *
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Controller
@RequestMapping(value = "${adminPath}/project/tech/manhour")
public class ManhourController extends BaseController {

    @Autowired
    private ManhourService manhourService;
    @Autowired
    private WorkorderService workorderService;
    @Autowired
    private AssigningService assigningService;
    @Autowired
    private TechapplyService techapplyService;

    @ModelAttribute
    public Manhour get(@RequestParam(required = false) String id) {
        Manhour entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = manhourService.get(id);
        }
        if (entity == null) {
            entity = new Manhour();
        }
        return entity;
    }

    @RequiresPermissions("project:tech:manhour:view")
    @RequestMapping(value = {"list", ""})
    public String list(Manhour manhour, HttpServletRequest request, HttpServletResponse response, Model model) {
        manhour.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "so", "su"));
        Page<Manhour> page = manhourService.findPage(new Page<Manhour>(request, response), manhour);
        model.addAttribute("page", page);
        return "modules/project/tech/manhourList";
    }

    /**
     * 工时查看/审核
     *
     * @param manhour
     * @param model
     * @return
     */
    @RequiresPermissions("project:tech:manhour:view")
    @RequestMapping(value = "form")
    public String form(Manhour manhour, Model model) {
        model.addAttribute("manhour", manhour);
        Workorder workorder = workorderService.get(manhour.getWorkorder().getId());
        Assigning assigning = assigningService.get(workorder.getAssigning().getId());
        Techapply techapply = techapplyService.get(assigning.getTechapply().getId());
        model.addAttribute("workorder", workorder);
        model.addAttribute("assigning", assigning);
        model.addAttribute("techapply", techapply);
        return "modules/project/tech/manhourForm";
    }

    /**
     * 工时填报页面
     *
     * @param model
     * @return
     */
    @RequiresPermissions("project:tech:manhour:view")
    @RequestMapping(value = "form4FillInManhours")
    public String form4FillInManhours(Model model, RedirectAttributes redirectAttributes) throws ParseException {
        DateUtils.getDate("yyyy-MM-dd");
        Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(DateUtils.getDate("yyyy-MM-dd"));
        //Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-15");
        return form4FillInManhours(model, redirectAttributes, currentDate);
    }

    /**
     * 根据参数日期获取包含在内的一周的工时填报情况
     *
     * @param paramDate
     * @return
     */
    @RequestMapping(value = "regetform4FillInManhours")
    public String regetform4FillInManhours(Model model, RedirectAttributes redirectAttributes,
                                           @RequestParam(required = true) Date paramDate) throws ParseException {
        return form4FillInManhours(model, redirectAttributes, paramDate);
    }

    private String form4FillInManhours(Model model, RedirectAttributes redirectAttributes, Date currentDate) throws ParseException {
        model.addAttribute("currentDate", DateUtils.formatDate(currentDate, "yyyy-MM-dd"));
        Workorder workorder = new Workorder();
        workorder.setCompleted(DictUtils.getDictValue("未完成", "workorder_completed", "0"));
        workorder.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s", "u3"));
        List<Workorder> workorderList = workorderService.findList(workorder);
        model.addAttribute("workorderList", workorderList);
        WorkorderManhourArray workorderManhourArray = getWorkorderManhourArray(workorderList, currentDate);
        model.addAttribute("workorderManhourArray", workorderManhourArray);
        if (null == workorderManhourArray) {
            addMessage(redirectAttributes, "暂无工时可填");
            return "redirect:" + Global.getAdminPath() + "/project/tech/manhour/?repage";
        }
        return "modules/project/tech/manhourFillInForm";
    }

    private WorkorderManhourArray getWorkorderManhourArray(List<Workorder> workorderList, Date paramDate) {
        WorkorderManhourArray workorderManhourArray = new WorkorderManhourArray();
        if (0 == workorderList.size()) {
            return null;
        }
        Manhour[][] retArray = new Manhour[workorderList.size()][];
        for (int index = 0; index < workorderList.size(); ++index) {
            Manhour manhour = new Manhour();
            Workorder workorder = workorderList.get(index);
            manhour.setWorkorder(workorder);
            List<Manhour> manhourList = manhourService.findList4FillIn(manhour, paramDate);
            Manhour[] tempArray = new Manhour[manhourList.size()];
            manhourList.toArray(tempArray);
            retArray[index] = tempArray;
        }
        workorderManhourArray.setWorkorderManhourArray(retArray);
        return workorderManhourArray;
    }

    /**
     * 工时修改/工时单条审批
     *
     * @param manhour
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions(value = {"project:tech:manhour:edit", "project:tech:manhour:audit"}, logical = Logical.OR)
    @RequestMapping(value = "update")
    public String update(Manhour manhour, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, manhour)) {
            return form(manhour, model);
        }
        manhourService.save(manhour);
        addMessage(redirectAttributes, "操作成功");
        return "redirect:" + Global.getAdminPath() + "/project/tech/manhour/?repage";
    }

    /**
     * 工时填报
     *
     * @param workorderManhourArray
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("project:tech:manhour:edit")
    @RequestMapping(value = "save")
    public String save(WorkorderManhourArray workorderManhourArray,
                       Model model, RedirectAttributes redirectAttributes) throws ParseException {
        if (!beanValidator(model, workorderManhourArray)) {
            return form4FillInManhours(model, redirectAttributes);
        }
        manhourService.saveInPatch(workorderManhourArray);
        addMessage(redirectAttributes, "工时填报成功");
        return "redirect:" + Global.getAdminPath() + "/project/tech/manhour/?repage";
    }

    /**
     * 工时批量审批
     * @param ids 工时单编号串； 以“,”分隔
     * @param auditState 审批结果
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("project:tech:manhour:audit")
    @RequestMapping(value = "auditInPatch")
    public String auditInPatch(@RequestParam(required = true) String ids, @RequestParam(required = true) String auditState,
                               RedirectAttributes redirectAttributes) {
        manhourService.save4AuditInPatch(ids, auditState);
        addMessage(redirectAttributes, "批量审批成功");
        return "redirect:" + Global.getAdminPath() + "/project/tech/manhour/?repage";
    }
}