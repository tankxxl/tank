<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>项目投标管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#contentTable").find("input[export]").each(function(){
				$(this).click(function(){
					var proId =$(this).attr("proId");
					top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/project/bidding/projectBidding/export?id="+proId);
							$("#searchForm").submit();
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			});

			$(".trace").each(function() {
				$(this).click(function(){
					if ($(this).attr("procInsId") =="" || $(this).attr("procInsId") == null){
						top.$.jBox.tip('请先启动流程再跟踪。');
						return false;
					}
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
	<li class="active"><a href="${ctx}/project/bidding/projectBidding/">投标申请列表</a></li>
	<shiro:hasPermission name="project:bidding:projectBidding:edit">
		<li><a href="${ctx}/project/bidding/projectBidding/form">投标添加</a></li></shiro:hasPermission>
</ul>
	<form:form id="searchForm" modelAttribute="projectBidding" htmlEscape="false"
			   action="${ctx}/project/bidding/projectBidding/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目编号：</label>
				<form:input path="apply.projectCode" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="apply.projectName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>标书种类：</label>
				<form:checkboxes path="category" items="${fns:getDictList('tender_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<%--<li><label>用印内容：</label>--%>
				<%--<form:checkboxes path="printingPaste" items="${fns:getDictList('tender_printing_paste')}" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
			<%--</li>--%>
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
				<th>标书种类</th>
				<%--<th>用印内容</th>--%>
				<th>更新时间</th>
				<th>审批状态</th>
				<%--<th>备注信息</th>--%>
				<shiro:hasPermission name="project:bidding:projectBidding:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="projectBidding">
			<tr>
				<td><a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">
					${projectBidding.apply.projectCode}
				</a></td>
				<td>
					${projectBidding.apply.projectName}
				</td>
				<td>
					${fns:getDictLabels(projectBidding.category, 'tender_category', '')}
				</td>

				<td>
					<fmt:formatDate value="${projectBidding.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>

				<c:choose>
				<c:when test="${projectBidding.procStatus != '2'}">
				<td class="text-warning" >
					</c:when>
					<c:otherwise>
				<td class="text-success">
					</c:otherwise>
					</c:choose>
						${fns:getDictLabel(projectBidding.procStatus, 'AuditStatus', '')}
				</td>

				<td>
				<shiro:hasPermission name="project:bidding:projectBidding:edit">

					<c:if test="${projectBidding.procStatus == '2'}">
					<input export="btnExport" class="btn btn-primary" type="button" proId="${projectBidding.id}" value="导出"/>
					</c:if>
					<a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">备案申请</a>
    				<a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">详情</a>
    				
    				<c:if test="${projectBidding.procStatus != '2'}">
						<a class="trace" target="_blank" procInsId="${projectBidding.procInsId}" href="${ctx}/act/task/trace1?procInsId=${projectBidding.procInsId}">跟踪</a>
					</c:if>
					<c:if test="${projectBidding.procStatus == '2'}">
						<a href="${ctx}/project/bidding/projectBidding/delete?id=${projectBidding.id}" onclick="return confirmx('确认要删除该项目投标吗？', this.href)">删除</a>
					</c:if>
    				
    				
    				<%-- <a class="trace" target="_blank" procInsId="${projectBidding.processInstanceId}" href="${ctx}/act/task/trace1?procInsId=${projectBidding.processInstanceId}">跟踪1</a>
					<a class="trace" target="_blank" procInsId="${projectBidding.processInstanceId}" href="${ctx}/act/task/trace2?procInsId=${projectBidding.processInstanceId}">跟踪2</a>
					<a href="${ctx}/project/bidding/projectBidding/delete?id=${projectBidding.id}" onclick="return confirmx('确认要删除该项目投标吗？', this.href)">删除</a> --%>
				</shiro:hasPermission>

					<shiro:hasPermission name="apply:external:projectApplyExternal:modify">
						<a href="${ctx}/project/bidding/projectBidding/modify?id=${projectBidding.id}">修改</a>
					</shiro:hasPermission>

				</td>


			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>