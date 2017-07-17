<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>投标备案管理</title>
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
	<li class="active"><a href="${ctx}/bidding/archive/">投标备案申请列表</a></li>
	<%--<shiro:hasPermission name="project:bidding:projectBidding:edit">--%>
		<%--<li><a href="${ctx}/bidding/archive/form">投标添加</a></li></shiro:hasPermission>--%>
</ul>
	<form:form id="searchForm" modelAttribute="biddingArchive" htmlEscape="false"
			   action="${ctx}/bidding/archive/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目编号：</label>
				<form:input path="apply.projectCode" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="apply.projectName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>

			<li><label>项目类别：</label>
				<form:select path="category" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>

			<%--<li><label>是否备案：</label>--%>
				<%--<form:select path="archiveFlag" class="input-medium">--%>
					<%--<form:option value="" label=""/>--%>
					<%--<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" />--%>
				<%--</form:select>--%>
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
				<%--<th>标书种类</th>--%>
				<%--<th>用印内容</th>--%>
				<th>更新时间</th>
				<th>审批状态</th>
				<%--<th>是否备案</th>--%>
				<shiro:hasPermission name="project:bidding:projectBidding:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="biddingArchive">
			<tr>
				<td><a href="${ctx}/bidding/archive/form?id=${biddingArchive.id}">
					${biddingArchive.apply.projectCode}
				</a></td>
				<td>
					${biddingArchive.apply.projectName}
				</td>
				<td>
					<fmt:formatDate value="${biddingArchive.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>

				<c:choose>
				<c:when test="${biddingArchive.procStatus != '2'}">
				<td class="text-warning" >
					</c:when>
					<c:otherwise>
				<td class="text-success">
					</c:otherwise>
					</c:choose>
						${fns:getDictLabel(biddingArchive.procStatus, 'AuditStatus', '')}
				</td>

				<%--<c:choose>--%>
					<%--<c:when test="${biddingArchive.archiveFlag != '1'}">--%>
						<%--<td class="text-warning" >--%>
					<%--</c:when>--%>
					<%--<c:otherwise>--%>
						<%--<td class="text-success">--%>
					<%--</c:otherwise>--%>
				<%--</c:choose>--%>
					<%--${fns:getDictLabel(biddingArchive.archiveFlag, 'yes_no', '否')}--%>
				<%--</td>--%>

				<td>
				<shiro:hasPermission name="project:bidding:projectBidding:edit">

					<c:if test="${biddingArchive.procStatus == '2'}">
					<input export="btnExport" class="btn btn-primary" type="button" proId="${biddingArchive.id}" value="导出"/>
					</c:if>
					<%--<c:if test="${biddingArchive.archiveFlag != '1' && biddingArchive.procStatus == '2'}">--%>
						<%--<a href="${ctx}/bidding/archive/form?id=${biddingArchive.id}">备案申请</a>--%>
					<%--</c:if>--%>

    				<a href="${ctx}/bidding/archive/form?id=${biddingArchive.id}">详情</a>
    				
    				<c:if test="${biddingArchive.procStatus != '2'}">
						<a class="trace" target="_blank" procInsId="${biddingArchive.procInsId}" href="${ctx}/act/task/trace1?procInsId=${biddingArchive.procInsId}">跟踪</a>
						<a class="trace" target="_blank" procInsId="${biddingArchive.procInsId}" href="${ctx}/act/task/trace2?procInsId=${biddingArchive.procInsId}">跟踪2</a>
					</c:if>
					<c:if test="${biddingArchive.procStatus == '2'}">
						<a href="${ctx}/bidding/archive/delete?id=${biddingArchive.id}" onclick="return confirmx('确认要删除该项目投标吗？', this.href)">删除</a>
					</c:if>
				</shiro:hasPermission>

					<shiro:hasPermission name="apply:external:projectApplyExternal:modify">
						<a href="${ctx}/bidding/archive/modify?id=${biddingArchive.id}">修改</a>
					</shiro:hasPermission>
				</td>

			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>