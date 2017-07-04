<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>结项审批管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//
			$(".trace").each(function() {
				$(this).click(function(){
					if ($(this).attr("procInsId") =="" || $(this).attr("procInsId") == null){
						top.$.jBox.tip('请先启动流程再跟踪。');
						return false;
					}
				});
			});
			//
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/project/finish/projectFinishApproval/">结项审批列表</a></li>
	<shiro:hasPermission name="project:finish:projectFinishApproval:edit">
		<li><a href="${ctx}/project/finish/projectFinishApproval/form">结项审批添加</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="projectFinishApproval" htmlEscape="false"
		   action="${ctx}/project/finish/projectFinishApproval/" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<ul class="ul-form">
		<li><label>项目编号：</label>
			<form:input path="apply.projectCode" maxlength="64" class="input-medium"/>
		</li>
		<li><label>项目名称：</label>
			<form:input path="apply.projectName" maxlength="64" class="input-medium"/>
		</li>
		<li><label>结项种类：</label>
			<form:checkboxes path="category" items="${fns:getDictList('jic_pro_finish_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
		<tr>
			<th>结项种类</th>
			<th>项目编号</th>
			<th>项目名称</th>
			<th>更新时间</th>
			<shiro:hasPermission name="project:finish:projectFinishApproval:edit"><th>操作</th></shiro:hasPermission>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="projectFinishApproval">
		<tr>
			<td><a href="${ctx}/project/finish/projectFinishApproval/form?id=${projectFinishApproval.id}">
				${fns:getDictLabel(projectFinishApproval.category, 'jic_pro_finish_type', '')}
			</a></td>
			<td>
				${projectFinishApproval.apply.projectCode}
			</td>
			<td>
				${projectFinishApproval.apply.projectName}
			</td>
			<td>
				<fmt:formatDate value="${projectFinishApproval.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</td>
			<shiro:hasPermission name="project:finish:projectFinishApproval:edit"><td>

				<c:if test="${projectFinishApproval.processStatus == '2'}">
					<input export="btnExport" class="btn btn-primary" type="button" proId="${projectFinishApproval.id}" value="导出"/>
				</c:if>
				<a href="${ctx}/project/finish/projectFinishApproval/form?id=${projectFinishApproval.id}">详情</a>

				<c:if test="${projectFinishApproval.processStatus != '2'}">
					<a class="trace" target="_blank" procInsId="${projectFinishApproval.procInsId}" href="${ctx}/act/task/trace1?procInsId=${projectFinishApproval.procInsId}">跟踪</a>
				</c:if>
				<c:if test="${projectFinishApproval.processStatus == '2'}">
					<a href="${ctx}/project/finish/projectFinishApproval/delete?id=${projectFinishApproval.id}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
				</c:if>

				<%--<a href="${ctx}/project/finish/projectFinishApproval/form?id=${projectFinishApproval.id}">详情</a>--%>
				<%--<a class="trace" target="_blank" procInsId="${projectFinishApproval.procInsId}" href="${ctx}/act/task/trace1?procInsId=${projectFinishApproval.procInsId}">跟踪1</a>--%>
				<%--<a class="trace" target="_blank" procInsId="${projectFinishApproval.procInsId}" href="${ctx}/act/task/trace2?procInsId=${projectFinishApproval.procInsId}">跟踪2</a>--%>
				<%--<a href="${ctx}/project/finish/projectFinishApproval/delete?id=${projectFinishApproval.id}" onclick="return confirmx('确认要删除该结项审批吗？', this.href)">删除</a>--%>
			</td></shiro:hasPermission>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>