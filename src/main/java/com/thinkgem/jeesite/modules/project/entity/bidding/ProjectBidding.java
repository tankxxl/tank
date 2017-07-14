/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.bidding;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.hibernate.validator.constraints.Length;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.List;

/**
 * 项目投标Entity
 * @author jicdata
 * @version 2016-03-08
 */
public class ProjectBidding extends ActEntity<ProjectBidding> {
	
	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal apply;		// 外部项目立项编号
	private String tenderer;		// 招标方
	private String category;		// 标书种类
	private String price;		// 标书购买价
	private String printingPaste;		// 用印内容
	private String content;		// 投标内容与立项内容偏差说明
    private String amount; // 投标金额(元)
    private String grossMargin; // 投标毛利(元)
	private String profitMargin;		// 毛利率
	private String profitMarginFile;		// 毛利说明文件
	
	private String outsourcing; // 是否有外包，0：没有；1：有

    private String result; // 投标结果

	private Date issueDate; // 北京科技-发标日期
	private Date biddingDate; // 北京科技-投标日期
	private Date openDate; // 北京科技-开标日期
	private String biddingMembers; // 北京科技-投标参与人员-多选-本公司的人
	private String tenderee; // 北京科技-招标单位
	private String tendereeInfo; // 北京科技-招标方情况
	private String judgeInfo; // 北京科技-评委情况
	private String cobidderInfo; // 北京科技-参标公司情况
	private String prepare; // 北京科技-投标前期准备工作内容
	private String biddingPrice; //  北京科技-投标价格
	private String biddingInfo; //  北京科技-投标主要内容
	private String finalPrice; // 北京科技-最终价格
	private String finalInfo; // 北京科技-修改内容
	private String biddingIssue; // 北京科技-投标过程中的问题
	private String winFlag; // 北京科技-是否中标
	private String archiveFlag; // 北京科技-是否归档
	private String alterFlag; // 北京科技-投标前后是否有变化
	private String lostInfo; // 北京科技-丟标分析
	private String membersName; // 合成字段，用于前台展示，数据来源于数据库

	private String ifb; // 邀标函文件
	private String negotiationFile; // 含投标价格的谈判文件
	private String priceAccountingFile; // 价格核算表

	public ProjectBidding() {
		super();
	}

	public ProjectBidding(String id){
		super(id);
	}

	
	
	public ProjectApplyExternal getApply() {
		return apply;
	}

	public void setApply(ProjectApplyExternal apply) {
		this.apply = apply;
	}
	

	@Length(min=0, max=255, message="招标方长度必须介于 0 和 255 之间")
	public String getTenderer() {
		return tenderer;
	}

	public void setTenderer(String tenderer) {
		this.tenderer = tenderer;
	}
	
	@Length(min=0, max=50, message="标书种类长度必须介于 0 和 50 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Length(min=0, max=64, message="标书购买价长度必须介于 0 和 64 之间")
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	@Length(min=0, max=50, message="用印内容长度必须介于 0 和 50 之间")
	public String getPrintingPaste() {
		return printingPaste;
	}

	public void setPrintingPaste(String printingPaste) {
		this.printingPaste = printingPaste;
	}
	
	@Length(min=0, max=2000, message="投标内容与立项内容偏差说明长度必须介于 0 和 2000 之间")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(String grossMargin) {
        this.grossMargin = grossMargin;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getProfitMargin() {
		return profitMargin;
	}

	public void setProfitMargin(String profitMargin) {
		this.profitMargin = profitMargin;
	}
	
	@Length(min=1, max=2000, message="毛利说明文件必须存在，长度必须介于 1 和 2000 之间")
	public String getProfitMarginFile() {
		return profitMarginFile;
	}

	public void setProfitMarginFile(String profitMarginFile) {
		this.profitMarginFile = profitMarginFile;
	}
	
	@Length(min=1, max=1)
	public String getOutsourcing() {
		return outsourcing;
	}

	public void setOutsourcing(String outsourcing) {
		this.outsourcing = outsourcing;
	}
	
	
	@XmlTransient
	public List<String> getCategoryList() {
		if (category == null){
			return Lists.newArrayList();
		}else{
			return Lists.newArrayList(StringUtils.split(category, ","));
		}
	}

	
	public void setCategoryList(List<String> categoryList) {
		if (categoryList == null){
			this.category = "";
		}else{
			this.category = ","+StringUtils.join(categoryList, ",") + ",";
		}
	}
	
	@XmlTransient
	public List<String> getPrintingPasteList() {
		if (printingPaste == null){
			return Lists.newArrayList();
		}else{
			return Lists.newArrayList(StringUtils.split(printingPaste, ","));
		}
	}
	
	public void setPrintingPasteList(List<String> printingPasteList) {
		if (printingPasteList == null){
			this.printingPaste = "";
		}else{
			this.printingPaste = ","+StringUtils.join(printingPasteList, ",") + ",";
		}
	}
	
	@XmlTransient
	public String getCategory4Export(){
		if (category == null){
			return "";
		}else{
			return DictUtils.getDictLabels(category, "tender_category", "");
		}
	}
	
	@XmlTransient
	public String getPrintingPaste4Export(){
		if (printingPaste == null){
			return "";
		}else{
			return DictUtils.getDictLabels(printingPaste, "tender_printing_paste", "");
		}
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getBiddingDate() {
		return biddingDate;
	}

	public void setBiddingDate(Date biddingDate) {
		this.biddingDate = biddingDate;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public String getBiddingMembers() {
		return biddingMembers;
	}

	public void setBiddingMembers(String biddingMembers) {
		this.biddingMembers = biddingMembers;
	}

	public String getTenderee() {
		return tenderee;
	}

	public void setTenderee(String tenderee) {
		this.tenderee = tenderee;
	}

	public String getTendereeInfo() {
		return tendereeInfo;
	}

	public void setTendereeInfo(String tendereeInfo) {
		this.tendereeInfo = tendereeInfo;
	}

	public String getJudgeInfo() {
		return judgeInfo;
	}

	public void setJudgeInfo(String judgeInfo) {
		this.judgeInfo = judgeInfo;
	}

	public String getCobidderInfo() {
		return cobidderInfo;
	}

	public void setCobidderInfo(String cobidderInfo) {
		this.cobidderInfo = cobidderInfo;
	}

	public String getPrepare() {
		return prepare;
	}

	public void setPrepare(String prepare) {
		this.prepare = prepare;
	}

	public String getBiddingPrice() {
		return biddingPrice;
	}

	public void setBiddingPrice(String biddingPrice) {
		this.biddingPrice = biddingPrice;
	}

	public String getBiddingInfo() {
		return biddingInfo;
	}

	public void setBiddingInfo(String biddingInfo) {
		this.biddingInfo = biddingInfo;
	}

	public String getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}

	public String getFinalInfo() {
		return finalInfo;
	}

	public void setFinalInfo(String finalInfo) {
		this.finalInfo = finalInfo;
	}

	public String getBiddingIssue() {
		return biddingIssue;
	}

	public void setBiddingIssue(String biddingIssue) {
		this.biddingIssue = biddingIssue;
	}

	public String getWinFlag() {
		return winFlag;
	}

	public void setWinFlag(String winFlag) {
		this.winFlag = winFlag;
	}

	public String getArchiveFlag() {
		return archiveFlag;
	}

	public void setArchiveFlag(String archiveFlag) {
		this.archiveFlag = archiveFlag;
	}

	public String getAlterFlag() {
		return alterFlag;
	}

	public void setAlterFlag(String alterFlag) {
		this.alterFlag = alterFlag;
	}

	public String getLostInfo() {
		return lostInfo;
	}

	public void setLostInfo(String lostInfo) {
		this.lostInfo = lostInfo;
	}

	public String getMembersName() {
		return membersName;
	}

	public void setMembersName(String membersName) {
		this.membersName = membersName;
	}


	public String getIfb() {
		return ifb;
	}

	public void setIfb(String ifb) {
		this.ifb = ifb;
	}

	public String getNegotiationFile() {
		return negotiationFile;
	}

	public void setNegotiationFile(String negotiationFile) {
		this.negotiationFile = negotiationFile;
	}

	public String getPriceAccountingFile() {
		return priceAccountingFile;
	}

	public void setPriceAccountingFile(String priceAccountingFile) {
		this.priceAccountingFile = priceAccountingFile;
	}
}