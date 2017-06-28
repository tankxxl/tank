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
    <li class="active"><a>资源申请审批</a></li>
    <%-- <li class="active">
        <a href="${ctx}/project/tech/techapply/form?id=${techapply.id}">
            资源申请查看
        </a>
    </li> --%>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="techapply" action="${ctx}/project/tech/techapply/saveAudit" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<form:hidden id="flag" path="act.flag"/>
	
<%--     <input type="hidden" name="assigningor.id" value="${fns:getUser().id}"/> --%>
<%--     <input type="hidden" name="assigningDate" value="${fns:getDate('yyyy-MM-dd HH:mm:ss')}"/> --%>
	
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
	                <fmt:formatDate value="${need.descTimeBegin}" pattern="yyyy-MM-dd"/>
	            </td>
	            <td>
	                <fmt:formatDate value="${need.descTimeEnd}"  pattern="yyyy-MM-dd"/>
	            </td>
	            <td>
	                    ${need.manHour}
	            </td>
	        </tr>
	    </c:forEach>
	    </tbody>
	</table>


	
<%--派工操作--%>
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
									title="工程师" url="/sys/office/treeData?type=3" cssClass="input-small " allowClear="true" notAllowSelectParent="true" customClick="locateLevel" idx="{{idx}}"/>
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
                    <input id="workorderList{{idx}}_descTimeBegin" name="workorderList[{{idx}}].descTimeBegin" type="text" readonly="readonly" maxlength="20" class="input-small Wdate "
                        value="{{row.descTimeBegin}}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                </td>
                <td class="tit">
                    <input id="workorderList{{idx}}_descTimeEnd" name="workorderList[{{idx}}].descTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate "
                        value="{{row.descTimeEnd}}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                </td>
                <td class="tit">
                    <input id="workorderList{{idx}}_manHour" name="workorderList[{{idx}}].manHour" type="text" value="{{row.manHour}}" maxlength="11" class="number input-small "/>
                </td>
                    <td class="text-center" width="10">
                        {{#delBtn}}<span class="close" onclick="delRow(this, '#workorderList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
                    </td>
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
    <%-- <div class="form-actions">
        <shiro:hasPermission name="project:tech:techapply:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div> --%>
    <tr>
		<td class="tit">您的意见</td>
		<td colspan="6">
			<form:textarea path="act.comment" class="required" rows="5" maxlength="20" cssStyle="width:500px"/>
			<span class="help-inline"><font color="red">*</font></span>
		</td>
	</tr>
    <div class="form-actions">
    
    <c:if test="${techapply.act.taskDefKey eq 'apply_end'}">
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="兑 现" onclick="$('#flag').val('yes')"/>&nbsp;
	</c:if>
	<c:if test="${techapply.act.taskDefKey ne 'apply_end'}">
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
		<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
	</c:if>
				
			<shiro:hasPermission name="project:tech:techapply:edit">
			
				<%-- <c:if test="${techapply.act.taskDefKey eq 'apply_end'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="兑 现" onclick="$('#flag').val('yes')"/>&nbsp;
				</c:if>
				<c:if test="${techapply.act.taskDefKey ne 'apply_end'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
					<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
				</c:if> --%>
			
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
		</div>
	<c:if test="${not empty techapply.id}">
		<act:histoicFlow procInsId="${techapply.act.procInsId}" />
	</c:if>
    
</form:form>
</body>
</html>