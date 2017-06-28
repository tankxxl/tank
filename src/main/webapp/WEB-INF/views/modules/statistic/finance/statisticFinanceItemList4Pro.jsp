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
	<form:form id="searchForm" modelAttribute="statisticFinance4Pro" action="${ctx}/statistic/finance/statisticFinance/financeList4pro" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="proID" name="pro.id" type="hidden" value="${pro.id}"/>
		<ul class="ul-form" style="display:none;">
			<li class="btns"><input id="btnSubmit" style="display: none;" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>申请id</th>
				<th>工程师名称</th>
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
		<c:forEach items="${page.list}" var="statisticFinanceItem4Pro">
			<tr>
				<td>
					${statisticFinanceItem4Pro.techApplyId}
				</td>
				<td>
					${statisticFinanceItem4Pro.engnierName}
				</td>
				<td>
					${statisticFinanceItem4Pro.salary}
				</td>
				<td>
					${statisticFinanceItem4Pro.pension}
				</td>
				<td>
					${statisticFinanceItem4Pro.medical}
				</td>
				<td>
					${statisticFinanceItem4Pro.unemployment}
				</td>
				<td>
					${statisticFinanceItem4Pro.occupationalInjury}
				</td>
				<td>
					${statisticFinanceItem4Pro.birth}
				</td>
				<td>
					${statisticFinanceItem4Pro.rovidentFund}
				</td>
				<td>
					${statisticFinanceItem4Pro.insuranceAndHousingFund}
				</td>
				<td>
					${statisticFinanceItem4Pro.labor}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</body>
</html>