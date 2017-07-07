<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部立项申请管理</title>
	<meta name="decorator" content="default"/>
	<%-- Deprecated replaced by xxxView.jsp --%>
	<script type="text/javascript">
	
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					estimatedGrossProfitMargin: {
				      required: true,
				      range: [0,100]
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
						
						if(error[0].innerHTML ==''){
							error[0].innerHTML= "必填信息";
						}
						console.log(element);
						console.log(error);
						error.appendTo(element.parent().parent());
					}else {
						error.insertAfter(element);
					}
				}
			});
			
			
			$("#project_code_button").click(function(){
				 if($("#category").val() ==""){
					alert("请先选择项目类型");
					return false;
				}
				if($("#ownership").val()==""){
					alert("请先选择项目归属");
					return false;
				}
				var url ="${ctx }/apply/external/projectApplyExternal/projectCodeGenerate?category="+$("#category").val()+"&ownership="+$("#ownership").val();
			    $.ajax( {  
			        type : "get",  
			        url : url,  
			        dataType:"json",
			        success : function(data) {
			            //alert("Data Saved: " + customer.industry+"--"+customer.customerCategory);
			            //alert($("#customer\\.customerCategory").val());
			            console.log(data);
			            if(data.error){
			            	alert(data.error);
			            	return;
			            }else{
				            $("#projectCode").val(data.data);
			            }
			        }  
			    });
			});
			
			
			$("#category,#ownership").change(function changeProCode(){
				top.$.jBox.tip("你修改了了 项目类型或项目归属，请重新生成项目编号");
				$("#projectCode").val("");
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
				<td colspan="3">
				
					<c:if test="${empty projectApplyExternal.projectCode}">
						<form:input path="projectCode" htmlEscape="false" maxlength="64" class=" required"/>
						<span class="help-inline"><font color="red">*</font></span>
						<input id="project_code_button" class="btn btn-primary" type="button" value="生成项目编号"/>
					</c:if>
					<c:if test="${not empty projectApplyExternal.projectCode}">
						<label>${projectApplyExternal.projectCode}</label>
					</c:if>
				</td>
				<td class="tit">项目归属</td>
				<td colspan="2">
					<form:select path="ownership" class="input-medium required" style="width:89%;">
						<form:option value="" label=""/>
						<form:options items="${fns:getDictList('pro_ownership')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
			</tr>
			<tr>
				<td  class="tit" colspan="2">项目名称</td>
				<td colspan="5">
					<form:input path="projectName" htmlEscape="false" maxlength="64" class=" required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
			</tr>
			<c:if test="${not empty projectApplyExternal.saler.name}">
				<tr>
					<td  class="tit" colspan="2">销售人员</td>
						
					<td   class="tit" colspan="2">
						<label>${projectApplyExternal.saler.name }</label>
					</td>
					<td  class="tit">部&nbsp;&nbsp;门</td>
					<td   class="tit" colspan="2">
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
				<td>
					<form:select path="category" class="input-medium" style="width:89%;">
						<form:option value="" label=""/>
						<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span><br>
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
                    1、项目预计合同金额默认以人民币为单位，以其他货币为单位时，应注明货币单位；<br>
                    2、项目的预计毛利率原则上不得低于公司规定的毛利率标准，若预计毛利率低于公司要求标准时，须对预计毛利率不达标的原因进行说明；<br>
                    3、如对项目信息有更详细的说明或者其他相关文档的，可使用文件附件的形式提交；<br>
                    4、超过分管领导授权的项目需公司总经理进行审批；<br>
                    5、项目立项审批完成后，由项目管理部专人负责定时打印本表进行存档。
				</div>
				</td>
			</tr>
			
			<tr>
				<td class="tit">您的意见</td>
				<td colspan="6">
					<form:textarea path="act.comment" class="required" rows="5" maxlength="20" cssStyle="width:500px"/>
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