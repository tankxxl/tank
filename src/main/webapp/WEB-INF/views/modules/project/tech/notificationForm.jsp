<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>知会管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });
    </script>

    <style type="text/css">
        .td-h {
            font-weight: bold;
        }
        .td-text-multi {
            word-break: break-all;
        }
        .input-s {
            width: 120px;
        }
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/project/tech/notification/">知会列表</a></li>
    <li class="active">
        <a href="${ctx}/project/tech/notification/form?id=${notification.id}">知会
            <shiro:hasPermission name="project:tech:notification:edit">
                ${not empty notification.id?'修改':'生成'}
            </shiro:hasPermission>
            <shiro:hasPermission name="project:tech:notification:reply">
                评论
            </shiro:hasPermission>
        </a>
    </li>
</ul>
<br/>

<%--申请基本信息--%>
<table class="table table-striped table-bordered" style="text-align: center; table-layout: fixed">
    <caption style="text-align: left" class="td-h">基本信息</caption>
    <thead>
    <tr style="height: 1px;">
        <th style="width: 15%;">
        </th>
        <th style="width: 30%;">
        </th>
        <th style="width: 15%;">
        </th>
        <th style="width: 30%;">
        </th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td class="tit td-h">
            申请人
        </td>
        <td class="tit">
            ${techapply.applicant.name}
        </td>
        <td class="tit td-h">
            申请时间
        </td>
        <td class="tit">
            <fmt:formatDate value="${techapply.applyDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </td>
    </tr>
    <tr>
        <td class="tit td-h">
            项目名称
        </td>
        <td class="tit">
            ${techapply.project.projectName}
        </td>
        <td class="tit td-h">
            项目编号
        </td>
        <td class="tit">
            ${techapply.project.projectCode}
        </td>
    </tr>
    <tr>
        <td class="tit td-h">
            客户名称
        </td>
        <td class="tit">
            ${techapply.project.customer.customerName}
        </td>
        <td class="tit td-h">
            客户联系人
        </td>
        <td class="tit">
            ${techapply.project.customerContact.contactName}
        </td>
    </tr>
    <tr>
        <td class="tit td-h">
            项目进展
        </td>
        <td class="tit">
            ${fns:getDictLabel(techapply.projectProgress, 'project_progress', '')}
        </td>
        <td class="tit td-h">
            人力来源
        </td>
        <td class="tit">
            ${techapply.office.name}
        </td>
    </tr>
    <tr>
        <td class="tit td-h">
            申请原因
        </td>
        <td class="tit td-text-multi" colspan="3">
            ${techapply.applyReason}
        </td>
    </tr>
    </tbody>
</table>

<%--派工信息--%>
<table class="table table-striped table-bordered" style="text-align: center; table-layout: fixed; margin-top: 30px;">
    <caption style="text-align: left" class="td-h">派工信息</caption>
    <thead>
    <tr style="height: 1px;">
        <th style="width: 15%;">
        </th>
        <th style="width: 35%;">
        </th>
        <th style="width: 15%;">
        </th>
        <th style="width: 35%;">
        </th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td class="tit td-h">
            派工编号
        </td>
        <td class="tit">
            ${assigning.id}
        </td>
        <td class="tit td-h">
            派工人
        </td>
        <td class="tit">
            ${assigning.assigningor.name}
        </td>
    </tr>
    <tr>
        <td class="tit td-h">
            派工时间
        </td>
        <td class="tit">
            <fmt:formatDate value="${assigning.assigningDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </td>
        <td colspan="2"></td>
    </tr>
    </tbody>
</table>

<table class="table table-striped table-bordered" style="text-align: center; table-layout: fixed; margin-top: 30px;">
    <caption style="text-align: left" class="td-h">派工明细</caption>
    <thead>
    <tr>
        <th style="width: 250px;">工单编号</th>
        <th style="width: 100px;">工程师姓名</th>
        <th class="td-text-multi">工作地点</th>
        <th class="td-text-multi">工作内容</th>
        <th style="width: 120px;">预计起始时间</th>
        <th style="width: 120px;">预计结束时间</th>
        <th style="width: 60px;">预定工时</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${workorderList}" var="workorder">
        <tr>
            <td>
                    ${workorder.id}
            </td>
            <td>
                    ${workorder.user.name}
            </td>
            <td>
                    ${workorder.descLocation}
            </td>
            <td>
                    ${workorder.descContent}
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
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:choose>
    <c:when test="${empty notification.id}">
        <c:set var="action" value="save"/>
    </c:when>
    <c:otherwise>
        <shiro:hasPermission name="project:tech:notification:edit">
            <c:set var="action" value="update"/>
        </shiro:hasPermission>
        <shiro:hasPermission name="project:tech:notification:reply">
            <c:set var="action" value="reply"/>
        </shiro:hasPermission>
    </c:otherwise>
</c:choose>

<form:form id="inputForm" modelAttribute="notification" action="${ctx}/project/tech/notification/${action}" method="post"
           class="form-horizontal" style="margin-left: 0px; margin-right: 0px;">
    <form:hidden path="id"/>
    <input type="hidden" name="assigning.id" value="${assigning.id}"/>
    <input type="hidden" name="notificationDate" value="<fmt:formatDate value="${notification.notificationDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
    <input type="hidden" name="replyDate" value="<fmt:formatDate value="${notification.replyDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label td-h">知会内容：</label>
        <div class="controls">
            <shiro:hasPermission name="project:tech:notification:edit">
                <form:textarea path="notification" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge " style="width:55%"/>
            </shiro:hasPermission>
            <shiro:hasPermission name="project:tech:notification:reply">
                <textarea name="notification" rows="4" readonly style="width:55%">${notification.notification}</textarea>
            </shiro:hasPermission>
        </div>
    </div>
    <shiro:hasPermission name="project:tech:notification:reply">
        <div class="control-group">
            <label class="control-label td-h">知会评论：</label>
            <div class="controls">
                <form:textarea path="reply" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "  style="width:55%"/>
            </div>
        </div>
    </shiro:hasPermission>

    <div class="form-actions">
        <shiro:hasPermission name="project:tech:notification:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <shiro:hasPermission name="project:tech:notification:reply">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="评 论"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>