<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>拜访管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnImport").click(function(){
		      $.jBox($("#importBox").html(), {
		     title:"导入数据",
		     buttons:{"关闭":true},
		     bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
		    });
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
	<li class="active"><a href="${ctx}/crm/visit/">拜访列表</a></li>
	<shiro:hasPermission name="client:client:edit">
	<li><a href="${ctx}/crm/visit/form">拜访添加</a></li></shiro:hasPermission>
</ul>
	<form:form id="searchForm"
		modelAttribute="visit"
		action="${ctx}/crm/visit/"
		method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>客户名称：</label>
				<form:input path="client.customerName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>

			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<shiro:hasPermission name="client:client:edit">
					<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
				</shiro:hasPermission>
				
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>客户名称</th>
				<th>联系人</th>
				<th>拜访标题</th>
				<th>拜访内容</th>
				<th>拜访时间</th>
				<th>创建者</th>
				<th>创建时间</th>
				<shiro:hasPermission name="client:client:edit,client:contact:edit">
				<th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="visit">
			<tr>
				<td> ${visit.client.customerName} </td>
				<td> ${visit.contact.contactName} </td>
				<td> ${visit.title} </td>
				<td> ${visit.content} </td>
				<td> ${visit.visitDate} </td>
				<td> ${visit.createBy.name} </td>
				<td>
					<fmt:formatDate value="${visit.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<shiro:hasPermission name="client:client:edit">
	    				<a href="${ctx}/crm/visit/form?id=${visit.id}">修改</a>
						<a href="${ctx}/crm/visit/delete?id=${visit.id}" onclick="return confirmx('确认要删除该客户联系人吗？', this.href)">删除</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>