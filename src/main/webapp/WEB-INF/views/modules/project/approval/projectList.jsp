<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>立项管理</title>
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
		<li class="active"><a href="${ctx}/project/approval/project/">立项列表</a></li>
		<shiro:hasPermission name="project:approval:project:edit"><li><a href="${ctx}/project/approval/project/form">立项添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="project" action="${ctx}/project/approval/project/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目编码：</label>
				<form:input path="projectCode" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="projectName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>销售人员编号：</label>
				<sys:treeselect id="user" name="user.id" value="${project.user.id}" labelName="user.name" labelValue="${project.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>客户编号：</label>
				<sys:treeselect id="customer" name="customer.id" value="${project.customer.id}" labelName="customer.customerName" labelValue="${project.customer.customerName}"
					title="客户" url="/customer/customer/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>项目类别：</label>
				<form:select path="category" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目编码</th>
				<th>项目名称</th>
				<th>销售人员编号</th>
				<th>客户编号</th>
				<th>预计毛利率</th>
				<th>项目类别</th>
				<shiro:hasPermission name="project:approval:project:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="project">
			<tr>
				<td><a href="${ctx}/project/approval/project/form?id=${project.id}">
					${project.projectCode}
				</a></td>
				<td>
					${project.projectName}
				</td>
				<td>
					${project.user.name}
				</td>
				<td>
					${project.customer.customerName}
				</td>
				<td>
					${project.estimatedGrossProfitMargin}
				</td>
				<td>
					${fns:getDictLabel(project.category, 'pro_category', '')}
				</td>
				<shiro:hasPermission name="project:approval:project:edit"><td>
    				<a href="${ctx}/project/approval/project/form?id=${project.id}">修改</a>
					<a href="${ctx}/project/approval/project/delete?id=${project.id}" onclick="return confirmx('确认要删除该立项吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>