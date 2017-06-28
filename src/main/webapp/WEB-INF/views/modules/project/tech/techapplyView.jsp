<%--author: Arthur@jicdata--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>资源申请管理</title>
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

        // 选择项目触发事件
        function changeProject(projectId, idx) {
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson', {id: projectId}, function (data) {
                if (data.projectCode) {
                    $('#project\\.projectCode').val(data.projectCode);
                }
                if (data.customer && data.customer.customerName) {
                    $('#project\\.customer\\.customerName').val(data.customer.customerName);
                }
                if (data.customerContact && data.customerContact.contactName) {
                    $('#project\\.customerContact\\.contactName').val(data.customerContact.contactName);
                }
            })
        }
    </script>
    <style type="text/css">
        .input-ml {
            margin-left: -60px;
        }
        .td-h {
            font-weight: bold;
        }
        <style type="text/css">
		.tit_content{
			text-align:center
		}
	</style>
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/project/tech/techapply/">资源申请列表</a></li>
    <li class="active">
        <a href="${ctx}/project/tech/techapply/form?id=${techapply.id}">
            资源申请查看
        </a>
    </li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="techapply" action="${ctx}/project/tech/techapply/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>

    <%--申请基本信息--%>
    <table class="table-form" style="text-align: center;">
        <thead>
            <tr>
                <th style="width: 10%;">
                    基本信息
                </th>
                <th>
                </th>
                <th style="width: 10%;">
                </th>
                <th>
                </th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td class="tit">
                    申请人
                </td>
                <td class="tit_content">
                     ${techapply.applicant.name}
                </td>
                <td class="tit">
                    申请时间
                </td>
                <td class="tit_content">
                      <fmt:formatDate value="${techapply.applyDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
            </tr>
            <tr>
                <td class="tit">
                    项目名称
                </td>
                <td class="tit_content">
                	${techapply.project.projectName}
                </td>
                <td class="tit">
                    项目编号
                </td>
                <td class="tit_content">
                	${techapply.project.projectCode}
                </td>
            </tr>
            <tr>
                <td class="tit">
                    客户名称
                </td>
                <td class="tit">
                	${techapply.project.customer.customerName}
                </td>
                <td class="tit">
                    客户联系人
                </td>
                <td class="tit">
                	${techapply.project.customerContact.contactName}
                </td>
            </tr>
            <tr>
                <td class="tit">
                    项目进展
                </td>
                <td class="tit_content">
                	${fns:getDictLabel(techapply.projectProgress, 'project_progress', '')}
                </td>
                <td class="tit">
                    人力来源
                </td>
                <td class="tit_content">
                	${techapply.office.name}
                </td>
            </tr>
            <tr>
                <td class="tit">
                    申请原因
                </td>
                <td class="tit" colspan="3">
                    <form:textarea path="applyReason" htmlEscape="false" rows="4" style="width:95%" maxlength="64"  readonly="true"/>
                </td>
            </tr>
            <tr>
                <td class="tit">
                    备注信息
                </td>
                <td class="tit" colspan="3">
                    <form:textarea path="remarks" htmlEscape="false"  style="width:95%" rows="4" maxlength="255"   readonly="true"/>
                </td>
            </tr>
        </tbody>
    </table>

	<%--人员需求表--%>
	<table class="table table-striped table-bordered table-condensed" style="table-layout: fixed; margin-top: 30px;">
    <caption style="text-align: left" class="td-h">人员需求明细</caption>
    <thead>
	    <tr>
	        <th style="width: 65px;">级别</th>
	        <th style="width: 65px;">人数</th>
	        <th class="td-text-multi">工作内容</th>
	        <th class="td-text-multi">工作地点</th>
	        <th style="width: 120px;">预计时间(起始)</th>
	        <th style="width: 120px;">预计时间(结束)</th>
	        <th style="width: 65px;">工时(天)</th>
	    </tr>
	    </thead>
	    <tbody>
	    <c:forEach items="${techapply.needList}" var="need">
	        <tr>
	            <td>
	                    ${fns:getDictLabel(need.level, 'profession_grade', '')}
	            </td>
	            <td>
	                    ${need.number}
	            </td>
	            <td>
	                    ${need.descContent}
	            </td>
	            <td>
	                    ${need.descLocation}
	            </td>
	            <td>
	                <fmt:formatDate value="${need.descTimeBegin}" pattern="yyyy-MM-dd HH:mm:ss"/>
	            </td>
	            <td>
	                <fmt:formatDate value="${need.descTimeEnd}"  pattern="yyyy-MM-dd HH:mm:ss"/>
	            </td>
	            <td>
	                    ${need.manHour}
	            </td>
	        </tr>
	    </c:forEach>
	    </tbody>
	</table>


    <%-- <div class="form-actions">
        <shiro:hasPermission name="project:tech:techapply:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div> --%>
    
    <div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>
	
	<c:if test="${not empty techapply.id}">
		<act:histoicFlow procInsId="${techapply.act.procInsId}" />
	</c:if>
    
</form:form>
</body>
</html>