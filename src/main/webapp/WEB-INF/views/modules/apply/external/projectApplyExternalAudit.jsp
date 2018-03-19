<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部立项申请管理</title>
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
					}else {
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
	<li class="active"><a>外部立项审批</a></li>
		<%-- <li><a href="${ctx}/apply/external/projectApplyExternal/">外部立项申请列表</a></li> --%>
		<%-- <li class="active"><a href="${ctx}/apply/external/projectApplyExternal/form?id=${projectApplyExternal.id}">外部立项申请<shiro:hasPermission name="apply:external:projectApplyExternal:edit">${not empty projectApplyExternal.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="apply:external:projectApplyExternal:edit">查看</shiro:lacksPermission></a></li> --%>
	</ul><br/>
	
	
	<form:form id="inputForm" modelAttribute="projectApplyExternal" action="${ctx}/apply/external/projectApplyExternal/saveAudit" method="post" class="form-horizontal">
		
		<form:hidden path="id"/>
		<form:hidden path="act.taskId"/>
		<form:hidden path="act.taskName"/>
		<form:hidden path="act.taskDefKey"/>
		<form:hidden path="act.procInsId"/>
		<form:hidden path="act.procDefId"/>
		<form:hidden id="flag" path="act.flag"/>
		<sys:message content="${message}"/>
		
		<table class="table-form">
			<tr>
				<td colspan="2" class="tit">项目编号</td>
				<td class="tit_content" colspan="6">
					${projectApplyExternal.projectCode  }
				</td>
			</tr>
			<tr>
				<td  class="tit" colspan="2">项目名称</td>
				<td class="tit_content" colspan="5">
					${projectApplyExternal.projectName  }
				</td>
			</tr>
			<c:if test="${not empty projectApplyExternal.saler.name}">
				<tr>
					<td  class="tit" colspan="2">销售人员</td>
						
					<td   class="tit_content" colspan="2">
						<label>${projectApplyExternal.saler.name }</label>
					</td>
					<td  class="tit">部&nbsp;&nbsp;门</td>
					<td   class="tit_content" colspan="2">
						<%--${projectApplyExternal.saleOffice.name  }--%>
						${projectApplyExternal.saler.office.name  }
					</td>
				</tr>
			</c:if>
			
			<tr>
				<td  class="tit" colspan="2">客户全称</td>
				<td class="tit_content">
					${projectApplyExternal.customer.customerName }
				</td>
				<td  class="tit">客户类别</td>
				<td  class="tit_content">
						${fns:getDictLabel(projectApplyExternal.customer.customerCategory, 'customer_category', '')}
						
				</td>
				<td   class="tit">客户所属行业</td>
				<td   class="tit_content">
					${fns:getDictLabel(projectApplyExternal.customer.industry, 'customer_industry', '')}
				</td>
				
			</tr>
			<tr>
				<td  class="tit"  colspan="2">客户联系人</td>
				<td class="tit_content">
					${projectApplyExternal.customerContact.contactName }
				</td>
				
				<td  class="tit">职务</td>
				<td class="tit_content">
					${projectApplyExternal.customerContact.position }
				</td>
				<td class="tit">联系方式</td>
				<td class="tit_content">
					${projectApplyExternal.customerContact.phone }
				</td>
			</tr>
			<tr>
				<td class="tit" >主要供应商</td>
				<td colspan="6">

				</td>
			</tr>
			<tr>
				<td  class="tit" rowspan="4">项目描述</td>
				<td class="tit">预计合同金额￥万元</td>
				<td class="tit_content">
					${projectApplyExternal.estimatedContractAmount }
				</td>
				<td class="tit">预计毛利率％</td>
				<td class="tit_content">
					${projectApplyExternal.estimatedGrossProfitMargin }
				</td>
				<td class="tit">预计签约时间</td>
				<td class="tit_content">
					<fmt:formatDate value="${projectApplyExternal.estimatedTimeOfSigning}" pattern="yyyy-MM-dd"/>
				</td>
			</tr>
			<tr>
				<td class="tit">项目类别</td>
				<td class="tit_content">
					${fns:getDictLabel(projectApplyExternal.category, 'pro_category', '')}
				</td>
			</tr>
			<tr>
				<td  class="tit"rowspan="2">项目描述</td>
				<td  colspan="6"><label class="small_label">（描述内容包括主要设备名称及规格、实施工作等）</label></td>
			</tr>
			<tr>
				<td  colspan="6">
					${projectApplyExternal.description}
				</td>
			</tr>

			<tr>
				<td  class="tit"rowspan="2" >项目毛利率说明</td>
				<td  colspan="6"><label class="small_label">（当预计毛利率低于公司要求时，须加以说明）</label></td>
			</tr>
			<tr>
				<td   colspan="6">
					${projectApplyExternal.estimatedGrossProfitMarginDescription}
				</td>
			</tr>
			<tr>
				<td  class="tit" rowspan="2">项目风险分析</td>
				<td  colspan="6"><label class="small_label">（立项人对项目风险进行识别、评估）</label></td>
			</tr>
			<tr>
				<td   colspan="6">
					${projectApplyExternal.riskAnalysis}
				</td>
			</tr>
			<tr>
				<td class="tit">资源需求</td>
				<td colspan="6">${projectApplyExternal.resource}</td>
			</tr>
			
			<tr>
				<td class="tit" >文件附件</td>
				<td   colspan="6">
					<form:hidden id="documentAttachmentPath" path="documentAttachmentPath" htmlEscape="false" maxlength="20000"  />
					<sys:ckfinder input="documentAttachmentPath" type="files" uploadPath="/apply/external/projectApplyExternal" selectMultiple="true" readonly="true"/>
				</td>
				<script type="text/javascript">
					$(document).ready(function() {
						$("#documentAttachmentPath").nextAll("a").remove();
					});
				</script>
			</tr>
			<tr>
				<td  class="tit" colspan="7">填表说明</td>
			</tr>
			<tr>
				<td colspan="7">
				<div >
					预计项目毛利率原则上不得低于公司规定的毛利率标准，如预计项目毛利率低于公司要求时，须对预计毛利率进行特殊说明；<br> 
					1、项目立项后，本表原件由项目管理部存档；<br>
					2、如对项目信息有更详细的说明，可附页说明，其他文档作为附件提交；<br>
					3、销售收入以人民币为单位，当以其他货币为单位时，应注明货币单位。
				</div>
				</td>
			</tr>
			
			<tr>
				<td class="tit">您的意见</td>
				<td colspan="6">
					<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" value="同意" cssStyle="width:500px"/>
					<span class="help-inline"><font color="red">*</font></span>
				</td>
			</tr>
			
		</table>
		
		<div class="form-actions">
			<shiro:hasPermission name="apply:external:projectApplyExternal:edit">
			
				<c:if test="${projectApplyExternal.act.taskDefKey eq 'apply_end'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="兑 现" onclick="$('#flag').val('yes')"/>&nbsp;
				</c:if>
				<c:if test="${projectApplyExternal.act.taskDefKey ne 'apply_end'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
					<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
				</c:if>
			
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
		<act:histoicFlow procInsId="${projectApplyExternal.act.procInsId}"/>
	</form:form>
</body>
</html>