<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>立项申请管理</title>
	<meta name="decorator" content="default"/>

	<script type="text/javascript">

        $(document).ready(function() {
            //$("#name").focus();
            $("#inputForm").validate({
                rules: {
                    estimatedGrossProfitMargin: {
                        required: true,
                        range: [0,100]
                    },
                    ownership: {
                        required: true
                    }
                },
                submitHandler: function(form){
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function(error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });

            $("#category").change(function(){
// 				$("#inputForm").valid().element($("#category"));
                $("#inputForm").validate().element($("#category"));
            });
            $("#ownership").change(function(){
                $("#inputForm").validate().element($("#ownership"));
            });

            // 只能输入数字，并且关闭输入法
            $(".checkNum").keypress(function(event) {
                var keyCode = event.which;
                if (keyCode == 46 || (keyCode >= 48 && keyCode <= 57) || keyCode == 8) // 8是删除键
                    return true;
                else
                    return false;
            }).focus(function() {
                this.style.imeMode = 'disabled';
				/* imeMode有四种形式，分别是：
				 active 代表输入法为中文
				 inactive 代表输入法为英文
				 auto 代表打开输入法 (默认)
				 disable 代表关闭输入法 */
            });
        });
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<c:if test="${ empty projectApplyExternal.act.taskId}">
		<li><a href="${ctx}/apply/external/projectApplyExternal/">立项申请列表</a></li>
	</c:if>
	<li class="active"><a href="${ctx}/apply/external/projectApplyExternal/form?id=${projectApplyExternal.id}">立项申请
		<shiro:hasPermission name="apply:external:projectApplyExternal:edit">
			${not empty projectApplyExternal.id?'审批':'查看'}
		</shiro:hasPermission>
		<shiro:lacksPermission name="apply:external:projectApplyExternal:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectApplyExternal" htmlEscape="false"
		   action="${ctx}/apply/external/projectApplyExternal/saveAudit"
		   method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<form:hidden id="flag" path="act.flag"/>
	<form:hidden path="docPath" />
	<sys:message content="${message}"/>

	<c:set var="rand" value="id"/>
	<%--<c:if test="${}"--%>
	<table class="table-form">
		<caption>项目立项备案审批表</caption>
		<tr>
			<td colspan="1" class="tit">项目编号${rand}</td>
			<td colspan="1" class="" >
				${ projectApplyExternal.projectCode }
			</td>
			<td colspan="1" class="tit">申请部门</td>
			<td colspan="1">
					${fns:getUser().office.name}
			</td>
		</tr>

		<tr>
			<td colspan="1" class="tit" >项目名称</td>
			<td colspan="3">
				${projectApplyExternal.projectName}
			</td>
		</tr>

		<c:if test="${not empty projectApplyExternal.saler.name}">
			<tr>
				<td  class="tit" colspan="1">销售人员</td>
				<td colspan="1">
					<label>${projectApplyExternal.saler.name }</label>
				</td>
				<td  class="tit">部&nbsp;&nbsp;门</td>
				<td colspan="1">
					${projectApplyExternal.saler.office.name  }
				</td>
			</tr>
		</c:if>

		<tr>
			<td  class="tit" colspan="1">项目类别</td>
			<td>
				${fns:getDictLabel(projectApplyExternal.category , 'pro_category', '')}
			</td>
			<td  class="tit">是否涉及自研</td>
			<td  class="">
				${fns:getDictLabel(projectApplyExternal.selfDev , 'yes_no', '')}
			</td>
		</tr>

		<tr>
			<td  class="tit"  colspan="1">预计合同金额</td>
			<td class="">
				<div class="input-append">
					<form:input path="estimatedContractAmount" readonly="true" class="checkNum input-medium"/><span class="add-on">元</span>
				</div>
			</td>
			<td  class="tit">预计公司利润率</td>
			<td class="">
				<div class="input-append">
					<form:input path="estimatedGrossProfitMargin"  style="width:80px" readonly="true" class="checkNum input-medium"/>
					<span class="add-on">%</span>
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit">预计开始时间</td>
			<td>
				<fmt:formatDate value="${projectApplyExternal.beginDate}" pattern="yyyy-MM-dd"/>
			</td>
			<td class="tit">预计截止时间</td>
			<td>
				<fmt:formatDate value="${projectApplyExternal.endDate}" pattern="yyyy-MM-dd"/>
			</td>
		</tr>

		<tr>
			<td class="tit">项目经理</td>
			<td>
				${projectApplyExternal.projectManager.name}
			</td>

			<td class="tit">项目组成员</td>
			<td>
				<sys:treeselect id="projectMembers" name="projectMembers"
								value="${projectApplyExternal.projectMembers}" labelName="projectMembers"
								labelValue="${projectApplyExternal.membersName}"
								checked="true"
								dataMsgRequired="项目成员必填" title="项目成员" url="/sys/office/treeData?type=3"
								cssClass="required"  allowClear="true" notAllowSelectParent="true" />
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目开展背景、概述</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					${projectApplyExternal.description}
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目业务模式/产品形式</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					${projectApplyExternal.pattern}
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目目标/阶段性目标</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					${projectApplyExternal.target}
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目盈利分析</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					${projectApplyExternal.analysis}
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit">项目需要资源</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					${projectApplyExternal.resource}
				</div>
			</td>
		</tr>

		<tr>
			<td  class="tit">项目风险预测</td>
			<td  colspan="3">
				<div style="white-space:nowrap;">
					${projectApplyExternal.riskAnalysis}
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit" >文件附件</td>
			<td   colspan="3">
				<form:hidden id="documentAttachmentPath" path="documentAttachmentPath" htmlEscape="false" maxlength="20000"  />
				<sys:ckfinder input="documentAttachmentPath" type="files"
							  uploadPath="/apply"
							  readonly="true"
							  selectMultiple="true" />
			</td>
		</tr>

		<c:if test="${not empty projectApplyExternal.act.taskId && projectApplyExternal.act.status != 'finish'}">
			<tr>
				<td class="tit">您的意见</td>
				<td colspan="3">
					<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
					<span class="help-inline"><font color="red">*</font></span>
				</td>
			</tr>
		</c:if>

		<tr>
			<td  class="tit" colspan="4">填表说明</td>
		</tr>
		<tr>
			<td colspan="4">
			<span class="help-block" >
				1、立项经过备案后，方可进入下一步会签审批环节。<br>
				2、立项通过审批后，原件由市场营销中心存档。（申请部门、运营管理部、财务部均留存复印件）<br>
				3、如对项目信息有更详细的说明，可附页说明，其他文档作为附件提交。<br>
				4、本审批表需按审批栏逐级审批。<br>
				5、项目测算表请作为附件在审批中一并提交。<br>
				6、打印要求：项目评审会表打印时需双面打印。
			</span>
			</td>
		</tr>
	</table>

	<div class="form-actions">
		<shiro:hasPermission name="apply:external:projectApplyExternal:edit">

			<c:if test="${not empty projectApplyExternal.act.taskId && projectApplyExternal.act.status != 'finish'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
				<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectApplyExternal.id}">
		<act:histoicFlow procInsId="${projectApplyExternal.processInstanceId}" />
	</c:if>
</form:form>
</body>
</html>