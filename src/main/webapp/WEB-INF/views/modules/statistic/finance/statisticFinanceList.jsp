<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>财务统计管理</title>
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
		<li class="active"><a href="${ctx}/statistic/finance/statisticFinance/">财务统计列表</a></li>
		<shiro:hasPermission name="statistic:finance:statisticFinance:edit"><li><a href="${ctx}/statistic/finance/statisticFinance/form">财务统计添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="statisticFinance" action="${ctx}/statistic/finance/statisticFinance/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目编码：</label>
				<form:input path="projectCode" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="projectName" htmlEscape="false" maxlength="64" class="input-medium"/>
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
				<th>工资</th>
				<th>养老</th>
				<th>医疗</th>
				<th>失业</th>
				<th>工伤</th>
				<th>生育</th>
				<th>公积金</th>
				<th>五险一金</th>
				<th>人力</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="statisticFinance">
			<tr>
				<td>
					<a href="${ctx}/statistic/finance/statisticFinance/financeList4pro?pro.id=${statisticFinance.projectId}">
						${statisticFinance.projectCode}
					</a>
				</td>
				<td>
					${statisticFinance.projectName}
				</td>
				
				<td>
					${statisticFinance.salary}
				</td>
				<td>
					${statisticFinance.pension}
				</td>
				<td>
					${statisticFinance.medical}
				</td>
				<td>
					${statisticFinance.unemployment}
				</td>
				<td>
					${statisticFinance.occupationalInjury}
				</td>
				<td>
					${statisticFinance.birth}
				</td>
				<td>
					${statisticFinance.rovidentFund}
				</td>
				<td>
					${statisticFinance.insuranceAndHousingFund}
				</td>
				<td>
					${statisticFinance.labor}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>