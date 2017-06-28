<%--author: Arthur@jicdata--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>派工管理</title>
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

        // 选择工程师触发事件
        function locateLevel(userId, idx) {
            // 向后台获取人员级别信息，并将相关信息回显
            $.post('${ctx}/salary/relation/userSalaryRelation/getAsJsonByUserId', {userId: userId}, function(data) {
                if (data && data.salary && data.salary.grade) {
                    var id = 'workorderList' + idx + '_level';
                    $($('#' + id)[0]).val(data.salary.grade);
                    $($('#' + id + '_hidden')[0]).val(data.salary.grade);
                }
            })
        }
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
    <li>
        <c:choose>
            <c:when test="${not empty assigning.id}">
                <a href="${ctx}/project/tech/assigning/">派工列表</a>
            </c:when>
            <c:otherwise>
                <a href="${ctx}/project/tech/techapply/">资源申请列表</a>
            </c:otherwise>
        </c:choose>
    </li>
    <li class="active">
        <a href="${ctx}/project/tech/assigning/form?id=${assigning.id}">
            派工
            <shiro:hasPermission name="project:tech:assigning:edit">
                ${not empty assigning.id?'修改':'添加'}
            </shiro:hasPermission>
            <shiro:lacksPermission name="project:tech:assigning:edit">
                查看
            </shiro:lacksPermission>
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

<%--派工操作--%>
<form:form id="inputForm" modelAttribute="assigning" action="${ctx}/project/tech/assigning/save" method="post">
    <form:hidden path="id"/>
    <c:choose>
        <c:when test="${not empty assigning.techapply.id}">
            <form:hidden path="techapply.id"/>
        </c:when>
        <c:otherwise>
            <input type="hidden" name="techapply.id" value="${techapply.id}"/>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${not empty assigning.assigningor.id}">
            <form:hidden path="assigningor.id"/>
        </c:when>
        <c:otherwise>
            <input type="hidden" name="assigningor.id" value="${fns:getUser().id}"/>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${not empty assigning.assigningDate}">
            <input type="hidden" name="assigningDate" value="<fmt:formatDate value="${assigning.assigningDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
        </c:when>
        <c:otherwise>
            <input type="hidden" name="assigningDate" value="${fns:getDate('yyyy-MM-dd HH:mm:ss')}"/>
        </c:otherwise>
    </c:choose>

    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed" style="margin-top: 30px;">
        <caption style="text-align: left" class="td-h">派工明细</caption>
        <thead>
        <tr>
            <th class="hide"></th>
            <th>工程师</th>
            <th>级别</th>
            <th>承担工作</th>
            <th>工作地点</th>
            <th>预计时间(起始)</th>
            <th>预计时间(结束)</th>
            <th>预计工时(天)</th>
            <th class="hide">工单创建时间</th>
            <shiro:hasPermission name="project:tech:assigning:edit">
                <th width="10">&nbsp;</th>
            </shiro:hasPermission>
        </tr>
        </thead>
        <tbody id="workorderList">
        </tbody>
        <shiro:hasPermission name="project:tech:assigning:edit">
            <tfoot>
            <tr>
                <td colspan="10" class="tit">
                    <a href="javascript:"
                       onclick="addRow('#workorderList', workorderRowIdx, workorderTpl);workorderRowIdx = workorderRowIdx + 1;"
                       class="btn">
                        新增
                    </a>
                </td>
            </tr>
            </tfoot>
        </shiro:hasPermission>
    </table>

    <%--生成单行增加删除脚本--%>
    <script type="text/template" id="workorderTpl">
        //<!--
            <tr id="workorderList{{idx}}">
                <td class="hide">
                    <input id="workorderList{{idx}}_id" name="workorderList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
                    <input id="workorderList{{idx}}_delFlag" name="workorderList[{{idx}}].delFlag" type="hidden" value="0"/>
                    <input id="workorderList{{idx}}_workorderDate" name="workorderList[{{idx}}].workorderDate" type="hidden" value="${fns:getDate('yyyy-MM-dd HH:mm:ss')}"/>
                </td>
                <td class="tit">
                    <sys:treeselect id="workorderList{{idx}}_user" name="workorderList[{{idx}}].user.id" value="{{row.user.id}}" labelName="workorderList{{idx}}.user.name" labelValue="{{row.user.name}}"
									title="工程师" url="/sys/office/treeData?type=3" cssClass="input-s" allowClear="true" notAllowSelectParent="true" customClick="locateLevel" idx="{{idx}}"/>
                </td>
                <td class="tit">
                    <select id="workorderList{{idx}}_level" name="workorderList[{{idx}}].level" data-value="{{row.level}}" disabled="disabled" class="input-small required">
                        <option value=""></option>
                        <c:forEach items="${fns:getDictList('profession_grade')}" var="dict">
                            <option value="${dict.value}">${dict.label}</option>
                        </c:forEach>
                    </select>
                    <input id="workorderList{{idx}}_level_hidden" name="workorderList[{{idx}}].level" type="hidden" value="{{row.level}}"/>
                </td>
                <td class="tit">
                    <input id="workorderList{{idx}}_descContent" name="workorderList[{{idx}}].descContent" type="text" maxlength="255" value="{{row.descContent}}" class="input-small "/>
                </td>
                <td class="tit">
                    <input id="workorderList{{idx}}_descLocation" name="workorderList[{{idx}}].descLocation" type="text" value="{{row.descLocation}}" maxlength="255" class="input-small "/>
                </td>
                <td class="tit">
                    <input id="workorderList{{idx}}_descTimeBegin" name="workorderList[{{idx}}].descTimeBegin" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                        value="{{row.descTimeBegin}}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
                </td>
                <td class="tit">
                    <input id="workorderList{{idx}}_descTimeEnd" name="workorderList[{{idx}}].descTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                        value="{{row.descTimeEnd}}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
                </td>
                <td class="tit">
                    <input id="workorderList{{idx}}_manHour" name="workorderList[{{idx}}].manHour" type="text" value="{{row.manHour}}" maxlength="11" class="input-small "/>
                </td>
                <shiro:hasPermission name="project:tech:assigning:edit">
                    <td class="text-center" width="10">
                        {{#delBtn}}<span class="close" onclick="delRow(this, '#workorderList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
                    </td>
                </shiro:hasPermission>
            </tr>
        //-->
    </script>
    <script type="text/javascript">
        var workorderRowIdx = 0, workorderTpl = $("#workorderTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, "");
        $(document).ready(function () {
            var data = ${fns:toJson(assigning.workorderList)};
            for (var i = 0; i < data.length; i++) {
                addRow('#workorderList', workorderRowIdx, workorderTpl, data[i]);
                workorderRowIdx = workorderRowIdx + 1;
            }
        });
    </script>

    <div class="form-actions">
        <shiro:hasPermission name="project:tech:assigning:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>