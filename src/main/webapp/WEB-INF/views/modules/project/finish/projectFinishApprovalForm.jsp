<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>结项审批管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
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
				}
			});
		});

        // 选择项目后触发事件
        function changeProject(projectId, idx) {
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {
                    $("#project_code").text(apply.projectCode);
                    $("#saler_name").text(apply.saler.name);
                    $("#customer_name").text(apply.projectManager.name);
                    $("#saler_office_name").text(apply.saler.office.name);
                });
        }
	</script>

</head>
<body>
<ul class="nav nav-tabs">
<c:choose>
	<c:when test="${ empty projectFinishApproval.act.taskId}">
	<%--<li><a href="${ctx}/project/finish/projectFinishApproval/">结项审批列表</a></li>--%>
	<li class="active"><a href="${ctx}/project/finish/projectFinishApproval/form?id=${projectFinishApproval.id}">结项审批
		<shiro:hasPermission name="project:finish:projectFinishApproval:edit">${not empty projectFinishApproval.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:finish:projectFinishApproval:edit">查看</shiro:lacksPermission></a></li>
	</c:when>
	<c:otherwise> <!-- 我的任务 -->
		<li class="active"><a>项目结项<shiro:hasPermission name="project:finish:projectFinishApproval:edit">${not empty projectFinishApproval.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:finish:projectFinishApproval:edit">查看</shiro:lacksPermission></a></li>
	</c:otherwise>
</c:choose>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectFinishApproval" htmlEscape="false"
		   action="${ctx}/project/finish/projectFinishApproval/save" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<form:hidden id="flag" path="act.flag"/>
	<sys:message content="${message}"/>

	<table class="table-form">
		<tr>
			<th colspan="5" class="tit">项目信息</th>
		</tr>

		<tr>
			<td class="tit" colspan="2">项目名称<span class="help-inline"><font color="red">*</font> </span></td>
			<td colspan="1" >
				<div style="white-space:nowrap;" >
					<sys:treeselect
							id="apply"
							name="apply.id"
							value="${projectFinishApproval.apply.id}"
							labelName="apply.projectName"
							labelValue="${projectFinishApproval.apply.projectName}"
							title="项目名称" cssClass="required"  allowClear="true" notAllowSelectParent="true"
							url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11"
							customClick="changeProject"/>
				</div>
			</td>

			<td class="tit">项目编码</td>
			<td class="tit_content"><label id="project_code" >${projectFinishApproval.apply.projectCode }</label></td>
		</tr>

		<tr>
			<td class="tit" colspan="2">客户名称</td>
			<td class="tit_content"><label id="customer_name">${projectFinishApproval.apply.customer.customerName }</label></td>

			<td class="tit">项目类别</td>
			<td>${projectFinishApproval.apply.bigCategory }</td>
		</tr>

		<c:if test="${not empty projectFinishApproval.apply.saler.name}">
			<tr>
				<td  class="tit" colspan="1">销售人员</td>
				<td colspan="1">
					<label>${projectFinishApproval.apply.saler.name }</label>
				</td>
				<td  class="tit">部&nbsp;&nbsp;门</td>
				<td colspan="1">
						${projectFinishApproval.apply.saler.office.name  }
				</td>
			</tr>
		</c:if>

		<tr>
			<td class="tit" colspan="2">结项收入</td>
			<td class="">
				<div class="input-append" >
					<form:input path="finishPrice"  number="true" min="0" max="99999999" maxlength="100" class="checkNum input-medium required"/><span class="add-on">元</span>
				</div>
			</td>
			<td class="tit">结项日期</td>
			<td>
				<input name="finishDate" type="text" readonly="readonly" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${projectFinishApproval.finishDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</td>
		</tr>

		<tr>
			<td class="tit" colspan="2">结项种类<span class="help-inline"><font color="red">*</font> </span></td>
			<td colspan="3">
				<form:checkboxes path="category" items="${fns:getDictList('jic_pro_finish_type')}" itemLabel="label" itemValue="value" class="required"/>
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">项目概述</td>
			<td colspan="3">
				<form:textarea path="summary"
				   style="width:98%"  maxlength="255"
				   placeholder="包括项目范围、工作内容简述、项目涉及项目组成员、项目架构等情况。"/>
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">实施运行计划</td>
			<td colspan="3">
				<form:textarea path="runningPlan"
							   style="width:98%"  maxlength="255"
							   placeholder="成功的项目需说明项目下一阶段转至哪个部门负责。"/>
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">结项情况说明<br>项目成果分享</td>
			<td colspan="3">
				<form:textarea path="achievement"
				   style="width:98%"  maxlength="255"
				   placeholder="对此项目是否成功进行分析，具体说明原因，有何收获。通过项目结果带来的经验分享，并对项目资源释放情况进行说明。"/>
			</td>
		</tr>

		<tr>
			<td rowspan="3" class="tit">项目利润率<br>说明</td>
			<td class="tit">预算利润率</td>
			<td>
				<form:input path="estimatedProfitMargin" maxlength="255" class="input-xlarge "/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>

			<td class="tit">结算利润率</td>
			<td>
				<form:input path="settlementProfitMargin" maxlength="255" class="input-xlarge "/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<tr>
			<td class="tit">利润率浮动</td>
			<td colspan="3">
				<form:input path="profitMarginFloat" maxlength="255" class="input-xlarge "/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<tr>
			<td class="tit">情况说明</td>
			<td colspan="3">
				<form:textarea path="profitMarginInfo"
							   style="width:98%"  maxlength="255"
							   placeholder="利润浮动说明情况分析"/>
			</td>
		</tr>

		<tr>
			<td class="tit">项目承担费用说明</td>
			<td colspan="4">
				<form:textarea path="feeInfo"
							   style="width:98%"  maxlength="255"
							   placeholder="人工、社保、福利等人力费用；差旅、交通、招待等所有为该项目支出的费用；人力费用如需单独核算需要单独报考勤给人力资源部。"/>
			</td>
		</tr>

		<tr>
			<td colspan="1" class="tit">项目核算表附件</td>
			<td class="" colspan="4">
				<form:hidden id="projectAccountingFile" path="projectAccountingFile" maxlength="2000" class="input-xlarge required"/>
				<sys:ckfinder input="projectAccountingFile" type="files" uploadPath="/project/finish/projectFinishApproval" selectMultiple="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

	</table>

	<div class="form-actions">
		<shiro:hasPermission name="project:finish:projectFinishApproval:edit">

		<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
			<c:if test="${not empty projectFinishApproval.id}">
				<input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>

		<shiro:hasPermission name="apply:external:projectApplyExternal:super">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')"/>&nbsp;
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectFinishApproval.id}">
		<act:histoicFlow procInsId="${projectFinishApproval.procInsId}" />
	</c:if>
</form:form>
</body>
</html>