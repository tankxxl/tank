<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>立项申请管理</title>
	<meta name="decorator" content="default"/>
		
	<script type="text/javascript">
	
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					estimatedGrossProfitMargin: {
				      required: true,
				      range: [0,100]
				    },
					ownership: {
				      required: true
				    }
				  },
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
// 					$("#messageBox").text("输入有误，请先更正。");
// 					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						
// 						if(error[0].innerHTML ==''){
// 							error[0].innerHTML= "必填信息";
// 						}
// 						console.log(element);
// 						console.log(error);
// 						error.appendTo(element.parent().parent());
// 					}else {
// 						error.insertAfter(element);
// 					}
				}
			});
			
			$("#category").change(function(){
// 				$("#inputForm").valid().element($("#category"));
				$("#inputForm").validate().element($("#category"));
			});
			$("#ownership").change(function(){
				$("#inputForm").validate().element($("#ownership"));
			});
			
			// 只能输入数字，并且关闭输入法
			$(".checkNum").keypress(function(event) {
				var keyCode = event.which;
				if (keyCode == 46 || (keyCode >= 48 && keyCode <= 57) || keyCode == 8) // 8是删除键 
					return true;
				else
					return false;
			}).focus(function() {
				this.style.imeMode = 'disabled';
				/* imeMode有四种形式，分别是：
				active 代表输入法为中文
				inactive 代表输入法为英文
				auto 代表打开输入法 (默认)
				disable 代表关闭输入法 */
			});
			
			
		});
		
		
		function changeCustomer(customerId){
			var url ="${ctx }/customer/customer/customer4projectApplyExternal?id="+customerId;
		    $.ajax( {  
		        type : "get",  
		        url : url,  
		        dataType:"json",
		        success : function(customer) {
		            $("#customer_industry_label").text(customer.industry);
		            $("#customer_category_label").text(customer.customerCategory);
		            
		            //验证validate
		            $("#inputForm").validate().element($("#customerName"));
		        }  
		    });
		    
		    //清除客户联系人的值
		    $("#customerContact_phone_label").text("");
		    $("#customerContact_position_label").text("");
		    $("#customerContactId").val("");
		    $("#customerContactName").val("");
		}
	</script>
	<style type="text/css">
		.tit_content{
			text-align:center
		}
	</style>
</head>
<body>
<ul class="nav nav-tabs">
	<c:if test="${ empty projectApplyExternal.act.taskId}">
		<li><a href="${ctx}/apply/external/projectApplyExternal/">立项申请列表</a></li>
	</c:if>
	<li class="active"><a href="${ctx}/apply/external/projectApplyExternal/form?id=${projectApplyExternal.id}">立项申请<shiro:hasPermission name="apply:external:projectApplyExternal:edit">${not empty projectApplyExternal.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="apply:external:projectApplyExternal:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectApplyExternal" htmlEscape="false"
		   action="${ctx}/apply/external/projectApplyExternal/save" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<form:hidden id="flag" path="act.flag"/>
	<form:hidden path="docPath" />
	<sys:message content="${message}"/>

	<c:set var="rand" value="id"/>
	<%--<c:if test="${}"--%>
	<table class="table-form">
		<caption>项目立项备案审批表</caption>
		<tr>
			<td colspan="1" class="tit">项目编号${rand}</td>
			<td colspan="1" class="" >
				<shiro:hasPermission name="apply:external:projectApplyExternal:onlySave">
					<form:input path="projectCode" style="width:90%" htmlEscape="false" maxlength="64" class=" required"/>
					<span class="help-inline"><font color="red">*</font></span>
				</shiro:hasPermission>
				<shiro:lacksPermission name="apply:external:projectApplyExternal:onlySave">
					${projectApplyExternal.projectCode  }
				</shiro:lacksPermission>
			</td>
			<td colspan="1" class="tit">申请部门</td>
			<td colspan="1">
				${fns:getUser().office.name}
			</td>
		</tr>

		<tr>
			<td colspan="1" class="tit" >项目名称</td>
			<td colspan="3">
				<form:input path="projectName" style="width:95%" htmlEscape="false" maxlength="100" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<c:if test="${not empty projectApplyExternal.saler.name}">
			<tr>
				<td  class="tit" colspan="1">销售人员</td>
				<td colspan="1">
					<label>${projectApplyExternal.saler.name }</label>
				</td>
				<td  class="tit">部&nbsp;&nbsp;门</td>
				<td colspan="1">
					${projectApplyExternal.saler.office.name  }
				</td>
			</tr>
		</c:if>

		<tr>
			<td  class="tit" colspan="1">项目类别</td>
			<td>
				<form:select path="category" class="input-medium required" >
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
			<td  class="tit">是否涉及自研</td>
			<td  class="">
				<form:select path="selfDev" class="input-medium required">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<tr>
			<td  class="tit"  colspan="1">预计合同金额</td>
			<td class="">
				<div class="input-append">
					<form:input path="estimatedContractAmount" htmlEscape="false" maxlength="10" number="true" min="0" max="99999999" class="checkNum input-medium"/><span class="add-on">元</span>
				</div>
			</td>
			<td  class="tit">预计公司利润率</td>
			<td class="">
				<div class="input-append">
					<form:input path="estimatedGrossProfitMargin"  style="width:80px" htmlEscape="false" maxlength="5" number="true" min="0" max="999" class="checkNum input-medium"/>
					<span class="add-on">%</span>
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit">预计开始时间</td>
			<td>
				<input name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					value="<fmt:formatDate value="${projectApplyExternal.beginDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
					<span class="help-inline"><font color="red">*</font> </span>
			</td>
			<td class="tit">预计截止时间</td>
			<td>
				<input name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${projectApplyExternal.endDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<tr>
			<td class="tit">项目经理</td>
			<td>
				<sys:treeselect id="projectManager" name="projectManager.id"
								value="${projectApplyExternal.projectManager.id}" labelName="projectManager.name"
								labelValue="${projectApplyExternal.projectManager.name}"
								dataMsgRequired="经理必填" title="经理" url="/sys/office/treeData?type=3"
								cssClass="required"  allowClear="true" notAllowSelectParent="true" />
				<span class="help-inline"><font color="red">*</font> </span>
			</td>

			<td class="tit">项目组成员</td>
			<td>
				<sys:treeselect id="projectMembers" name="projectMembers"
								value="${projectApplyExternal.projectMembers}" labelName="projectMembers"
								labelValue="${projectApplyExternal.membersName}"
								checked="true"
								dataMsgRequired="项目成员必填" title="项目成员" url="/sys/office/treeData?type=3"
								cssClass="required"  allowClear="true" notAllowSelectParent="true" />
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目开展背景、概述</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
				<form:textarea path="description"
							   style="width:98%"  htmlEscape="false"  maxlength="255"
							   placeholder="项目背景及机遇，请描述需求内容，即项目设计说明，目的是让审批人了解该项目的目前情况。"/>
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目业务模式/产品形式</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					<form:textarea path="pattern"
								   style="width:98%"  htmlEscape="false"  maxlength="255"
								   placeholder="请描述对客户的需求，制定市场/产品/销售/服务等应对的策略。及产品形式需求的特定满足形式。"/>
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目目标/阶段性目标</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					<form:textarea path="target"
								   style="width:98%"  htmlEscape="false"  maxlength="255"
								   placeholder="是否有预期的实现目标（工作要求，达到目标），请再此说明；
重点说明项目的投入对公司的价值贡献，包括公司收入/利润、其他重要价值。"/>
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目盈利分析</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					<form:textarea path="analysis"
								   style="width:98%"  htmlEscape="false"  maxlength="255"
								   rows="4"
								   placeholder="项目的商业模式，请在此明确；
项目的整体收益情况进行分析，附上详细测算说明，并明确测算的基本假设；
1.如果是短期的项目（一年以内），请对整个项目周期进行损益预测（按照季度或月度预测）。
2.如果是长期运作项目（一年以上），请预测3年的收益及资金投入情况（按照季度或半年预测）。"/>
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit">项目需要资源</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					<form:textarea path="resource"
								   style="width:98%"  htmlEscape="false"  maxlength="255"
								   rows="7"
								   placeholder="1）公司的资质：资质要求
2）人员配置：该部分内容要与业务目标及盈利预测部分匹配，且与其中人工成本预测数据相符
3）资金需求
4）发票种类及发票量需求
5）库房及货物保管、发货要求
6）系统支持要求
若项目存在公司的外部合作方，请在此描述各自的分工和利益；
"/>
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目风险预测</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					<form:textarea path="riskAnalysis"
								   style="width:98%"  htmlEscape="false"  maxlength="255"
								   placeholder="如果有其他需要提前说明的事项，特别是项目的风险，请在这里做出说明。"/>
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit" >文件附件</td>
			<td   colspan="3">
				<form:hidden id="documentAttachmentPath" path="documentAttachmentPath" htmlEscape="false" maxlength="20000"  />
				<sys:ckfinder input="documentAttachmentPath" type="files"
							  uploadPath="/apply"
							  selectMultiple="true" />
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="4">填表说明</td>
		</tr>
		<tr>
			<td colspan="4">
			<span class="help-block" >
				1、立项经过备案后，方可进入下一步会签审批环节。<br>
				2、立项通过审批后，原件由市场营销中心存档。（申请部门、运营管理部、财务部均留存复印件）<br>
				3、如对项目信息有更详细的说明，可附页说明，其他文档作为附件提交。<br>
				4、本审批表需按审批栏逐级审批。<br>
				5、项目测算表请作为附件在审批中一并提交。<br>
				6、打印要求：项目评审会表打印时需双面打印。
			</span>
			</td>
		</tr>
	</table>

	<div class="form-actions">
		<shiro:hasPermission name="apply:external:projectApplyExternal:edit">

		<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
			<c:if test="${not empty projectApplyExternal.id}">
				<input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>

		<shiro:hasPermission name="apply:external:projectApplyExternal:super">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectApplyExternal.id}">
		<act:histoicFlow procInsId="${projectApplyExternal.processInstanceId}" />
	</c:if>
</form:form>
</body>
</html>