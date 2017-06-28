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
		
		<%-- <li class="active"><a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">项目投标<shiro:hasPermission name="project:bidding:projectBidding:edit">${not empty projectBidding.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li> --%>
		<li class="active"><a>项目投标查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="projectBidding" action="${ctx}/project/bidding/projectBidding/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
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
				<td class="tit_content" colspan="3">
					${fns:getDictLabels(projectBidding.category, 'tender_category', '')}
				</td>
				
				<td colspan="1" class="tit">标书购买价</td>
				<td class="tit_content" colspan="1">
					${projectBidding.price }
				</td>
			</tr>
			<%--<tr>--%>
				<%--<td colspan="1" class="tit">用印内容</td>--%>
				<%--<td class="tit_content" colspan="5">--%>
					<%--${fns:getDictLabels(projectBidding.printingPaste, 'tender_printing_paste', '')}--%>
				<%--</td>--%>
			<%--</tr>--%>
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
					<form:hidden id="profitMarginFile" path="profitMarginFile" htmlEscape="false" maxlength="2000" class="input-xlarge required"/>
					<sys:ckfinder input="profitMarginFile" type="files" uploadPath="/project/bidding/projectBidding" selectMultiple="true"/>
				</td>
				<script type="text/javascript">
					$(document).ready(function() {
						$("#profitMarginFile").nextAll("a").remove();
					});
				</script>
			</tr>
			<tr>
				<td  class="tit" colspan="6">填表说明</td>
			</tr>
			<tr>
				<td colspan="6">
				<div >
					1、 必须将《项目收入成本预测表》作为附件，否则不予批准；<br/>
					2、 售前工程师、项目经理、解决方案部负责人、服务交付部或软件开发部负责人、技术部分管领导负责逐级确认投标书中的技术部分，包括产品配置、技术方案、技术偏离表、技术承诺等内容；<br/>
					3、 商务部负责对《项目收入成本预测表》中的产品采购、工程外包等内容进行确认；<br/>
					4、 销售支持部、项目销售、业务部负责人、业务部分管领导负责逐级确认投标报价以及商务承诺等内容；<br/>
					5、 项目管理部和法务对项目中的风险和法律内容进行监控；<br/>
					6、 投标审批后，本表及其附件《项目收入成本预测表》原件由项目管理部存档。
				</div>
				</td>
			</tr>
			
		</table>	
		
		<act:histoicFlow procInsId="${projectBidding.processInstanceId}" />
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>