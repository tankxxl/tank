<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>合同管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
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
		});

		function addRow(list, idx, tpl, row){
			$(list).append(Mustache.render(tpl, {
				idx: idx, delBtn: true, row: row
			}));
			$(list+idx).find("select").each(function(){
				$(this).val($(this).attr("data-value"));
			});
			$(list+idx).find("input[type='checkbox'], input[type='radio']").each(function(){
				var ss = $(this).attr("data-value").split(',');
				for (var i=0; i<ss.length; i++){
					if($(this).val() == ss[i]){
						$(this).attr("checked","checked");
					}
				}
			});
		}
		function delRow(obj, prefix){
			var id = $(prefix+"_id");
			var delFlag = $(prefix+"_delFlag");
			if (id.val() == ""){
				$(obj).parent().parent().remove();
			}else if(delFlag.val() == "0"){
				delFlag.val("1");
				$(obj).html("&divide;").attr("title", "撤销删除");
				$(obj).parent().parent().addClass("error");
			}else if(delFlag.val() == "1"){
				delFlag.val("0");
				$(obj).html("&times;").attr("title", "删除");
				$(obj).parent().parent().removeClass("error");
			}
		}

        // 选择项目后触发事件
        function changeProject(projectId, idx) {
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {
                    $("#project_code").text(apply.projectCode);
                    $("#customer_name").text(apply.customer.customerName);
                    $("#customer_contact_name").text(apply.customerContact.contactName);
                    $("#customer_phone").text(apply.customerContact.phone);
                    $("#project_category").text(apply.category);
            });
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<c:choose>
		<c:when test="${ empty projectContract.act.taskId}">
			<li><a href="${ctx}/project/contract/projectContract/">合同列表</a></li>
			<li class="active"><a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}">
				合同
				<shiro:hasPermission name="project:contract:projectContract:edit">
					${not empty projectContract.id?'查看':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="project:contract:projectContract:edit">查看</shiro:lacksPermission></a></li>
		</c:when>
		<c:otherwise>
			<li class="active"><a>合同
				<shiro:hasPermission name="project:contract:projectContract:edit">
					${not empty projectContract.id?'审批':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="project:contract:projectContract:edit">查看</shiro:lacksPermission></a></li>
		</c:otherwise>
	</c:choose>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectContract" htmlEscape="false"
		   action="${ctx}/project/contract/projectContract/saveAudit" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<form:hidden id="flag" path="act.flag"/>
	<sys:message content="${message}"/>
	<table class="table-form">
		<%--<tr><th colspan="6" class="tit">项目信息</th></tr>--%>
		<caption>项目信息</caption>
		<tr>
			<td class="tit">项目名称</td>
			<td >${projectContract.apply.projectName}</td>

			<td class="tit">项目编码</td>
			<td ><label id="project_code" >${projectContract.apply.projectCode }</label></td>

			<td class="tit">项目类型</td>
			<td ><label id="project_category">${fns:getDictLabel(projectContract.apply.category , 'pro_category', '')}</label></td>
		</tr>

		<tr>
			<td class="tit">客户名称</td>
			<td class=""><label id="customer_name">${projectContract.apply.customer.customerName }</label></td>
			<td class="tit">客户联系人</td>
			<td class=""><label id="customer_contact_name">${projectContract.apply.customerContact.contactName }</label></td>
			<td class="tit">客户电话</td>
			<td class=""><label id="customer_phone">${projectContract.apply.customer.phone }</label></td>
		</tr>

		<c:if test="${not empty  projectContract.projectManager.id}">
			<tr>
				<td class="tit">实施项目经理</td>
				<td colspan="1">${projectContract.projectManager.name}</td>
				<td colspan="4"/>
			</tr>
		</c:if>

		<c:if test="${projectContract.act.taskDefKey eq 'usertask_software_development_leader'}">
		<tr>
			<td class="tit">指定实施项目经理</td>
			<td colspan="1">
				<sys:treeselect id="projectManager" name="projectManager.id"
								value="${projectContract.projectManager.id}" labelName="projectManager.name"
								labelValue="${projectContract.projectManager.name}"
								dataMsgRequired="经理必填" title="经理" url="/sys/office/treeData?type=3" cssClass="required"  allowClear="true" notAllowSelectParent="true" />
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>
		</c:if>
	<c:if test="${projectContract.act.taskDefKey eq 'usertask_service_delivery_leader'}">
		<tr>
			<td class="tit">指定实施项目经理</td>
			<td colspan="1">
				<sys:treeselect id="projectManager" name="projectManager.id"
								value="${projectContract.projectManager.id}" labelName="projectManager.name"
								labelValue="${projectContract.projectManager.name}"
								dataMsgRequired="经理必填" title="经理" url="/sys/office/treeData?type=3" cssClass="required"  allowClear="true" notAllowSelectParent="true" />
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>
	</c:if>

		<tr>
			<td  class="tit" rowspan="2" >说明</td>
			<td  colspan="5"><label class="small_label">（没有投标的，须加以说明）</label></td>
		</tr>

		<tr>
			<td  class="tit" colspan="5">
				<div style="white-space:nowrap;">
				${projectContract.remarks}
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit" >文件附件</td>
			<td   colspan="5">
				<form:hidden id="attachment" path="attachment" maxlength="20000"  />
				<sys:ckfinder input="attachment" type="files" uploadPath="/project/contract/projectContract"
					selectMultiple="true" readonly="true" />
			</td>
		</tr>

		<c:if test="${not empty projectContract.act.taskId && projectContract.act.status != 'finish'}">
			<tr>
				<td class="tit">您的意见</td>
				<td colspan="5">
					<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
					<span class="help-inline"><font color="red">*</font></span>
				</td>
			</tr>
		</c:if>
	</table>
	<br/><br/>
	<div class="">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><td class="tit" colspan="9">项目合同列表</td> <tr>
				<tr>
					<th>合同编号</th>
					<th>签约金额</th>
					<th>签约毛利</th>
					<th>毛利率%</th>
					<th>收款条款</th>
					<th>保修条款</th>
					<th>合同起始时间</th>
					<th>合同结束时间</th>
					<th>培训、外包</th>
					<%--<shiro:hasPermission name="project:contract:projectContract:edit"><th width="10">&nbsp;</th></shiro:hasPermission>--%>
				</tr>
			</thead>
			<tbody id="projectContractItemList">
			</tbody>

		</table>
		<script type="text/template" id="projectContractItemTpl">//<!--
			<tr id="projectContractItemList{{idx}}">
					<input id="projectContractItemList{{idx}}_id" name="projectContractItemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
					<input id="projectContractItemList{{idx}}_delFlag" name="projectContractItemList[{{idx}}].delFlag" type="hidden" value="0"/>
				<td>
				<c:choose>
				<c:when test="${projectContract.act.taskDefKey eq 'usertask_commerce_leader'}">
				<input id="projectContractItemList{{idx}}_contractCode" name="projectContractItemList[{{idx}}].contractCode" type="text" value="{{row.contractCode}}" maxlength="64" class="input-small required"/>
   				</c:when>
   				<c:otherwise>
   				{{row.contractCode}}
   				</c:otherwise>
  				</c:choose>
				</td>

				<td>{{row.contractAmount}}</td>

				<td>{{row.grossMargin}}</td>

				<td>{{row.grossProfitMargin}}</td>

				<td>{{row.termsOfPayment}}</td>
				<td>{{row.termsOfWarranty}}</td>
				<td style="width:95px" maxlength="20" class="input-medium">
					{{row.contractStartTime}}
				</td>
				<td style="width:95px" maxlength="20" class="input-medium">
					{{row.contractEndTime}}
				</td>
				<td>{{row.trainingOrOutsourcing}} </td>
			</tr>//-->
		</script>
		<script type="text/javascript">
			var projectContractItemRowIdx = 0, projectContractItemTpl = $("#projectContractItemTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			$(document).ready(function() {
				var data = ${fns:toJson(projectContract.projectContractItemList)};
				for (var i=0; i<data.length; i++){
					addRow('#projectContractItemList', projectContractItemRowIdx, projectContractItemTpl, data[i]);
					projectContractItemRowIdx = projectContractItemRowIdx + 1;
				}
				if (projectContractItemRowIdx ==0) {
					addRow('#projectContractItemList', projectContractItemRowIdx, projectContractItemTpl, data[i]);
					projectContractItemRowIdx = projectContractItemRowIdx + 1;
				}

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

			}); // end ready
		</script>
	</div>
	<div class="form-actions">
		<shiro:hasPermission name="project:contract:projectContract:edit">
			<c:if test="${not empty projectContract.act.taskId && projectContract.act.status != 'finish'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-warning" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectContract.id}">
		<act:histoicFlow procInsId="${projectContract.procInsId}" />
	</c:if>
</form:form>
</body>
</html>