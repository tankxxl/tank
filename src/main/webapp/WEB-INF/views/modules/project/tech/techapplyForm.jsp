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
        function addRow(list, idx, tpl, row) {
            $(list).append(Mustache.render(tpl, {
                idx: idx, delBtn: true, row: row
            }));
            $(list + idx).find("select").each(function () {
                $(this).val($(this).attr("data-value"));
            });
            $(list + idx).find("input[type='checkbox'], input[type='radio']").each(function () {
                var ss = $(this).attr("data-value").split(',');
                for (var i = 0; i < ss.length; i++) {
                    if ($(this).val() == ss[i]) {
                        $(this).attr("checked", "checked");
                    }
                }
            });
        }
        function delRow(obj, prefix) {
            var id = $(prefix + "_id");
            var delFlag = $(prefix + "_delFlag");
            if (id.val() == "") {
                $(obj).parent().parent().remove();
            } else if (delFlag.val() == "0") {
                delFlag.val("1");
                $(obj).html("&divide;").attr("title", "撤销删除");
                $(obj).parent().parent().addClass("error");
            } else if (delFlag.val() == "1") {
                delFlag.val("0");
                $(obj).html("&times;").attr("title", "删除");
                $(obj).parent().parent().removeClass("error");
            }
        }

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
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/project/tech/techapply/">资源申请列表</a></li>
    <li class="active">
        <a href="${ctx}/project/tech/techapply/form?id=${techapply.id}">
            资源申请
            <shiro:hasPermission name="project:tech:techapply:edit">
                ${not empty techapply.id?'修改':'添加'}
            </shiro:hasPermission>
            <shiro:lacksPermission name="project:tech:techapply:edit">
                查看
            </shiro:lacksPermission>
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
        <caption style="text-align: left" class="td-h">派工明细</caption>
        <thead>
        <tr>
            <th style="width: 10%;">
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
            <td class="tit">
                <c:choose>
                    <c:when test="${not empty techapply.applicant}">
                        <form:hidden path="applicant"/>
                        <input type="text" name="applicant.name" value="${techapply.applicant.name}" class="input-ml"
                               readonly/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="applicant" value="${fns:getUser()}" readonly/>
                        <input type="text" name="applicant.name" value="${fns:getUser().name}" class="input-ml"
                               readonly/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td class="tit">
                申请时间
            </td>
            <td class="tit">
                <c:choose>
                    <c:when test="${not empty techapply.applyDate}">
                        <input type="text" name="applyDate"
                               value="<fmt:formatDate value="${techapply.applyDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                               class="input-ml" readonly/>
                    </c:when>
                    <c:otherwise>
                        <input type="text" name="applyDate" class="input-ml"
                               value="${fns:getDate('yyyy-MM-dd HH:mm:ss')}" readonly/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td class="tit">
                项目名称
            </td>
            <td class="tit">
                <sys:treeselect id="project" name="project.id" value="${techapply.project.id}"
                                labelName="project.projectName" labelValue="${techapply.project.projectName}" title="项目"
                                url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11" cssClass="required"
                                dataMsgRequired="项目必选"
                                allowClear="true" notAllowSelectParent="true" customClick="changeProject"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </td>
            <td class="tit">
                项目编号
            </td>
            <td class="tit">
                <form:input path="project.projectCode" cssClass="input-ml" htmlEscape="false" readonly="true"/>
            </td>
        </tr>
        <tr>
            <td class="tit">
                客户名称
            </td>
            <td class="tit">
                <form:input path="project.customer.customerName" htmlEscape="false" style="margin-left: -60px;"
                            readonly="true" />
            </td>
            <td class="tit">
                客户联系人
            </td>
            <td class="tit">
                <form:input path="project.customerContact.contactName" cssClass="input-ml" htmlEscape="false"
                            readonly="true"/>
            </td>
        </tr>
        <tr>
            <td class="tit">
                项目进展
            </td>
            <td class="tit">
                <form:radiobuttons path="projectProgress" items="${fns:getDictList('project_progress')}"
                                   itemLabel="label" cssClass="required"
                                   itemValue="value" htmlEscape="false"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </td>
            <td class="tit">
                人力来源
            </td>
            <td class="tit">
                <sys:treeselect id="office" name="office.id" value="${techapply.office.id}" labelName="office.name"
                                labelValue="${techapply.office.name}"
                                title="部门" url="/sys/office/treeData?type=2&isAll=1" cssClass="required"
                                allowClear="true" dataMsgRequired="项目必选"
                                notAllowSelectParent="true"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </td>
        </tr>
        <tr>
            <td class="tit">
                申请原因
            </td>
            <td class="tit" colspan="3">
                <form:textarea path="applyReason" htmlEscape="false" rows="4" style="width:85%" maxlength="64"
                               cssClass="required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </td>
        </tr>
            <%--<tr>
                <td class="tit">
                    备注信息
                </td>
                <td class="tit" colspan="3">
                    <form:textarea path="remarks" htmlEscape="false"  style="width:95%" rows="4" maxlength="255"/>
                </td>
            </tr>--%>
        </tbody>
    </table>

    <%--人员需求表--%>
    <table id="contentTable" class="table table-striped table-bordered table-condensed" style="margin-top: 30px;">
        <caption style="text-align: left" class="td-h">人员需求明细</caption>
        <thead>
        <tr>
            <th class="hide"></th>
            <th>级别</th>
            <th>人数</th>
            <th>工作内容</th>
            <th>工作地点</th>
            <th>预计时间(起始)</th>
            <th>预计时间(结束)</th>
            <th>工时(天)</th>
            <shiro:hasPermission name="project:tech:techapply:edit">
                <th width="10">&nbsp;</th>
            </shiro:hasPermission>
        </tr>
        </thead>
        <tbody id="needList">
        </tbody>
        <shiro:hasPermission name="project:tech:techapply:edit">
            <tfoot>
            <tr>
                <td colspan="10" class="tit">
                    <a href="javascript:"
                       onclick="addRow('#needList', needRowIdx, needTpl);needRowIdx = needRowIdx + 1;"
                       class="btn">
                        新增
                    </a>
                </td>
            </tr>
            </tfoot>
        </shiro:hasPermission>
    </table>
    <%--生成单行增加删除脚本--%>
    <script type="text/template" id="needTpl">
        //<!--
            <tr id="needList{{idx}}">
                <td class="hide">
                    <input id="needList{{idx}}_id" name="needList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
                    <input id="needList{{idx}}_delFlag" name="needList[{{idx}}].delFlag" type="hidden" value="0"/>
                </td>
                <td class="tit">
                    <select id="needList{{idx}}_level" name="needList[{{idx}}].level" data-value="{{row.level}}" class="input-small required">
                        <option value=""></option>
                        <c:forEach items="${fns:getDictList('profession_grade')}" var="dict">
                            <option value="${dict.value}">${dict.label}</option>
                        </c:forEach>
                    </select>
                </td>
                <td class="tit">
                    <input id="needList{{idx}}_number" name="needList[{{idx}}].number" type="text" value="{{row.number}}" maxlength="11" class="input-small number required"/>
                </td>
                <td class="tit">
                    <input id="needList{{idx}}_descContent" name="needList[{{idx}}].descContent" type="text" value="{{row.descContent}}" maxlength="255" class="input-small required"/>
                </td>
                <td class="tit">
                    <input id="needList{{idx}}_descLocation" name="needList[{{idx}}].descLocation" type="text" value="{{row.descLocation}}" maxlength="255" class="input-small required"/>
                </td>
                <td class="tit">
                    <input id="needList{{idx}}_descTimeBegin" name="needList[{{idx}}].descTimeBegin" type="text" readonly="readonly" maxlength="20" class="input-small Wdate "
                        value="{{row.descTimeBegin}}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                </td>
                <td class="tit">
                    <input id="needList{{idx}}_descTimeEnd" name="needList[{{idx}}].descTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate "
                        value="{{row.descTimeEnd}}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                </td>
                <td class="tit">
                    <input id="needList{{idx}}_manHour" name="needList[{{idx}}].manHour" type="text" value="{{row.manHour}}" maxlength="11" class="input-small number"/>
                </td>
                <shiro:hasPermission name="project:tech:techapply:edit"><td class="text-center" width="10">
                    {{#delBtn}}<span class="close" onclick="delRow(this, '#needList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
                </td></shiro:hasPermission>
            </tr>
        //-->
    </script>
    <script type="text/javascript">
        var needRowIdx = 0, needTpl = $("#needTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, "");
        $(document).ready(function () {
            var data = ${fns:toJson(techapply.needList)};
            for (var i = 0; i < data.length; i++) {
                addRow('#needList', needRowIdx, needTpl, data[i]);
                needRowIdx = needRowIdx + 1;
            }
        });
    </script>

    <%-- <div class="form-actions">
        <shiro:hasPermission name="project:tech:techapply:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div> --%>
    
    <div class="form-actions">
		<shiro:hasPermission name="project:tech:techapply:edit">
		
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
			<c:if test="${not empty techapply.id}">
				<input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>
	
	<c:if test="${not empty techapply.id}">
		<act:histoicFlow procInsId="${techapply.act.procInsId}" />
	</c:if>
    
</form:form>
</body>
</html>