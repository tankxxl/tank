<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>资源申请管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        	$(".trace").each(function() {
				$(this).click(function(){
					if ($(this).attr("procInsId") =="" || $(this).attr("procInsId") == null){
						top.$.jBox.tip('请先启动流程再跟踪。');
						return false;
					}
				});
			});
        	
        	
        });
        function page(n, s) {
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
    <li class="active"><a href="${ctx}/project/tech/techapply/">资源申请列表</a></li>
    <shiro:hasPermission name="project:tech:techapply:edit">
        <li><a href="${ctx}/project/tech/techapply/form">资源申请添加</a></li>
    </shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="techapply" action="${ctx}/project/tech/techapply/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>项目名称：</label>
            <sys:treeselect id="project" name="project.id" value="${techapply.project.id}"
                            labelName="project.projectName" labelValue="${techapply.project.projectName}" title="项目"
                            url="/apply/external/projectApplyExternal/treeAllData?proMainStage=03" cssClass="required"
                            allowClear="true" notAllowSelectParent="true"/>
        </li>
        <li><label>人力来源：</label>
            <sys:treeselect id="office" name="office.id" value="${techapply.office.id}" labelName="office.name"
                            labelValue="${techapply.office.name}"
                            title="部门" url="/sys/office/treeData?type=2&isAll=1" cssClass="input-small" allowClear="true"
                            notAllowSelectParent="true"/>
        </li>
        <li><label>申请时间：</label>
            <input name="beginApplyDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
                   value="<fmt:formatDate value="${techapply.beginApplyDate}" pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> -
            <input name="endApplyDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
                   value="<fmt:formatDate value="${techapply.endApplyDate}" pattern="yyyy-MM-dd"/>"
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
        <th style="width: 250px;">申请编号</th>
        <th style="width: 130px;">项目编号</th>
        <th>项目名称</th>
        <th style="width: 100px;">申请人</th>
        <th style="width: 120px;">申请时间</th>
        <th style="width: 100px;">人力来源</th>
        <th style="width: 80px;">审批状态</th>
        <shiro:hasAnyPermissions name="project:tech:techapply:edit, project:tech:techapply:assigning">
            <th style="width: 100px;">操作</th>
        </shiro:hasAnyPermissions>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="techapply">
        <tr>
            <td>
                <a href="${ctx}/project/tech/techapply/form?id=${techapply.id}">
                    ${techapply.id}
                </a>
            </td>
            <td>
                ${techapply.project.projectCode}
            </td>
            <td class="td-text">
                ${techapply.project.projectName}
            </td>
            <td>
                ${techapply.applicant.name}
            </td>
            <td style="white-space: nowrap;">
                <fmt:formatDate value="${techapply.applyDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                ${techapply.office.name}
            </td>
            
            <c:choose>
				<c:when test="${techapply.processStatus != '2'}">
					<td style="color:red;">
				</c:when>
				<c:otherwise>
					<td>
				</c:otherwise>
			</c:choose>
				${fns:getDictLabel(techapply.processStatus, 'AuditStatus', '')}
			</td>
            
            <shiro:hasPermission name="project:tech:techapply:assigning">
               <%--  <td>
                    <a href="${ctx}/project/tech/assigning/form?id=&techapplyId=${techapply.id}">派工</a>
                </td> --%>
                <td>
                	<c:if test="${techapply.assignFlag eq 0}">
	                    <a href="${ctx}/project/tech/assigning/form?id=&techapplyId=${techapply.id}">派工</a>
            		</c:if>
            		<c:if test="${techapply.assignFlag eq 1}">
	                    已派工
            		</c:if>
                </td>
            </shiro:hasPermission>
            <shiro:hasPermission name="project:tech:techapply:edit">
                <td>
                	<a href="${ctx}/project/tech/techapply/form?id=${techapply.id}">详情</a>
					<c:if test="${techapply.processStatus != '2'}">
						<a class="trace" target="_blank" procInsId="${techapply.act.procInsId}" href="${ctx}/act/task/trace1?procInsId=${techapply.act.procInsId}">跟踪</a>
					</c:if>
					<c:if test="${techapply.processStatus == '2'}">
						<a href="${ctx}/project/tech/techapply/delete?id=${techapply.id}"
                       		onclick="return confirmx('确认要删除该资源申请吗？', this.href)">删除</a>
					</c:if>
                
            <%--     	<a href="${ctx}/project/tech/techapply/form?id=${techapply.id}">详情</a>
    				<a class="trace" target="_blank" procInsId="${techapply.act.procInsId}" href="${ctx}/act/task/trace1?procInsId=${techapply.act.procInsId}">跟踪1</a>
					<a class="trace" target="_blank" procInsId="${techapply.act.procInsId}" href="${ctx}/act/task/trace2?procInsId=${techapply.act.procInsId}">跟踪2</a>
                
                    <a href="${ctx}/project/tech/techapply/delete?id=${techapply.id}"
                       onclick="return confirmx('确认要删除该资源申请吗？', this.href)">删除</a> --%>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>