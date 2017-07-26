<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>合同管理</title>
	<meta name="decorator" content="default"/>
	<%-- 服务合同、管理合同、消费金融合同共用 --%>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();

            $("#rmb").text(digitUppercase(${projectContract.amount }));

			$("#inputForm").validate({
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

			//
            $("#resignFlag").change(function(){
//                    top.$.jBox.tip("select.value=" + this.value);
				if (this.value == 1) {
                    addRequired("#originCode");
//                    $("#originCode").after("<span class='help-inline'><font color='red'>*</font> </span>");
//                    $("#originCode").addClass('required');
				} else {
                    removeRequired("#originCode");
//                    $("#originCode").nextAll().remove();
//                    $("#originCode").removeClass('required');
				}
            });

//            digitUppercase
			$("#amount").keyup(function () {
//                top.$.jBox.tip("input.value=" + digitUppercase(parseFloat(this.value)));
				$("#rmb").text(digitUppercase(parseFloat(this.value)));
            });
		});

        // 选择项目后触发事件
        function changeProject(projectId, idx) {
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {
                    $("#project_code").text(apply.projectCode);
//                    $("#customer_name").text(apply.customer.customerName);
//                    $("#customer_contact_name").text(apply.customerContact.contactName);
//                    $("#customer_phone").text(apply.customerContact.phone);
//                    $("#project_category").text(apply.category);
            });
        }
	</script>

	<style type="text/css">
		tit{
			background:#fafafa;
			text-align:center;
			/*padding-right:8px;*/
			/*white-space:nowrap;*/
		}
	</style>
</head>
<body>
<ul class="nav nav-tabs">
	<c:if test="${ empty projectContract.act.taskId}">
		<li><a href="${ctx}/project/contract/projectContract/">合同列表</a></li>
	</c:if>
	<li class="active"><a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}&contractType=1">
		${fns:getDictLabel(projectContract.contractType, 'jic_contract_type', '合同')}
		<shiro:hasPermission name="project:contract:projectContract:edit">
			${not empty projectContract.id?'修改':'添加'}
		</shiro:hasPermission>
		<shiro:lacksPermission name="project:contract:projectContract:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectContract" htmlEscape="false"
		   action="${ctx}/project/contract/projectContract/save" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<%-- 写到form里的参数才会传到后台，其它参数一概不管 --%>
	<form:hidden path="contractType"/>
	<form:hidden id="flag" path="act.flag"/>
	<sys:message content="${message}"/>
	<table class="table-form">
		<%--<tr><th colspan="6" class="tit">项目信息</th></tr>--%>
		<caption>项目信息</caption>
		<tr>
			<td class="tit">项目名称</td>
			<td >
				<sys:treeselect id="apply" name="apply.id"
					value="${projectContract.apply.id}"
					labelName="apply.projectName"
					labelValue="${projectContract.apply.projectName}"
					title="项目名称"

					url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11&isAll=true"
					cssClass="required"  allowClear="true" notAllowSelectParent="true"
					customClick="changeProject"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>

			<td class="tit">项目编码</td>
			<td ><label id="project_code" >${projectContract.apply.projectCode }</label></td>
		</tr>

		<tr>
			<%--<td class="tit">合同编号</td>--%>
			<%--<td colspan="1" class="">--%>
				<%--<form:input path="contractCode" style="width:90%"/>--%>
			<%--</td>--%>

			<td class="tit">合同类型</td>
			<td colspan="1" class="">
				${fns:getDictLabel(projectContract.contractType, 'jic_contract_type', '')}
			</td>
		</tr>

		<c:if test="${projectContract.contractType eq '2'}">
			<tr>
				<td class="tit">事权审批OA号</td>
				<td colspan="3" class="">
					<form:input path="oaNo" style="width:90%"/>
				</td>
			</tr>
		</c:if>

		<tr>
			<td class="tit">申请部门</td>
			<td class=""><label id="customer_name">${projectContract.apply.customer.customerName }</label></td>
			<td class="tit">申请日期</td>
			<td class=""><label id="customer_contact_name">${projectContract.apply.customerContact.contactName }</label></td>
		</tr>

		<tr>
			<td class="tit">合同对方名称</td>
			<td colspan="3" class="">
				<form:input path="clientName" style="width:90%"/>
			</td>
		</tr>

		<tr>
			<td class="tit">合同总金额</td>
			<td colspan="1" class="">
				<div class="input-append">
					<form:input path="amount" style="width:122px" maxlength="10" number="true" min="0" max="99999999" class="checkNum input-medium"/><span class="add-on">元</span>
				</div>
			</td>
			<td class="tit">大写</td>
			<td colspan="1" class=""><label id="rmb">${projectContract.apply.customer.customerName }</label></td>
		</tr>

		<tr>
			<td  class="tit" >合同金额明细</td>
			<td  colspan="3"><form:textarea path="amountDetail" style="width:98%" maxlength="255"/></td>
		</tr>

		<tr>
			<td class="tit">合同有效期</td>
			<td colspan="1" class="">
				<input name="beginDate" type="text" readonly="readonly" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${projectContract.beginDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</td>
			<td class="tit">至</td>
			<td colspan="1" class="">
				<input name="endDate" type="text" readonly="readonly" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${projectContract.endDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</td>
		</tr>

		<tr>
			<td  class="tit" >合同内容摘要</td>
			<td  colspan="3"><form:textarea path="contentSummary" style="width:98%" maxlength="255"/></td>
		</tr>

		<tr>
			<td  class="tit" >是否为续签合同</td>
			<td  colspan="1">
				<form:select path="resignFlag" class="input-medium required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value"/>
				</form:select>
				&nbsp;&nbsp;&nbsp;&nbsp;注：若为续签合同，请标明续签合同的合同号
			</td>
			<td  class="tit" >原合同号</td>
			<td  colspan="1"><form:input path="originCode" style="width:90%"/></td>
		</tr>
		<tr>
			<td  class="tit" >续签合同说明</td>
			<td  colspan="3"><form:textarea path="resignInfo" style="width:98%" maxlength="255"
			placeholder="若续签合同的相关条款与原合同不一致，请进行说明"/></td>
		</tr>

		<tr>
			<td  class="tit" >印章类型</td>
			<td  colspan="1">
				<form:checkboxes path="sealType" items="${fns:getDictList('jic_seal_type')}" itemLabel="label" itemValue="value" />
			</td>
		</tr>

		<tr>
			<td class="tit" >文件附件</td>
			<td  colspan="3">
				<form:hidden id="attachment" path="attachment" maxlength="20000"  />
				<sys:ckfinder input="attachment" type="files" uploadPath="/project/contract/projectContract"
					selectMultiple="true" />
			</td>
		</tr>
	</table>
	<br/>
	<div class="form-actions">
		<shiro:hasPermission name="project:contract:projectContract:edit">
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
			<c:if test="${not empty projectContract.id}">
				<input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>

		<shiro:hasPermission name="apply:external:projectApplyExternal:super">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectContract.id}">
		<act:histoicFlow procInsId="${projectContract.procInsId}" />
	</c:if>
</form:form>
</body>
</html>