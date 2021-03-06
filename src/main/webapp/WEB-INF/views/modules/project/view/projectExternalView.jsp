<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部立项申请管理</title>
	<meta name="decorator" content="default"/>
		
	<script type="text/javascript">
		$(document).ready(function() {
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
		<%-- <li><a href="${ctx}/apply/external/projectApplyExternal/">外部立项申请列表</a></li> --%>
		<c:if test="${ empty projectApplyExternal.applyFlag}">
			<li class="active"><a href="">立项申请单</a></li>
		</c:if>
		<c:if test="${ empty projectApplyExternal.biddingFlag}">
			<li class="active"><a href="">投标申请单</a></li>
		</c:if>
		<c:if test="${ empty projectApplyExternal.contractFlag}">
			<li class="active"><a href="">合同申请单</a></li>
		</c:if>
		<c:if test="${ empty projectApplyExternal.applyFlag}">
			<li class="active"><a href="">结项申请单</a></li>
		</c:if>
	</ul><br/>
	
	
	<form:form id="inputForm" modelAttribute="projectApplyExternal" action="${ctx}/apply/external/projectApplyExternal/save" method="post" class="form-horizontal">
		
		<form:hidden path="id"/>
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
						${projectApplyExternal.saleOffice.name  }
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
				<td class="tit" >主要供应商</label></td>
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
				<td  class="tit"rowspan="2" >项目毛利率说明</label></td>
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
				<td colspan="5"></td>
			</tr>
			
			<tr>
				<td class="tit" >文件附件</td>
				<td   colspan="6">
					<form:hidden id="documentAttachmentPath" path="documentAttachmentPath" htmlEscape="false" maxlength="20000"  />
					<sys:ckfinder input="documentAttachmentPath" type="files" uploadPath="/apply/external/projectApplyExternal" selectMultiple="true"/>
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
					3、销售收入以人民币为单位，当以其他货币为单位时，应注明货币单位。<br>
					4、超过常务副总经理授权的项目需要总经理审批。
				</div>
				</td>
			</tr>
		</table>
		
		<act:histoicFlow procInsId="${projectApplyExternal.processInstanceId}" />
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>