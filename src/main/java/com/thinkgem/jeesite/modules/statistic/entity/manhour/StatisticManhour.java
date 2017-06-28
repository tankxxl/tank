/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.entity.manhour;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * 工时统计Entity
 * 
 * @author jicdata
 * @version 2016-03-15
 */
/**
 * @author Lu
 *
 */
public class StatisticManhour extends DataEntity<StatisticManhour> {

	private static final long serialVersionUID = 1L;
	private String projectId; // 项目id
	private String projectCode; // 项目code
	private String projectName; // 项目名称
	private String salerName; // 销售名称
	private String salerOfficeName; // 销售部门名称
	private Integer totalManhour4service; // 服务交互部工时
	private Double totalLabor4service; // 服务交互部人力
	private Integer totalManhour4software; // 软件工时
	private Double totalLabor4software; // 软件人力
	private Integer totalManhour4scheme; // 解决方案部工时
	private Double totalLabor4scheme; // 解决方案部人力
	
	/**
	 * 总工时、工人力  这两个变量的get方法中 累加了。 服务、软件、方案 3个部门产生的工时、费用
	 */
	private Integer totalManhour; // 总工时
	private String totalLabor; // 总人力
	
	/**
	 * 下面2行作为查询参数
	 * startTime、endTime 表示查询 项目在 具体时间段内工时统计
	 */
	private Date startTime;	
	private Date endTime;
	
	private String tectechnicalDepartmentId;//技术部门id（为空表示查询 项目的 服务、软件、解决方案3个的全部信息） 若不为空，表示查询的只是该技术部门的数据

	public StatisticManhour() {
		super();
	}

	public StatisticManhour(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "项目id长度必须介于 1 和 64 之间")
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Length(min = 0, max = 64, message = "项目code长度必须介于 0 和 64 之间")
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	@Length(min = 0, max = 64, message = "项目名称长度必须介于 0 和 64 之间")
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getSalerName() {
		return salerName;
	}

	public void setSalerName(String salerName) {
		this.salerName = salerName;
	}


	public String getSalerOfficeName() {
		return salerOfficeName;
	}

	public void setSalerOfficeName(String salerOfficeName) {
		this.salerOfficeName = salerOfficeName;
	}

	public Integer getTotalManhour4service() {
		return totalManhour4service;
	}

	public void setTotalManhour4service(Integer totalManhour4service) {
		this.totalManhour4service = totalManhour4service;
	}

	public Double getTotalLabor4service() {
		return totalLabor4service;
	}

	public void setTotalLabor4service(Double totalLabor4service) {
		this.totalLabor4service = totalLabor4service;
	}

	public Integer getTotalManhour4software() {
		return totalManhour4software;
	}

	public void setTotalManhour4software(Integer totalManhour4software) {
		this.totalManhour4software = totalManhour4software;
	}

	public Double getTotalLabor4software() {
		return totalLabor4software;
	}

	public void setTotalLabor4software(Double totalLabor4software) {
		this.totalLabor4software = totalLabor4software;
	}

	public Integer getTotalManhour4scheme() {
		return totalManhour4scheme;
	}

	public void setTotalManhour4scheme(Integer totalManhour4scheme) {
		this.totalManhour4scheme = totalManhour4scheme;
	}

	public Double getTotalLabor4scheme() {
		return totalLabor4scheme;
	}

	public void setTotalLabor4scheme(Double totalLabor4scheme) {
		this.totalLabor4scheme = totalLabor4scheme;
	}

	public Integer getTotalManhour() {
		int tempTotalManhour =0;
		if(totalManhour4service != null){
			tempTotalManhour +=totalManhour4service;
		}
		if(totalManhour4software != null){
			tempTotalManhour +=totalManhour4software;
		}
		if(totalManhour4scheme != null){
			tempTotalManhour +=totalManhour4scheme;
		}
		return tempTotalManhour;
	}

	public void setTotalManhour(Integer totalManhour) {
		this.totalManhour = totalManhour;
	}

	public String getTotalLabor() {
		DecimalFormat df=new DecimalFormat(".##");
		double tempTotalLabor =0;
		if(totalLabor4service !=null){
			tempTotalLabor+=totalLabor4service;
		}
		if(totalLabor4software !=null){
			tempTotalLabor+=totalLabor4software;
		}
		if(totalLabor4scheme !=null){
			tempTotalLabor+=totalLabor4scheme;
		}
		
		return df.format(tempTotalLabor);
	}

	public void setTotalLabor(String totalLabor) {
		this.totalLabor = totalLabor;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getStartTime() {
		return startTime;
	}

	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTectechnicalDepartmentId() {
		return tectechnicalDepartmentId;
	}

	public void setTectechnicalDepartmentId(String tectechnicalDepartmentId) {
		this.tectechnicalDepartmentId = tectechnicalDepartmentId;
	}

	
}