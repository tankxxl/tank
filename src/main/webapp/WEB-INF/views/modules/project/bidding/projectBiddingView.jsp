<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>项目投标管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
	</script>
	
	<style type="text/css">
		.tit_content{
			text-align:center
		}
	</style>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<c:if test="${ empty projectBidding.act.taskId}">
			<li><a href="${ctx}/project/bidding/projectBidding/">项目投标列表</a></li>
		</c:if>

		<li class="active"><a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">项目投标
			<shiro:hasPermission name="project:bidding:projectBidding:edit">
			${not empty projectBidding.act.taskId?'审批':'查看'}
		</shiro:hasPermission> <shiro:lacksPermission name="project:bidding:projectBidding:edit">查看
		</shiro:lacksPermission>
		</a></li>
		
		<%-- <li class="active"><a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">项目投标<shiro:hasPermission name="project:bidding:projectBidding:edit">${not empty projectBidding.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li> --%>
		<%--<li class="active"><a>项目投标查看</a></li>--%>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="projectBidding" htmlEscape="false" action="${ctx}/project/bidding/projectBidding/saveAudit" method="post" class="form-horizontal">
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
				<td colspan="1" class="tit">项目名称</td>
				<td class="tit_content" colspan="1">
					${projectBidding.apply.projectName}
				</td>
				<td colspan="1" class="tit">项目编号</td>
				<td class="tit_content" colspan="3">
					<label id="project_code">${projectBidding.apply.projectCode }</label>
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">客户名称</td>
				<td class="tit_content" colspan="1">
					<label id="customer_name">${projectBidding.apply.customer.customerName }</label>
				</td>
				<td colspan="1" class="tit">行业</td>
				<td class="tit_content" colspan="1">
					<label id="customer_industry" style="width:70px">${fns:getDictLabel(projectBidding.apply.customer.industry , 'customer_industry', '')}</label>
				</td>
				<td colspan="1" class="tit">项目销售</td>
				<td class="tit_content" colspan="1">
					<label id="project_saler">${projectBidding.apply.saler.name }</label>
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">招标方(若无第三方，可不填)</td>
				<td class="tit_content" colspan="3">
					${projectBidding.tenderer }
				</td>
				
				<td colspan="1" class="tit">是否有外包</td>
				<td colspan="1" class="">
					${fns:getDictLabel(projectBidding.outsourcing, 'yes_no', '否')}
				</td>
				
			</tr>
			<tr>
				<td colspan="1" class="tit">标书种类</td>
				<td class="tit_content" colspan="1">
					${fns:getDictLabels(projectBidding.category, 'tender_category', '')}
				</td>
				
				<td colspan="1" class="tit">标书购买价</td>
				<td class="tit_content" colspan="1">
					${projectBidding.price }
				</td>

                <td colspan="1" class="tit">用印内容</td>
                <td class="tit_content" colspan="1">
                        ${fns:getDictLabels(projectBidding.printingPaste, 'tender_printing_paste', '')}
                </td>
			</tr>
			<tr>
				<td colspan="1" class="tit">技术负责人</td>
				<td  colspan="1">
					${projectBidding.projectManager.name}
				</td>
			</tr>
			<tr>

                <td colspan="1" class="tit">投标金额(元)</td>
                <td class="tit_content" colspan="1">
                        ${projectBidding.amount }
                </td>

                <td colspan="1" class="tit">投标毛利(元)</td>
                <td class="tit_content" colspan="1">
                        ${projectBidding.grossMargin }
                </td>

                <td colspan="1" class="tit">毛利率％</td>
                <td class="tit_content" colspan="1">
                        ${projectBidding.profitMargin }
                </td>

			</tr>
			<tr>
				<td colspan="1" class="tit">投标内容与立项内容偏差说明</td>
				<td class="" colspan="5">
					${projectBidding.content }
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">毛利分析表附件</td>
				<td class="" colspan="5">
					<form:hidden id="profitMarginFile" path="profitMarginFile" maxlength="2000" class="input-xlarge required"/>
					<sys:ckfinder input="profitMarginFile" type="files" uploadPath="/project/bidding/projectBidding" selectMultiple="true" readonly="true"/>
				</td>
			</tr>

			<c:if test="${not empty projectBidding.act.taskId && projectBidding.act.status != 'finish'}">
			<tr>
				<td class="tit">您的意见</td>
				<td colspan="6">
					<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" value="同意" cssStyle="width:95%" />
					<span class="help-inline"><font color="red">*</font></span>
				</td>
			</tr>
			</c:if>

		</table>	
		
		<act:histoicFlow procInsId="${projectBidding.procInsId}" />
		<div class="form-actions">

			<shiro:hasPermission name="project:bidding:projectBidding:edit">
				<c:if test="${not empty projectBidding.act.taskId && projectBidding.act.status != 'finish'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;&nbsp;
					<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;&nbsp;
				</c:if>
			</shiro:hasPermission>

			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>