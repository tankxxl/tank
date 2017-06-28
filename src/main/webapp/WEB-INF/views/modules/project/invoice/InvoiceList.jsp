<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#contentTable").find("input[export]").each(function(){
				$(this).click(function(){
					var proId =$(this).attr("proId");
					top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/project/invoice/export?id="+proId);
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
		<li class="active"><a href="${ctx}/project/invoice/">开票列表</a></li>
		<shiro:hasPermission name="project:invoice:edit"><li><a href="${ctx}/project/invoice/form">开票添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm"
               modelAttribute="projectInvoice"
               action="${ctx}/project/invoice/"
               method="post"
               class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

		<ul class="ul-form">
			<li><label>项目编号：</label>
				<form:input path="apply.projectCode" type="text" placeholder="项目编号" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="apply.projectName" type="text" placeholder="项目名称" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
            <li class="btns">
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询">
                    <i class="icon-search"></i>
                </input>
            </li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th>项目编号</th>
				<th>项目名称</th>
                <th>合同编号</th>
				<th>更新时间</th>
				<th>审批状态</th>
				<shiro:hasPermission name="project:invoice:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="invoice">
			<tr>
				<td><a href="${ctx}/project/invoice/form?id=${invoice.id}">
					${invoice.apply.projectCode}
				</a></td>
				<td>
					${invoice.apply.projectName}
				</td>

                <td>
                    ${invoice.contractItem.contractCode}
                </td>

				<td>
					<fmt:formatDate value="${invoice.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>

				<c:choose>
					<c:when test="${invoice.procStatus != '2'}">
						<td class="text-warning" >
					</c:when>
					<c:otherwise>
						<td class="text-success">
					</c:otherwise>
				</c:choose>
					${fns:getDictLabel(invoice.procStatus, 'AuditStatus', '')}
				</td>

				<shiro:hasPermission name="project:invoice:edit">
				<td>
					<c:if test="${invoice.procStatus == '2'}">
					<input export="btnExport" class="btn btn-primary" type="button" proId="${invoice.id}" value="导出"/>
					</c:if>
    				<a href="${ctx}/project/invoice/form?id=${invoice.id}">详情</a>

    				<c:if test="${invoice.procStatus != '2'}">
						<a class="trace" target="_blank" procInsId="${invoice.procInsId}" href="${ctx}/act/task/trace1?procInsId=${invoice.procInsId}">跟踪</a>
					</c:if>
					<c:if test="${invoice.procStatus == '2'}">
						<a class="warning" href="${ctx}/project/invoice/delete?id=${invoice.id}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
					</c:if>

                    <shiro:hasPermission name="project:invoice:admin">
                        <a href="${ctx}/project/invoice/modify?id=${invoice.id}">修改</a>
                    </shiro:hasPermission>
					<a href="${ctx}/project/invoice/returnForm?id=${invoice.id}">回款</a>

    				<%-- <a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}">详情</a>
    				<a class="trace" target="_blank" procInsId="${projectContract.procInsId}" href="${ctx}/act/task/trace1?procInsId=${projectContract.procInsId}">跟踪1</a>
					<a class="trace" target="_blank" procInsId="${projectContract.procInsId}" href="${ctx}/act/task/trace2?procInsId=${projectContract.procInsId}">跟踪2</a>
					<a href="${ctx}/project/contract/projectContract/delete?id=${projectContract.id}" onclick="return confirmx('确认要删除该合同吗？', this.href)">删除</a> --%>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>