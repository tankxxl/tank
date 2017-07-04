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
                    $("#customer_name").text(apply.customer.customerName);
                    $("#saler_name").text(apply.saler.name);
                    $("#saler_office_name").text(apply.saler.office.name);
                });
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
		<li><a href="${ctx}/project/finish/projectFinishApproval/">结项审批列表</a></li>
		<li class="active"><a href="${ctx}/project/finish/projectFinishApproval/form?id=${projectFinishApproval.id}">结项审批<shiro:hasPermission name="project:finish:projectFinishApproval:edit">${not empty projectFinishApproval.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:finish:projectFinishApproval:edit">查看</shiro:lacksPermission></a></li>
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
				<th colspan="6" class="tit">项目信息</th>
			</tr>
			<tr>
				<td class="tit">项目名称</td>
				<td colspan="1" >
						<div style="white-space:nowrap;" >

							<sys:treeselect
									id="apply"
									name="apply.id"
									value="${projectFinishApproval.apply.id}"
									labelName="apply.projectName"
									labelValue="${projectFinishApproval.apply.projectName}"
									title="项目"
									url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11"
									cssClass="required"
									cssStyle="width: 90%;"
									dataMsgRequired="项目必选"
									allowClear="true"
									notAllowSelectParent="true"
									customClick="changeProject"/>
							<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</td>
				
				<td class="tit">项目编码</td>
				<td class="tit_content"><label id="project_code" >${projectFinishApproval.apply.projectCode }</label></td>
			</tr>
		
			<tr>
				<td class="tit">销售名称</td>
				<td class="tit_content"><label id="saler_name">${projectFinishApproval.apply.saler.name }</label></td>
				<td class="tit">销售部门</td>
				<td class="tit_content"><label id="saler_office_name">${projectFinishApproval.apply.saler.office.name }</label></td>
			</tr>
			<tr>
				<td class="tit">客户名称</td>
				<td class="tit_content"><label id="customer_name">${projectFinishApproval.apply.customer.customerName }</label></td>
			</tr>
			<tr>
				<td class="tit">结项种类</td>
				<td class="tit_content">
					<form:checkboxes path="category" items="${fns:getDictList('jic_pro_finish_type')}" itemLabel="label" itemValue="value" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
				<td class="tit">风险评估</td>
				<td class="tit_content">
					<div style="white-space:nowrap;">
						<form:input path="riskAssessment" maxlength="255" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</td>
			</tr>
			<tr>
				<td class="tit">项目核算</td>
				<td class="tit_content">
					<form:input path="projectAccounting" maxlength="255" class="input-xlarge "/>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">项目核算表附件</td>
				<td class="" colspan="5">
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

			<shiro:hasPermission name="apply:external:projectApplyExternal:onlySave">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
			</shiro:hasPermission>

			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
		</div>
		
		<c:if test="${not empty projectFinishApproval.id}">
			<act:histoicFlow procInsId="${projectFinishApproval.procInsId}" />
		</c:if>
	</form:form>
</body>
</html>