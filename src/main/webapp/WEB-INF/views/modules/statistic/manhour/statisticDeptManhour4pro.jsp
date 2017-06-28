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
							$("#searchForm").attr("action","${ctx}/statistic/manhour/statisticManhour/export4SecendPage");
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
		<shiro:hasPermission name="statistic:manhour:statisticManhour:edit"><li><a href="${ctx}/statistic/manhour/statisticManhour/form">工时统计添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="statisticManhour" action="${ctx}/statistic/manhour/statisticManhour/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="projectId" name="projectId" type="hidden" value="${deptManhour4pro.projectId}"/>
		<input id="officeId" name="officeId" type="hidden" value="${deptManhour4pro.officeId}"/>
<!-- 		<ul class="ul-form" style="display:none;"> -->
		<ul class="ul-form" >
			<li class="btns"><input id="btnSubmit" style="display: none;" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input export="btnExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>申请id</th>
				<th>部门名称</th>
				<th>人员名称</th>
				<th>工时</th>
				<th>申请时间</th>
			</tr>
		</thead>
		<tbody>
		
		<c:forEach items="${page.list}" var="deptManhour4pro">
			<tr>
				<td>
					${deptManhour4pro.applyId}
				</td>
				<td>
					${deptManhour4pro.officeName}
				</td>
				<td>
					${deptManhour4pro.techName}
				</td>
				<td>
					${deptManhour4pro.manhour}
				</td>
				<td>
					<fmt:formatDate value="${deptManhour4pro.applyDate}" pattern="yyyy-MM-dd"/>
					
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