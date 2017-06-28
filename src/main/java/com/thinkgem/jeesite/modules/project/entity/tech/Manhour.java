/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.tech;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 工时Entity
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
public class Manhour extends DataEntity<Manhour> {
	
	private static final long serialVersionUID = 1L;
	private Workorder workorder;		// 派工单编号
	private User engineer;		// 工程师编号
	private Date manhourDate;		// 工时日期
	private String manhour;		// 当日工时
	private String auditState;		// 审批状态(0：未审核；1：审核通过；2：审核未通过)；默认未审核
	private String auditOpinion;		// 审批意见
	private Date beginManhourDate;		// 开始 工时填报日期
	private Date endManhourDate;		// 结束 工时填报日期


	public Manhour() {
		super();
	}

	public Manhour(String id){
		super(id);
	}


	@NotNull(message = "派工单编号不能为空")
	public Workorder getWorkorder() {
		return workorder;
	}

	public void setWorkorder(Workorder workorder) {
		this.workorder = workorder;
	}

	@NotNull(message = "工程师编号不能为空")
	public User getEngineer() {
		return engineer;
	}

	public void setEngineer(User engineer) {
		this.engineer = engineer;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="工时填报日期不能为空")
	public Date getManhourDate() {
		return manhourDate;
	}

	public void setManhourDate(Date manhourDate) {
		this.manhourDate = manhourDate;
	}
	
	@Length(min=1, max=11, message="当日工时长度必须介于 1 和 11 之间")
	public String getManhour() {
		return manhour;
	}

	public void setManhour(String manhour) {
		this.manhour = manhour;
	}
	
	@Length(min=0, max=64, message="审批状态(0：未审核；1：审核通过；2：审核未通过)；默认未审核长度必须介于 0 和 64 之间")
	public String getAuditState() {
		return auditState;
	}

	public void setAuditState(String auditState) {
		this.auditState = auditState;
	}
	
	@Length(min=0, max=255, message="审批意见长度必须介于 0 和 255 之间")
	public String getAuditOpinion() {
		return auditOpinion;
	}

	public void setAuditOpinion(String auditOpinion) {
		this.auditOpinion = auditOpinion;
	}
	
	public Date getBeginManhourDate() {
		return beginManhourDate;
	}

	public void setBeginManhourDate(Date beginManhourDate) {
		this.beginManhourDate = beginManhourDate;
	}
	
	public Date getEndManhourDate() {
		return endManhourDate;
	}

	public void setEndManhourDate(Date endManhourDate) {
		this.endManhourDate = endManhourDate;
	}
}