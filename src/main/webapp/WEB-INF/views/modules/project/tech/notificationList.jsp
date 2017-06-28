<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>知会管理</title>
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
	<style type="text/css">
		.td-text {
			text-overflow: ellipsis;
			overflow: hidden;
			white-space: nowrap;
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/project/tech/notification/">知会列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="notification" action="${ctx}/project/tech/notification/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>知会时间：</label>
				<input name="beginNotificationDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${notification.beginNotificationDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> -
				<input name="endNotificationDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${notification.endNotificationDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="table-layout: fixed">
		<thead>
			<tr>
				<th style="width: 250px;">申请编号</th>
				<th style="width: 250px;">知会编号</th>
				<th>知会内容</th>
				<th style="width: 120px;">知会时间</th>
				<th>知会评论</th>
				<th style="width: 120px;">评论时间</th>
				<shiro:hasAnyPermissions name="project:tech:notification:edit, project:tech:notification:reply">
					<th style="width: 65px;">操作</th>
				</shiro:hasAnyPermissions>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="notification">
			<tr>
				<td class="td-text">
					${notification.assigning.techapply.id}
				</td>
				<td class="td-text">
					<a href="${ctx}/project/tech/notification/form?id=${notification.id}">
							${notification.id}
					</a>
				</td>
				<td class="td-text">
					${notification.notification}
				</td>
				<td class="td-text">
					<fmt:formatDate value="${notification.notificationDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td class="td-text">
					${notification.reply}
				</td>
				<td class="td-text">
					<fmt:formatDate value="${notification.replyDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="project:tech:notification:edit">
					<td class="td-text">
    					<a href="${ctx}/project/tech/notification/form?id=${notification.id}">修改</a>
						<a href="${ctx}/project/tech/notification/delete?id=${notification.id}" onclick="return confirmx('确认要删除该知会吗？', this.href)">删除</a>
					</td>
				</shiro:hasPermission>
				<shiro:hasPermission name="project:tech:notification:reply">
					<td class="td-text">
						<a href="${ctx}/project/tech/notification/form?id=${notification.id}">评论</a>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>