<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>工时管理</title>
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
		<li class="active"><a href="${ctx}/project/tech/manhour/">工时列表</a></li>
		<shiro:lacksPermission name="project:tech:manhour:audit">
			<li><a href="${ctx}/project/tech/manhour/form4FillInManhours">工时填报</a></li>
		</shiro:lacksPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="manhour" action="${ctx}/project/tech/manhour/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目名称：</label>
				<sys:treeselect id="project" name="workorder.assigning.techapply.project.id"
								value="${manhour.workorder.assigning.techapply.project.id}"
								labelName="workorder.assigning.techapply.project.projectName"
								labelValue="${manhour.workorder.assigning.techapply.project.projectName}" title="项目名称"
								url="/apply/external/projectApplyExternal/treeAllData?proMainStage=03" cssClass="required"
								allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>审批状态</label>
				<form:select path="auditState" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('audit_state')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
		</ul>
		<ul class="ul-form">
			<li><label>受派人员：</label>
				<sys:treeselect id="applicant" name="workorder.assigning.techapply.applicant.id"
								value="${manhour.workorder.assigning.techapply.applicant.id}" labelName="workorder.assigning.techapply.applicant.name"
								labelValue="${manhour.workorder.assigning.techapply.applicant.name}"
								title="受派人员" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true"
								notAllowSelectParent="true"/>
			</li>
			<li><label>工时日期：</label>
				<input name="beginManhourDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${manhour.beginManhourDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> -
				<input name="endManhourDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${manhour.endManhourDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="table-layout: fixed;">
		<thead>
			<tr>
				<th style="width: 250px;">工时单编号</th>
				<th>工时日期</th>
				<th>当日工时</th>
				<th>受派人</th>
				<th>审批状态</th>
				<shiro:hasAnyPermissions name="project:tech:manhour:edit, project:tech:manhour:audit">
					<th style="width: 100px;">操作</th>
				</shiro:hasAnyPermissions>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="manhour">
			<tr>
				<td><a href="${ctx}/project/tech/manhour/form?id=${manhour.id}">
					${manhour.id}
				</a></td>
				<td>
					<fmt:formatDate value="${manhour.manhourDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					${manhour.manhour}
				</td>
				<td>
					${manhour.engineer.name}
				</td>
				<td>
					${fns:getDictLabel(manhour.auditState, 'audit_state', '0')}
				</td>
				<shiro:hasPermission name="project:tech:manhour:edit">
					<td>
						<c:choose>
							<c:when test="${fns:getDictValue('审批通过', 'audit_state', '1') eq manhour.auditState}">
								<span style="color: grey">修改</span>
							</c:when>
							<c:otherwise>
								<a href="${ctx}/project/tech/manhour/form?id=${manhour.id}">修改</a>
							</c:otherwise>
						</c:choose>
					</td>
				</shiro:hasPermission>
				<shiro:hasPermission name="project:tech:manhour:audit">
					<td>
						<c:choose>
							<c:when test="${fns:getDictValue('未审批', 'audit_state', '0') eq manhour.auditState}">
								<a href="${ctx}/project/tech/manhour/form?id=${manhour.id}">审批</a>
							</c:when>
							<c:otherwise>
								<span style="color: grey">审批</span>
							</c:otherwise>
						</c:choose>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>