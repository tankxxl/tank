<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>派工管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

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
    <li class="active"><a href="${ctx}/project/tech/assigning/">派工列表</a></li>
</ul>
<form:form id="searchForm" modelAttribute="assigning" action="${ctx}/project/tech/assigning/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>项目名称：</label>
            <sys:treeselect id="project" name="techapply.project.id" value="${assigning.techapply.project.id}"
                            labelName="techapply.project.projectName"
                            labelValue="${assigning.techapply.project.projectName}" title="项目"
                            url="/apply/external/projectApplyExternal/treeAllData?proMainStage=03" cssClass="required"
                            allowClear="true" notAllowSelectParent="true"/>
        </li>
        <li><label>申请人：</label>
            <sys:treeselect id="applicant" name="techapply.applicant.id"
                            value="${assigning.techapply.applicant.id}" labelName="techapply.applicant.name"
                            labelValue="${assigning.techapply.applicant.name}"
                            title="用户" url="/sys/office/treeData?type=3&isAll=1" cssClass="input-small" allowClear="true"
                            notAllowSelectParent="true"/>
        </li>
        <li><label>派工时间：</label>
            <input name="beginAssigningDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${techapply.beginAssigningDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> -
            <input name="endAssigningDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${techapply.endAssigningDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
<!--         <th style="width: 210px;">派工编号</th> -->
        <th style="width: 130px;">项目编号</th>
        <th>项目名称</th>
        <th style="width: 100px;">申请人</th>
<!--         <th style="width: 120px;">申请时间</th> -->
<!--         <th style="width: 100px;">派工人</th> -->
        <th style="width: 150px;">派工时间</th>
        <th>是否完成</th>
        <shiro:hasPermission name="project:tech:assigning:edit">
            <th style="width: 120px;">操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="assigning">
        <tr>
<!--             <td> -->
<%--                 <a href="${ctx}/project/tech/assigning/form?id=${assigning.id}"> --%>
<%--                         ${assigning.id} --%>
<!--                 </a> -->
<!--             </td> -->
            <td>
            	<a href="${ctx}/project/tech/assigning/form?id=${assigning.id}">
                    ${assigning.techapply.project.projectCode}
                </a>
            </td>
            <td class="td-text">
                    ${assigning.techapply.project.projectName}
            </td>
            <td>
                    ${assigning.techapply.applicant.name}
            </td>
<!--             <td> -->
<%--                 <fmt:formatDate value="${assigning.techapply.applyDate}" pattern="yyyy-MM-dd HH:mm:ss"/> --%>
<!--             </td> -->
<!--             <td> -->
<%--                     ${assigning.assigningor.name} --%>
<!--             </td> -->
            <td>
                <fmt:formatDate value="${assigning.assigningDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                    ${fns:getDictLabel(assigning.completed, "workorder_completed", "0")}
            </td>
            <shiro:hasPermission name="project:tech:assigning:edit">
                <td>
                    <c:if test="${fns:getDictValue('未完成', 'workorder_completed', '0') eq assigning.completed}">
                        <a href="${ctx}/project/tech/notification/form?id=&assigningId=${assigning.id}">完成/知会</a>
                    </c:if>
                    <a href="${ctx}/project/tech/assigning/form?id=${assigning.id}">修改</a>
<%--                     <a href="${ctx}/project/tech/assigning/delete?id=${assigning.id}" --%>
<!--                        onclick="return confirmx('确认要删除该派工单吗？', this.href)">删除</a> -->
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>