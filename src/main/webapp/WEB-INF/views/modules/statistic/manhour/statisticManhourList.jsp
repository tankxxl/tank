<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>工时统计管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#searchForm").find("input[export]").each(function(){
				$(this).click(function(){
					var proId =$(this).attr("proId");
					top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/statistic/manhour/statisticManhour/export");
							$("#searchForm").submit();
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
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
		<li class="active"><a href="${ctx}/statistic/manhour/statisticManhour/">工时统计列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="statisticManhour" action="${ctx}/statistic/manhour/statisticManhour/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目编码：</label>
				<form:input path="projectCode" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="projectName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>开始时间：</label>
				<input name="startTime" type="text" readonly="readonly" maxlength="20" class="input-small Wdate "
					value="<fmt:formatDate value="${statisticManhour.startTime}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li><label>结束时间：</label>
				<input name="endTime" type="text" readonly="readonly" maxlength="20" class="input-small Wdate "
					value="<fmt:formatDate value="${statisticManhour.endTime}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input export="btnExport" class="btn btn-primary" type="button" value="导出"/></li>
			
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目编码</th>
				<th>项目名称</th>
				<th>销售部门</th>
				<th>销售名称</th>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId || statisticManhour.tectechnicalDepartmentId =='29dff97821e14902881c08f773ffcef3'}">
				<th>服务交互部工时</th>
				<th>服务交互部人力</th>
				</c:if>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId || statisticManhour.tectechnicalDepartmentId =='9da460fbfab54027acd61d044e5e5f86'}">
				<th>软件工时</th>
				<th>软件人力</th>
				</c:if>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId || statisticManhour.tectechnicalDepartmentId =='3a8e57591ff54518ad5162bbf12797fb'}">
				<th>解决方案部工时</th>
				<th>解决方案部人力</th>
				</c:if>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId}">
					<th>总工时</th>
					<th>总人力</th>
				</c:if>
				<shiro:hasPermission name="statistic:manhour:statisticManhour:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="statisticManhour2">
			<tr>
				<td>
					${statisticManhour2.projectCode}
				</td>
				<td>
					${statisticManhour2.projectName}
				</td>
				<td>
					${statisticManhour2.salerOfficeName}
				</td>
				<td>
					${statisticManhour2.salerName}
				</td>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId||statisticManhour.tectechnicalDepartmentId =='29dff97821e14902881c08f773ffcef3'}">
					<td>
						<a href="${ctx}/statistic/manhour/statisticManhour/findDepartmentManhour4pro?projectId=${statisticManhour2.projectId} &officeId=29dff97821e14902881c08f773ffcef3">
							${statisticManhour2.totalManhour4service}
						</a>
					</td>
					<td>
						${statisticManhour2.totalLabor4service}
					</td>
				</c:if>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId||statisticManhour.tectechnicalDepartmentId =='9da460fbfab54027acd61d044e5e5f86'}">
					<td>
						<a href="${ctx}/statistic/manhour/statisticManhour/findDepartmentManhour4pro?projectId=${statisticManhour2.projectId} &officeId=9da460fbfab54027acd61d044e5e5f86">
						${statisticManhour2.totalManhour4software}
						</a>
					</td>
					<td>
						${statisticManhour2.totalLabor4software}
					</td>
				</c:if>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId||statisticManhour.tectechnicalDepartmentId =='3a8e57591ff54518ad5162bbf12797fb'}">
					<td>
					<a href="${ctx}/statistic/manhour/statisticManhour/findDepartmentManhour4pro?projectId=${statisticManhour2.projectId} &officeId=3a8e57591ff54518ad5162bbf12797fb">
						${statisticManhour2.totalManhour4scheme}
					</a>
					</td>
					<td>
						${statisticManhour2.totalLabor4scheme}
					</td>
				</c:if>
				<c:if test="${empty statisticManhour.tectechnicalDepartmentId}">
					<td>
						${statisticManhour2.totalManhour}
					</td>
					<td>
						${statisticManhour2.totalLabor}
					</td>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>