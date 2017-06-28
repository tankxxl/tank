<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>工单管理</title>
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
        .td-text-multi {
            word-break: break-all;
        }
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/project/tech/workorder/">工单列表</a></li>
</ul>
<form:form id="searchForm" modelAttribute="workorder" action="${ctx}/project/tech/workorder/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>项目名称：</label>
            <sys:treeselect id="project" name="assigning.techapply.project.id"
                            value="${workerorder.assigning.techapply.project.id}"
                            labelName="assigning.techapply.project.projectName"
                            labelValue="${workerorder.assigning.techapply.project.projectName}" title="项目"
                            url="/apply/external/projectApplyExternal/treeData?proMainStage=03" cssClass="required"
                            allowClear="true" notAllowSelectParent="true"/>
        </li>
        <li><label>派工时间：</label>
            <input name="beginWorkorderDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${workorder.beginWorkorderDate}" pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> -
            <input name="endWorkorderDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${workorder.endWorkorderDate}" pattern="yyyy-MM-dd"/>"
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
        <th style="width: 250px;">工单编号</th>
        <th style="width: 100px;">工程师姓名</th>
        <th class="td-text-multi">工作地点</th>
        <th style="width: 120px;">预计起始时间</th>
        <th style="width: 120px;">预计结束时间</th>
        <th style="width: 60px;">预定工时</th>
        <th style="width: 120px;">派工时间</th>
        <th style="width: 60px;">是否完成</th>
        <shiro:hasPermission name="project:tech:workorder:edit">
            <th style="width: 60px;">操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="workorder">
        <tr>
            <td><a href="${ctx}/project/tech/workorder/form?id=${workorder.id}">
                    ${workorder.id}
            </a></td>
            <td>
                    ${workorder.user.name}
            </td>
            <td>
                    ${workorder.descLocation}
            </td>
            <td>
                <fmt:formatDate value="${workorder.descTimeBegin}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                <fmt:formatDate value="${workorder.descTimeEnd}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                    ${workorder.manHour}
            </td>
            <td>
                <fmt:formatDate value="${workorder.workorderDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                    ${fns:getDictLabel(workorder.completed, 'workorder_completed', '')}
            </td>
            <shiro:hasPermission name="project:tech:workorder:edit">
                <td>
                    <c:set var="workorderConfirmed" value="${fns:getDictValue('已确认', 'workorder_confirmed', '0')}"/>
                    <c:choose>
                        <c:when test="${workorderConfirmed eq workorder.confirmed}">
                            已确认
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/project/tech/workorder/confirm?id=${workorder.id}">确认</a>
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