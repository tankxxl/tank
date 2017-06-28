<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>工时管理</title>
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
            
            $("#btnSubmit4pass,#btnSubmit4fail").click(function(){
            	$("#auditState").val($(this).attr("autoFlag"));
            	$("#inputForm").submit();
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
    <li><a href="${ctx}/project/tech/manhour/">工时列表</a></li>
    <li class="active">
        <a href="${ctx}/project/tech/manhour/form?id=${manhour.id}">工时
<%--             <shiro:hasPermission name="project:tech:manhour:edit"> --%>
<!--                 修改 -->
<%--             </shiro:hasPermission> --%>
            <shiro:lacksPermission name="project:tech:manhour:audit">
				修改
			</shiro:lacksPermission>
            <shiro:hasPermission name="project:tech:manhour:audit">
                审批
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

<%--工时修改/审批--%>
<form:form id="inputForm" modelAttribute="manhour" action="${ctx}/project/tech/manhour/update" method="post" cssClass="form-horizontal"
        style="margin-left: 0px; margin-right: 0px;">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <table class="table table-striped table-bordered" style="margin-top: 30px;">
        <thead>
        <tr>
            <th>
                工单编号
            </th>
            <th>
                工时日期
            </th>
            <th>
                当日工时
            </th>
<!--             <th> -->
<!--                 备注 -->
<!--             </th> -->
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>
                ${manhour.workorder.id}
            </td>
            <td>
                <fmt:formatDate value="${manhour.manhourDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                <shiro:hasPermission name="project:tech:manhour:audit">
                	${manhour.manhour}
                </shiro:hasPermission>
                <shiro:lacksPermission name="project:tech:manhour:audit">
                    <form:input path="manhour" htmlEscape="false" maxlength="11"/>
                </shiro:lacksPermission>
            </td>
<!--             <td> -->
<%--                 <shiro:hasPermission name="project:tech:manhour:edit"> --%>
<%--                     <form:input path="remarks" htmlEscape="false" maxlength="255"/> --%>
<%--                 </shiro:hasPermission> --%>
<%--                 <shiro:lacksPermission name="project:tech:manhour:edit"> --%>
<%--                     ${manhour.remarks} --%>
<%--                 </shiro:lacksPermission> --%>
<!--             </td> -->
        </tr>
        </tbody>
    </table>
	<shiro:lacksPermission name="project:tech:manhour:audit">
		${fns:getDictLabel(manhour.auditState, 'audit_state', '')}
	</shiro:lacksPermission>
    <shiro:hasPermission name="project:tech:manhour:audit">
<!--         <div class="control-group"> -->
<!--             <label class="control-label td-h">审批结果</label> -->
<!--             <div class="controls"> -->
<%--                 <form:radiobuttons path="auditState" items="${fns:getDictList('audit_state')}" --%>
<%--                                    itemLabel="label" --%>
<%--                                    itemValue="value" htmlEscape="false"/> --%>
<!--             </div> -->
<!--         </div> -->
        <div class="control-group">
            <label class="control-label td-h">审批意见:</label>
            <div class="controls">
            <c:choose>
                <c:when test="${fns:getDictValue('未审批', 'audit_state', '0') eq manhour.auditState}">
                	<form:textarea path="auditOpinion" htmlEscape="false" style="width: 55%" rows="4" maxlength="255" class="input-xxlarge required"/>
                	<span class="help-inline"><font color="red">*</font> </span>
                </c:when>
                <c:otherwise>
                	${manhour.auditOpinion }
                </c:otherwise>
            </c:choose>
                
            </div>
        </div>
    </shiro:hasPermission>

    <div class="form-actions">
    	<shiro:lacksPermission name="project:tech:manhour:audit">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
		</shiro:lacksPermission>
<%--         <shiro:hasPermission name="project:tech:manhour:edit"> --%>
<!--             <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp; -->
<%--         </shiro:hasPermission> --%>
        <shiro:hasPermission name="project:tech:manhour:audit">
            <c:choose>
                <c:when test="${fns:getDictValue('未审批', 'audit_state', '0') eq manhour.auditState}">
                	<input type="hidden" id="auditState" name="auditState"/>
                    <input id="btnSubmit4pass" class="btn btn-primary" type="button" autoFlag="${fns:getDictValue('审核通过', 'audit_state', '1')}" value="审 批 通过"/>&nbsp;
                    <input id="btnSubmit4fail" class="btn btn-primary" type="button" autoFlag="${fns:getDictValue('审批未通过', 'audit_state', '2')}" value="审 批 不通过"/>&nbsp;
                </c:when>
                <c:otherwise>
                    <input id="btnSubmit" class="btn btn-primary" type="submit" value="已审批" disabled="disabled"/>&nbsp;
                </c:otherwise>
            </c:choose>
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>