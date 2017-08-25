<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>合同管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();

            $("#rmb").text(digitUppercase(${projectContract.amount }));

			$("#inputForm").validate({
                rules: {
                    contractCode: {remote: "${ctx}/project/contract/projectContract/checkContractCode?oldContractCode=" + encodeURIComponent('${projectContract.contractCode}')}
                },
                messages: {
                    contractCode: {remote: "合同编号已存在"}
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
                    $("#customer_contact_name").text(apply.customerContact.contactName);
                    $("#customer_phone").text(apply.customerContact.phone);
                    $("#project_category").text(apply.category);
            });
        }
	</script>

	<style type="text/css">
		tit{
			background:#fafafa;
			text-align:center;
			/*padding-right:8px;*/
			/*white-space:nowrap;*/
		}
	</style>
</head>
<body>
<ul class="nav nav-tabs">
	<c:if test="${ empty projectContract.act.taskId}">
		<li><a href="${ctx}/project/contract/projectContract/">合同列表</a></li>
	</c:if>
	<li class="active"><a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}&contractType=1">
		${fns:getDictLabel(projectContract.contractType, 'jic_contract_type', '合同')}
		<shiro:hasPermission name="project:contract:projectContract:edit">
			${not empty projectContract.act.taskId?'审批':'查看'}
		</shiro:hasPermission>
		<shiro:lacksPermission name="project:contract:projectContract:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectContract" htmlEscape="false"
		   action="${ctx}/project/contract/projectContract/saveAudit" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<form:hidden path="contractType"/>
	<form:hidden id="flag" path="act.flag"/>
	<sys:message content="${message}"/>
	<table class="table-form">
		<%--<tr><th colspan="6" class="tit">项目信息</th></tr>--%>
		<caption>北京建投科信科技发展股份有限公司合同审批表</caption>

		<c:if test="${projectContract.contractType ne '2'}">
		<tr>
			<td class="tit">项目名称</td>
			<td >
				${projectContract.apply.projectName}
			</td>

			<td class="tit">项目编码</td>
			<td ><label id="project_code" >${projectContract.apply.projectCode }</label></td>
		</tr>
		</c:if>

		<tr>
			<td class="tit">合同编号</td>
			<td colspan="1" class="">
				<c:if test="${projectContract.act.taskDefKey eq 'usertask_specialist'}">
					<input id="oldContractCode" name="oldContractCode" type="hidden" value="${projectContract.contractCode}">
					<form:input path="contractCode" style="width:90%" cssClass="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</c:if>
				<c:if test="${projectContract.act.taskDefKey ne 'usertask_specialist'}">
					${projectContract.contractCode}
				</c:if>
			</td>

			<td class="tit">合同类型</td>
			<td colspan="1" class="">
					${fns:getDictLabel(projectContract.contractType, 'jic_contract_type', '')}
			</td>
		</tr>

		<c:if test="${projectContract.contractType eq '2'}">
			<tr>
				<td class="tit">事权审批OA号</td>
				<td colspan="3" class="">
					${projectContract.oaNo}
				</td>
			</tr>
		</c:if>

		<tr>
			<td class="tit">申请部门</td>
			<td class=""><label id="customer_name">
					${projectContract.createBy.office.name}-
					${projectContract.createBy.name}
			</label></td>
			<td class="tit">申请日期</td>
			<td class=""><label id="customer_contact_name">
				<fmt:formatDate value="${projectContract.createDate}" pattern="yyyy-MM-dd"/>
			</label></td>
		</tr>

		<%--<tr>--%>
			<%--<td class="tit">合同名称</td>--%>
			<%--<td colspan="3" class="">--%>
					<%--${projectContract.contractName}--%>
			<%--</td>--%>
		<%--</tr>--%>

		<tr>
			<td class="tit">合同对方名称</td>
			<td colspan="3" class="">
				${projectContract.clientName}
			</td>
		</tr>

		<tr>
			<td class="tit">合同总金额</td>
			<td colspan="1" class="">
				<div class="input-append">
					<form:input path="amount" style="width:122px" readonly="true" class="checkNum input-medium"/><span class="add-on">元</span>
				</div>
			</td>
			<td class="tit">大写</td>
			<td colspan="1" class=""><label id="rmb"></label></td>
		</tr>

		<tr>
			<td  class="tit" >合同金额明细</td>
			<td  colspan="3">
				${projectContract.amountDetail}
			</td>
		</tr>

		<tr>
			<td class="tit">合同有效期</td>
			<td colspan="1" class="">
				<fmt:formatDate value="${projectContract.beginDate}" pattern="yyyy-MM-dd"/>
			</td>
			<td class="tit">至</td>
			<td colspan="1" class="">
				<fmt:formatDate value="${projectContract.endDate}" pattern="yyyy-MM-dd"/>
			</td>
		</tr>

		<c:if test="${not empty projectContract.validInfo}">
		<tr>
			<td  class="tit" >合同有效期备注</td>
			<td colspan="3">${projectContract.validInfo}</td>
		</tr>
		</c:if>

		<tr>
			<td  class="tit" >合同内容摘要</td>
			<td colspan="3">${projectContract.contentSummary}</td>
		</tr>

		<tr>
			<td  class="tit" >是否为续签合同</td>

			<c:choose>
			<c:when test="${not empty projectContract.originCode}">
			<td  colspan="1">
			</c:when>

			<c:otherwise>
			<td  colspan="3">
			</c:otherwise>
			</c:choose>

				${fns:getDictLabel(projectContract.resignFlag , 'yes_no', '')}
			</td>

			<c:if test="${not empty projectContract.originCode}">
			<td  class="tit" >原合同号</td>
			<td  colspan="1">
				${projectContract.originCode}
			</td>
			</c:if>
		</tr>

		<c:if test="${not empty projectContract.resignInfo}">
			<tr>
				<td  class="tit" >续签合同说明</td>
				<td  colspan="3">
						${projectContract.resignInfo}
				</td>
			</tr>
		</c:if>

		<tr>
			<td  class="tit" >印章类型</td>
			<td  colspan="3">
				${fns:getDictLabels(projectContract.sealType, 'jic_seal_type', '')}
			</td>
		</tr>

		<tr>
			<td class="tit" >文件附件</td>
			<td  colspan="3">
				<form:hidden id="attachment" path="attachment" maxlength="20000"  />
				<sys:ckfinder input="attachment" type="files" uploadPath="/project/contract/projectContract"
					readonly="true" />
			</td>
		</tr>

		<c:if test="${not empty projectContract.act.taskId && projectContract.act.status != 'finish'}">

			<%--<c:if test="${projectContract.act.taskDefKey ne 'usertask_seal' && projectContract.act.taskDefKey ne 'usertask_specialist'}">--%>

				<tr>
					<td class="tit">您的意见</td>
					<td colspan="3">
						<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
						<span class="help-inline"><font color="red">*</font></span>
					</td>
				</tr>

			<%--</c:if>--%>

		</c:if>
	</table>
	<br/>
	<div class="form-actions">

		<shiro:hasPermission name="project:contract:projectContract:edit">
			<c:if test="${not empty projectContract.act.taskId && projectContract.act.status != 'finish'}">

				<c:if test="${projectContract.act.taskDefKey eq 'usertask_seal'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="结束流程" onclick="$('#flag').val('yes')"/>&nbsp;
				</c:if>
				<c:if test="${projectContract.act.taskDefKey ne 'usertask_seal'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="转交下一步" onclick="$('#flag').val('yes')"/>&nbsp;
				</c:if>

				&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectContract.id}">
		<act:histoicFlow procInsId="${projectContract.procInsId}" />
	</c:if>
</form:form>
</body>
</html>