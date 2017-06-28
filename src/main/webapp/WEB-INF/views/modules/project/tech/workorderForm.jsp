<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>工单确认</title>
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
<c:set var="workorderConfirmed" value="${fns:getDictValue('已确认', 'workorder_confirmed', '0')}"/>
<c:choose>
    <c:when test="${workorderConfirmed eq workorder.confirmed}">
        <c:set var="confirmed" value="${true}"/>
    </c:when>
    <c:otherwise>
        <c:set var="confirmed" value="${false}"/>
    </c:otherwise>
</c:choose>

<ul class="nav nav-tabs">
    <li><a href="${ctx}/project/tech/workorder/">工单列表</a></li>
    <li class="active">
        <a href="${ctx}/project/tech/workorder/form?id=${workorder.id}">
            工单
            <c:choose>
                <c:when test="${true eq confirmed}">
                    查看
                </c:when>
                <c:otherwise>
                    确认
                </c:otherwise>
            </c:choose>

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

<%--工单信息--%>
<table class="table table-striped table-bordered" style="text-align: center; table-layout: fixed; margin-top: 30px;">
    <caption style="text-align: left" class="td-h">工单信息</caption>
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
            工单编号
        </td>
        <td class="tit">
            ${workorder.id}
        </td>
        <td class="tit td-h">
            工程师姓名
        </td>
        <td class="tit">
            ${workorder.user.name}
        </td>
    </tr>
    <tr>
        <td class="tit td-h ">
            工作地点
        </td>
        <td class="tit td-text-multi">
            ${workorder.descLocation}
        </td>
        <td class="tit td-h">
            预定工时
        </td>
        <td class="tit">
            ${workorder.manHour}
        </td>
    </tr>
    <tr>
        <td class="tit td-h">
            预计起始工作
        </td>
        <td class="tit">
            <fmt:formatDate value="${workorder.descTimeBegin}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </td>
        <td class="tit td-h">
            预计结束时间
        </td>
        <td class="tit">
            <fmt:formatDate value="${workorder.descTimeEnd}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </td>
    </tr>
    <tr>
        <td class="tit td-h">
            工作内容
        </td>
        <td class="tit td-text-multi" colspan="3">
            ${workorder.descContent}
        </td>
    </tr>

    </tbody>
</table>

<%--确认--%>
<form:form id="inputForm" modelAttribute="workorder" action="${ctx}/project/tech/workorder/confirm" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="form-actions">
        <c:if test="${false eq confirmed}">
            <shiro:hasPermission name="project:tech:workorder:edit">
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="确 认"/>&nbsp;
            </shiro:hasPermission>
        </c:if>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>