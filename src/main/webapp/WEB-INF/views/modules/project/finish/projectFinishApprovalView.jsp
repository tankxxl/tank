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
	<li><a href="${ctx}/project/finish/projectFinishApproval/">结项审批列表</a></li>
	<li class="active"><a href="${ctx}/project/finish/projectFinishApproval/form?id=${projectFinishApproval.id}">结项审批
		<shiro:hasPermission name="project:finish:projectFinishApproval:edit">${not empty projectFinishApproval.id?'审批':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:finish:projectFinishApproval:edit">查看</shiro:lacksPermission></a></li>
	</c:when>
	<c:otherwise> <!-- 我的任务 -->
		<li class="active"><a>项目结项<shiro:hasPermission name="project:finish:projectFinishApproval:edit">
			${not empty projectFinishApproval.id?'审批':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:finish:projectFinishApproval:edit">查看</shiro:lacksPermission></a></li>
	</c:otherwise>
</c:choose>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectFinishApproval" htmlEscape="false"
		   action="${ctx}/project/finish/projectFinishApproval/saveAudit" method="post" class="form-horizontal">
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
			<td class="tit" colspan="2">项目名称</td>
			<td colspan="1" >
				${projectFinishApproval.apply.projectName}
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
					<form:input path="finishPrice" readonly="true" class="checkNum input-medium required"/><span class="add-on">元</span>
				</div>
			</td>
			<td class="tit">结项日期</td>
			<td>
				<fmt:formatDate value="${projectFinishApproval.finishDate }" pattern="yyyy-MM-dd"/>
			</td>
		</tr>

		<tr>
			<td class="tit" colspan="2">结项种类</td>
			<td colspan="3">
				${fns:getDictLabels(projectFinishApproval.category, 'jic_pro_finish_type', '')}
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">项目概述</td>
			<td colspan="3">
				${projectFinishApproval.summary}
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">实施运行计划</td>
			<td colspan="3">
				${projectFinishApproval.runningPlan}
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">结项情况说明<br>项目成果分享</td>
			<td colspan="3">
				${projectFinishApproval.achievement}
			</td>
		</tr>

		<tr>
			<td rowspan="3" class="tit">项目利润率<br>说明</td>
			<td class="tit">预算利润率%</td>
			<td>
				${projectFinishApproval.estimatedProfitMargin}
			</td>

			<td class="tit">结算利润率%</td>
			<td>
				${projectFinishApproval.settlementProfitMargin}
			</td>
		</tr>

		<tr>
			<td class="tit">利润率浮动%</td>
			<td colspan="3">
				${projectFinishApproval.profitMarginFloat}
			</td>
		</tr>

		<tr>
			<td class="tit">情况说明</td>
			<td colspan="3">
				${projectFinishApproval.profitMarginInfo}
			</td>
		</tr>

		<tr>
			<td class="tit">项目承担费用说明</td>
			<td colspan="4">
				${projectFinishApproval.feeInfo}
			</td>
		</tr>

		<tr>
			<td colspan="1" class="tit">项目核算表附件</td>
			<td class="" colspan="4">
				<form:hidden id="projectAccountingFile" path="projectAccountingFile" maxlength="2000" class="input-xlarge required"/>
				<sys:ckfinder input="projectAccountingFile" type="files" uploadPath="/project/finish/projectFinishApproval" selectMultiple="true" readonly="true"/>
			</td>
		</tr>

		<c:if test="${not empty projectFinishApproval.act.taskId && projectFinishApproval.act.status != 'finish'}">
			<tr>
				<td class="tit">您的意见</td>
				<td colspan="5">
					<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
					<span class="help-inline"><font color="red">*</font></span>
				</td>
			</tr>
		</c:if>

	</table>

	<div class="form-actions">
		<shiro:hasPermission name="project:finish:projectFinishApproval:edit">

		<c:if test="${not empty projectFinishApproval.act.taskId && projectFinishApproval.act.status != 'finish'}">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
			<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
		</c:if>
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectFinishApproval.id}">
		<act:histoicFlow procInsId="${projectFinishApproval.procInsId}" />
	</c:if>
</form:form>
</body>
</html>