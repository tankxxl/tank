<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>人员薪资管理</title>
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
		<li class="active"><a href="${ctx}/salary/relation/userSalaryRelation/">人员薪资列表</a></li>
		<shiro:hasPermission name="salary:relation:userSalaryRelation:edit"><li><a href="${ctx}/salary/relation/userSalaryRelation/form">人员薪资添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userSalaryRelation" action="${ctx}/salary/relation/userSalaryRelation/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户编号：</label>
				<sys:treeselect id="user" name="user.id" value="${userSalaryRelation.user.id}" labelName="user.name" labelValue="${userSalaryRelation.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>工资编号：</label>
				<form:input path="salaryId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户编号</th>
				<th>工资编号</th>
				<shiro:hasPermission name="salary:relation:userSalaryRelation:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userSalaryRelation">
			<tr>
				<td><a href="${ctx}/salary/relation/userSalaryRelation/form?id=${userSalaryRelation.id}">
					${userSalaryRelation.user.name}
				</a></td>
				<td>
					${userSalaryRelation.salaryId}
				</td>
				<shiro:hasPermission name="salary:relation:userSalaryRelation:edit"><td>
    				<a href="${ctx}/salary/relation/userSalaryRelation/form?id=${userSalaryRelation.id}">修改</a>
					<a href="${ctx}/salary/relation/userSalaryRelation/delete?id=${userSalaryRelation.id}" onclick="return confirmx('确认要删除该人员薪资吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>