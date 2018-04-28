<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>立项申请管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            $("#btnExportList").click(function(){
                top.$.jBox.confirm("确认要导出立项数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/apply/external/projectApplyExternal/exportList");
                        $("#searchForm").submit();
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });

			$("#contentTable").find("input[export]").each(function(){
				$(this).click(function(){
					var proId =$(this).attr("proId");
					top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/apply/external/projectApplyExternal/export?id="+proId);
							$("#searchForm").submit();
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			});
			
			// 
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
		<li class="active"><a href="${ctx}/apply/external/projectApplyExternal/">立项申请列表</a></li>
		<shiro:hasPermission name="apply:external:projectApplyExternal:edit">
			<li><a href="${ctx}/apply/external/projectApplyExternal/form">立项申请添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="projectApplyExternal" htmlEscape="false"
			   action="${ctx}/apply/external/projectApplyExternal/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目编码：</label>
				<form:input path="projectCode" maxlength="64" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="projectName" maxlength="64" class="input-medium"/>
			</li>
			<li><label>销售人员：</label>
				<sys:treeselect id="saler" name="saler.id" value="${projectApplyExternal.saler.id}" labelName="saler.name" labelValue="${projectApplyExternal.saler.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>客户：</label>
				<sys:treeselect id="customer" name="customer.id" value="${projectApplyExternal.customer.id}" labelName="customer.customerName" labelValue="${projectApplyExternal.customer.customerName}"
					title="客户" url="/customer/customer/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>项目类别：</label>
				<form:select path="category" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>审批状态：</label>
				<form:select path="procStatus" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('AuditStatus')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnExportList" type="button" class="btn btn-primary" value="导出"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目编码</th>
				<th>项目名称</th>
				<th>销售人员</th>
				<th>客户</th>
				<th>项目类别</th>
				<th>更新时间</th>
				<th>审批状态</th>
				<shiro:hasPermission name="apply:external:projectApplyExternal:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="projectApplyExternal">
			<tr>
				<td><a href="${ctx}/apply/external/projectApplyExternal/form?id=${projectApplyExternal.id}">
					${projectApplyExternal.projectCode}
				</a></td>
				<td>
				<c:if test="${empty projectApplyExternal.projectCode}">
					<a href="${ctx}/apply/external/projectApplyExternal/form?id=${projectApplyExternal.id}">
						${projectApplyExternal.projectName}
					</a>
				</c:if>
				<c:if test="${not empty projectApplyExternal.projectCode}">
					${projectApplyExternal.projectName}
				</c:if>
				</td>
				<td>
					${projectApplyExternal.saler.name}
				</td>
				<td>
					${projectApplyExternal.customer.customerName}
				</td>
				<td>
					${fns:getDictLabel(projectApplyExternal.category, 'pro_category', '')}
				</td>
				<td>
					<fmt:formatDate value="${projectApplyExternal.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<c:choose>
					<c:when test="${projectApplyExternal.procStatus != '2'}">
						<td style="color:red;">
					</c:when>
					<c:otherwise>
						<td>
					</c:otherwise>
				</c:choose>
					${fns:getDictLabel(projectApplyExternal.procStatus, 'AuditStatus', '无启动流程')}
				</td>
				
				<td>
				<c:if test="${projectApplyExternal.procStatus == '2'}">
					<input export="btnExport" class="btn btn-primary" type="button" proId="${projectApplyExternal.id}" value="导出"/>
				</c:if>
					
				<shiro:hasPermission name="apply:external:projectApplyExternal:edit">

					<c:choose>
					<c:when test="${projectApplyExternal.procStatus == null}">
						<a href="${ctx}/apply/external/projectApplyExternal/modify?id=${projectApplyExternal.id}">修改</a>
					</c:when>
					<c:otherwise>

					</c:otherwise>
					</c:choose>

					<a href="${ctx}/apply/external/projectApplyExternal/form?id=${projectApplyExternal.id}">详情</a>
					<c:if test="${projectApplyExternal.procStatus != '2'}">
					<a class="trace" target="_blank" procInsId="${projectApplyExternal.procInsId}" href="${ctx}/act/task/trace1?procInsId=${projectApplyExternal.procInsId}">跟踪</a>
					</c:if>
					<c:if test="${projectApplyExternal.procStatus == '2'}">
						<a href="${ctx}/apply/external/projectApplyExternal/delete?id=${projectApplyExternal.id}" onclick="return confirmx('确认要删除该立项申请吗？', this.href)">删除</a>
					</c:if>
					<%-- <a class="trace" target="_blank" procInsId="${projectApplyExternal.procInsId}" href="${ctx}/act/task/trace2?procInsId=${projectApplyExternal.procInsId}">跟踪2</a> --%>
				</shiro:hasPermission>

				<shiro:hasPermission name="apply:external:projectApplyExternal:modify">
					<a href="${ctx}/apply/external/projectApplyExternal/modify?id=${projectApplyExternal.id}">修改</a>
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>