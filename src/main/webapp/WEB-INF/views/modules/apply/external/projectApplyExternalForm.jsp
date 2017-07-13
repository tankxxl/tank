<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部立项申请管理</title>
	<meta name="decorator" content="default"/>
		
	<script type="text/javascript">
	
		$(document).ready(function() {
            // 初始化全局变量，修改表单使用
            treeGetParam = "?customerId=${projectApplyExternal.customer.id}";
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					estimatedGrossProfitMargin: {
				      	required: true,
						number: true,
				      	min: 0,
                        max: 100,
                        minlength: 1
				    },
					ownership: {
				      	required: true
				    }
				  },
				messages: {
                    ownership: "请填写",
                    estimatedGrossProfitMargin: {
                        required: "请填写毛利率",
                        minlength: jQuery.validator.format("至少要填写 {0} 位数字。")
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
			    // Checks whether the selected form or selected elements are valid.
// 				$("#inputForm").valid().element($("#category"));
				// Validates the selected form.
				$("#inputForm").validate().element($("#category"));
			});
			$("#ownership").change(function(){
				$("#inputForm").validate().element($("#ownership"));
			});
			
			$("#estimatedGrossProfitMargin").change(function(){
				if(isNaN(this.value) ){
					return false;
				}
				//若毛利率低于公司的设置百分比，弹出提示，毛利率说明必填
				if(parseFloat(this.value)< ${fns:getDictLabel("key", 'estimatedGrossProfitMargin', '5')}){
					alert("毛利率低于公司设置标准 ${fns:getDictLabel("key", 'estimatedGrossProfitMargin', '5')}(%)，请填写毛利率说明");
					$("#estimatedGrossProfitMarginDescription").after("<span class='help-inline'><font color='red'>*</font> </span>");
					$("#estimatedGrossProfitMarginDescription").addClass('required');
				}
				else{
					$("#estimatedGrossProfitMarginDescription").nextAll().remove();
					$("#estimatedGrossProfitMarginDescription").removeClass('required');
				}
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

		function changeCustomer(customerId){
            // JavaScript全局变量，用于传递参数，给下一个树控件过滤数据使用
            treeGetParam = "?customerId=" + customerId;
			var url ="${ctx }/customer/customer/getAsJson?id="+customerId;
		    $.ajax( {  
		        type : "get",  
		        url : url,  
		        dataType:"json",
		        success : function(customer) {
		            $("#customer_industry_label").text(customer.industry);
		            $("#customer_category_label").text(customer.customerCategory);
		            
		            //验证validate
		            $("#inputForm").validate().element($("#customerName"));
		        }  
		    });
		    
		    //清除客户联系人的值
		    $("#customerContact_phone_label").text("");
		    $("#customerContact_position_label").text("");
		    $("#customerContactId").val("");
		    $("#customerContactName").val("");
		}

		function changeCustomerContact(contactId) {
            $.post('${ctx}/customer/customer/getContactAsJson',
                {id: contactId}, function (item) {
                    if (item) {
                        $("#customerContact_phone_label").text(item.phone);
                        $("#customerContact_position_label").text(item.position);
                        //验证validate
                        $("#inputForm").validate().element($("#customerContactName"));
                    }
                });
        }

	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<c:if test="${ empty projectApplyExternal.act.taskId}">
		<li><a href="${ctx}/apply/external/projectApplyExternal/">外部立项申请列表</a></li>
	</c:if>
	<li class="active"><a href="${ctx}/apply/external/projectApplyExternal/form?id=${projectApplyExternal.id}">外部立项申请
		<shiro:hasPermission name="apply:external:projectApplyExternal:edit">
			${not empty projectApplyExternal.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="apply:external:projectApplyExternal:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectApplyExternal" htmlEscape="false"
		   action="${ctx}/apply/external/projectApplyExternal/save" method="post" class="form-horizontal">
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
	<table class="table-form">
		<tr>
			<td colspan="2" class="tit">项目编号${rand}</td>
			<td class="" colspan="3">

				<shiro:hasPermission name="apply:external:projectApplyExternal:onlySave">
					<form:input path="projectCode" maxlength="64" class="required"/>
					<span class="help-inline"><font color="red">*</font></span>
				</shiro:hasPermission>
				<shiro:lacksPermission name="apply:external:projectApplyExternal:onlySave">
					${projectApplyExternal.projectCode  }
				</shiro:lacksPermission>

			</td>
			<td class="tit">项目归属</td>
			<td colspan="2">
				<form:select path="ownership" class="" style="width:89%;">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('pro_ownership')}" itemLabel="label" itemValue="value"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>
		<tr>
			<td  class="tit" colspan="2">项目名称</td>
			<td colspan="5">
				<form:input path="projectName" style="width: 80%" maxlength="64" class=" required"/>
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
					${projectApplyExternal.saler.office.name  }
				</td>
			</tr>
		</c:if>

		<tr>
			<td  class="tit" colspan="2">客户全称</td>
			<td>
				<div style="white-space:nowrap;">
					<sys:treeselect id="customer"
						name="customer.id"
						value="${projectApplyExternal.customer.id}"
						labelName="customer.customerName"
						labelValue="${projectApplyExternal.customer.customerName}"
						dataMsgRequired="客户必填" title="客户"
						url="/customer/customer/treeData" cssClass="required"
						allowClear="true" notAllowSelectParent="true" customClick="changeCustomer"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</td>
			<td  class="tit">客户类别</td>
			<td  class="text-center">
					<label id="customer_category_label"></label>
			</td>
			<td   class="tit">客户所属行业</td>
			<td   class="text-center">
				<label id="customer_industry_label"></label>
			</td>

		</tr>
		<tr>
			<td  class="tit"  colspan="2">客户联系人</td>
			<td>
				<sys:treeselect id="customerContact"
					name="customerContact.id"
					value="${projectApplyExternal.customerContact.id}"
					labelName="customerContact.contactName"
					labelValue="${projectApplyExternal.customerContact.contactName}"
					title="客户联系人" dataMsgRequired="项目客户联系人必填"
					url="/customer/customer/treeData2" cssClass="required"
					allowClear="true" notAllowSelectParent="true"
					dependBy="customer"
					dependMsg="请先选择客户！"
					customClick="changeCustomerContact"  />
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
			<td  class="tit">职务</td>
			<td class="text-center">
				<label id="customerContact_position_label">${projectApplyExternal.customerContact.position }</label>
			</td>
			<td class="tit">联系方式</td>
			<td class="text-center">
				<label id="customerContact_phone_label">${projectApplyExternal.customerContact.phone }</label>
			</td>
		</tr>

		<tr>
			<td  class="tit" rowspan="4">项目描述</td>
			<td class="tit">预计合同金额￥万元</td>
			<td>
				<div style="white-space:nowrap;">
					<form:input path="estimatedContractAmount" style="width:80%;"   class="checkNum number contract_amount required"  maxlength="10"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</td>
			<td class="tit">预计毛利率％</td>

			<td>
				<form:input path="estimatedGrossProfitMargin" style="width:80%"  maxlength="5" class="checkNum"  number="true" type="text" /><span class="help-inline"><font color="red">*</font> </span>
			</td>

			<td class="tit">预计签约时间</td>
			<td>
				<input name="estimatedTimeOfSigning" type="text" readonly="readonly" class="input-medium Wdate required"
					value="<fmt:formatDate value="${projectApplyExternal.estimatedTimeOfSigning}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
					<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>
		<tr>
			<td class="tit">项目类别</td>
			<td>
				<form:select path="category" class="input-medium required" >
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>
		<tr>
			<td  class="tit"rowspan="2">项目描述</td>
			<td  colspan="6"><label class="small_label">（描述内容包括主要设备名称及规格、实施工作等）</label></td>
		</tr>
		<tr>
			<td  colspan="6">
				<form:textarea path="description" style="width:98%" rows="4" maxlength="255" />
			</td>
		</tr>

		<tr>
			<td  class="tit"rowspan="2" >项目毛利率说明</td>
			<td  colspan="6"><label class="small_label">（当预计毛利率低于公司要求时，须加以说明）</label></td>
		</tr>
		<tr>
			<td  class="tit" colspan="6">
				<div style="white-space:nowrap;">
				<form:textarea path="estimatedGrossProfitMarginDescription" style="width:98%" maxlength="255"/>
				</div>
			</td>
		</tr>
		<tr>
			<td  class="tit" rowspan="2">项目风险分析</td>
			<td  colspan="6"><label class="small_label">（立项人对项目风险进行识别、评估）</label></td>
		</tr>
		<tr>
			<td  class="tit" colspan="6">
				<div style="white-space:nowrap;">
					<form:textarea path="riskAnalysis" class="required" style="width:98%" maxlength="255"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</td>
		</tr>

		<tr>
			<td class="tit" >文件附件</td>
			<td   colspan="6">
				<form:hidden id="documentAttachmentPath" path="documentAttachmentPath" htmlEscape="false" maxlength="20000"  />
				<sys:ckfinder input="documentAttachmentPath" type="files"
							  uploadPath="/apply"
							  selectMultiple="true" />
			</td>
		</tr>
		<tr>
			<td class="tit" colspan="7">填表说明</td>
		</tr>
		<tr>
			<td colspan="7">
			<div>
				1、项目预计合同金额默认以人民币为单位，以其他货币为单位时，应注明货币单位；<br>
				2、项目的预计毛利率原则上不得低于公司规定的毛利率标准，若预计毛利率低于公司要求标准时，须对预计毛利率不达标的原因进行说明；<br>
				3、如对项目信息有更详细的说明或者其他相关文档的，可使用文件附件的形式提交；<br>
				4、超过分管领导授权的项目需公司总经理进行审批；<br>
				5、项目立项审批完成后，由项目管理部专人负责定时打印本表进行存档。
			</div>
			</td>
		</tr>
	</table>

	<div class="form-actions">
		<shiro:hasPermission name="apply:external:projectApplyExternal:edit">

		<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
			<c:if test="${not empty projectApplyExternal.id}">
				<input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>

		<shiro:hasPermission name="apply:external:projectApplyExternal:super">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectApplyExternal.id}">
		<act:histoicFlow procInsId="${projectApplyExternal.procInsId}" />
	</c:if>
</form:form>
</body>
</html>