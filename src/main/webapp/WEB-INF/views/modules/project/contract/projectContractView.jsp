<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>合同管理</title>
<meta name="decorator" content="default" />
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
		//
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
		//
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
	</script>
<style type="text/css">
.tit_content {
	text-align: center
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:if test="${ empty projectContract.act.taskId}">
			<li><a href="${ctx}/project/contract/projectContract/">合同列表</a></li>
		</c:if>
		<li class="active"><a
			href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}">
				合同 <shiro:hasPermission name="project:contract:projectContract:edit">
			${not empty projectContract.act.taskId?'审批':'查看'}
		</shiro:hasPermission> <shiro:lacksPermission name="project:contract:projectContract:edit">查看
		</shiro:lacksPermission>
		</a></li>
		
		<%--<li class="active"><a>项目合同查看</a></li>--%>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="projectContract"
		action="${ctx}/project/contract/projectContract/saveAudit" method="post"
		class="form-horizontal">
		<form:hidden path="id" />
		<form:hidden path="act.taskId" />
		<form:hidden path="act.taskName" />
		<form:hidden path="act.taskDefKey" />
		<form:hidden path="act.procInsId" />
		<form:hidden path="act.procDefId" />
		<form:hidden id="flag" path="act.flag" />
		<sys:message content="${message}" />
		<table class="table-form">
			<tr>
				<th colspan="6" class="tit">项目信息</th>
			</tr>
			<tr>
				<td class="tit">项目名称</td>
				<td colspan="1" class="tit_content">${projectContract.apply.projectName}</td>
				<td class="tit">项目编码</td>
				<td style="width: 100px"><label id="project_code">${projectContract.apply.projectCode }</label></td>
				<td class="tit">项目类型</td>
				<td style="width: 100px"><label id="project_category">${fns:getDictLabel(projectContract.apply.category , 'pro_category', '')}</label></td>
			</tr>
			<tr>
				<td class="tit">客户名称</td>
				<td class="tit_content"><label id="customer_name">${projectContract.apply.customer.customerName }</label></td>
				<td class="tit">客户联系人</td>
				<td class="tit_content"><label id="customer_contact_name">${projectContract.apply.customerContact.contactName }</label></td>
				<td class="tit">客户电话</td>
				<td class="tit_content"><label id="customer_phone">${projectContract.apply.customer.phone }</label></td>
			</tr>
			<c:if test="${not empty  projectContract.projectManager.id}">
				<tr>
					<td class="tit">实施项目经理</td>
					<td colspan="1" class="tit_content">${projectContract.projectManager.name}</td>
					<td colspan="4"/>
				</tr>
			</c:if>
			
			<tr>
				<td  class="tit" rowspan="2" >说明</td>
				<td  colspan="5"><label class="small_label">（没有投标的，须加以说明）</label></td>
			</tr>
			<tr>
				<td colspan="5">
					${projectContract.remarks}
				</td>
			</tr>
			
			<tr>
				<td class="tit" >文件附件</td>
				<td   colspan="5">
					<form:hidden id="attachment" path="attachment" htmlEscape="false" maxlength="20000"  />
					<sys:ckfinder input="attachment" type="files" uploadPath="/project/contract/projectContract" selectMultiple="true" readonly="true"/>
				</td>
			</tr>

			<c:if test="${not empty projectContract.act.taskId && projectContract.act.status != 'finish'}">
				<tr>
					<td class="tit">您的意见</td>
					<td colspan="5">
						<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" value="同意" cssStyle="width:95%" />
						<span class="help-inline"><font color="red">*</font></span>
					</td>
				</tr>
			</c:if>
			
		</table>
		<br />
		<br />
		<br />

		<table id="contentTable"
			class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<td class="tit" colspan="10">项目合同列表</td>
				<tr>
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
					<th>其他</th>
				</tr>
			</thead>
			<tbody id="projectContractItemList">
			</tbody>
			<%-- <shiro:hasPermission name="project:contract:projectContract:edit">
				<tfoot>
				<tr>
					<td colspan="12">
					<a href="javascript:" onclick="addRow('#projectContractItemList', projectContractItemRowIdx, projectContractItemTpl);projectContractItemRowIdx = projectContractItemRowIdx + 1;" class="btn">新增</a>
					</td>
				</tr>
				</tfoot>
			</shiro:hasPermission> --%>
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
							<td>
								{{row.contractAmount}}
							</td>

							<td>
								{{row.grossMargin}}
							</td>

							<td>
								{{row.grossProfitMargin}}
							</td>

							<td>
								{{row.termsOfPayment}}
							</td>
							<td>
								{{row.termsOfWarranty}}
							</td>
							<td style="width:95px" maxlength="20" class="input-medium">
								{{row.contractStartTime}}
							</td>
							<td style="width:95px" maxlength="20" class="input-medium">
								{{row.contractEndTime}}
							</td>
							<td>
								{{row.trainingOrOutsourcing}}
							</td>
							<td>
								{{row.other}}
							</td>
							
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
			});
		</script>

		<act:histoicFlow procInsId="${projectContract.procInsId}" />
		<div class="form-actions">

			<shiro:hasPermission name="project:contract:projectContract:edit">
				<c:if test="${not empty projectContract.act.taskId && projectContract.act.status != 'finish'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;&nbsp;
					<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;&nbsp;
				</c:if>
			</shiro:hasPermission>

			<input id="btnCancel" class="btn" type="button" value="返 回"
				onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>