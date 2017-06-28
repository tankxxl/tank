<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>内部立项申请管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
		<li class="active"><a href="${ctx}/apply/internal/projectApplyInternal/">内部立项申请列表</a></li>
		<shiro:hasPermission name="apply:internal:projectApplyInternal:edit"><li><a href="${ctx}/apply/internal/projectApplyInternal/form">内部立项申请添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="projectApplyInternal" action="${ctx}/apply/internal/projectApplyInternal/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目编号：</label>
				<form:input path="projectCode" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="projectName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>立项发起人：</label>
				<sys:treeselect id="organiger" name="organiger.id" value="${projectApplyInternal.organiger.id}" labelName="organiger.name" labelValue="${projectApplyInternal.organiger.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目编号</th>
				<th>项目名称</th>
				<th>立项发起人</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="apply:internal:projectApplyInternal:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="projectApplyInternal">
			<tr>
				<td><a href="${ctx}/apply/internal/projectApplyInternal/form?id=${projectApplyInternal.id}">
					${projectApplyInternal.projectCode}
				</a></td>
				<td>
					${projectApplyInternal.projectName}
				</td>
				<td>
					${projectApplyInternal.organiger.name}
				</td>
				<td>
					<fmt:formatDate value="${projectApplyInternal.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${projectApplyInternal.remarks}
				</td>
				<shiro:hasPermission name="apply:internal:projectApplyInternal:edit"><td>
    				<a href="${ctx}/apply/internal/projectApplyInternal/form?id=${projectApplyInternal.id}">修改</a>
					<a href="${ctx}/apply/internal/projectApplyInternal/delete?id=${projectApplyInternal.id}" onclick="return confirmx('确认要删除该内部立项申请吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>