<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>立项管理</title>
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/project/approval/project/">立项列表</a></li>
		<li class="active"><a href="${ctx}/project/approval/project/form?id=${project.id}">立项<shiro:hasPermission name="project:approval:project:edit">${not empty project.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:approval:project:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="project" action="${ctx}/project/approval/project/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">项目编码：</label>
			<div class="controls">
				<form:input path="projectCode" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目名称：</label>
			<div class="controls">
				<form:input path="projectName" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户编号：</label>
			<div class="controls">
				<sys:treeselect id="customer" name="customer.id" value="${project.customer.id}" labelName="customer.customerName" labelValue="${project.customer.customerName}"
					title="客户" url="/customer/customer/treeData?type=2" cssClass="" allowClear="true" notAllowSelectParent="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户联系人编号：</label>
			<div class="controls">
				<sys:treeselect id="customerContact" name="customerContact.id" value="${project.customerContact.id}" labelName="customerContact.contactName" labelValue="${project.customerContact.contactName}"
					title="客户联系人" url="/customer/customer/treeData2?type=2" cssClass="" allowClear="true" notAllowSelectParent="true"/>
			</div>
		</div>
		
		<script type="text/javascript">
		$(document).ready(function() {
			$("#customerContactButton, #customerContactName").unbind('click');
			$("#customerContactButton, #customerContactName").click(function(){
				var customerId =$("#customerId").val();
				console.log(customerId);
				// 正常打开	${ctx}/tag/treeselect  /jeesite/a/tag/treeselect
				top.$.jBox.open("iframe:${ctx}/tag/treeselect?url="+encodeURIComponent("/customer/customer/treeData2?type=2&customerId=")+customerId+"&module=&checked=&extId=&isAll=", "选择客户联系人", 300, 420, {
					ajaxData:{selectIds: $("#customerContactId").val()},buttons:{"确定":"ok", "清除":"clear", "关闭":true}, submit:function(v, h, f){
						if (v=="ok"){
							var tree = h.find("iframe")[0].contentWindow.tree;//h.find("iframe").contents();
							var ids = [], names = [], nodes = [];
							if ("" == "true"){
								nodes = tree.getCheckedNodes(true);
							}else{
								nodes = tree.getSelectedNodes();
							}
							for(var i=0; i<nodes.length; i++) {//
								if (nodes[i].isParent){
									top.$.jBox.tip("不能选择父节点（"+nodes[i].name+"）请重新选择。");
									return false;
								}//
								ids.push(nodes[i].id);
								names.push(nodes[i].name);//
								break; // 如果为非复选框选择，则返回第一个选择  
							}
							$("#customerContactId").val(ids.join(",").replace(/u_/ig,""));
							$("#customerContactName").val(names.join(","));
						}//
						else if (v=="clear"){
							$("#customerContactId").val("");
							$("#customerContactName").val("");
		                }//
						if(typeof customerContactTreeselectCallBack == 'function'){
							customerContactTreeselectCallBack(v, h, f);
						}
					}, loaded:function(h){
						$(".jbox-content", top.document).css("overflow-y","hidden");
					}
				});
			});

		});
	</script>
	
		<div class="control-group">
			<label class="control-label">预计合同金额：</label>
			<div class="controls">
				<form:input path="estimatedContractAmount" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">预计毛利率：</label>
			<div class="controls">
				<form:input path="estimatedGrossProfitMargin" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">预计签约时间：</label>
			<div class="controls">
				<input name="estimatedTimeOfSigning" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${project.estimatedTimeOfSigning}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目类别：</label>
			<div class="controls">
				<form:select path="category" class="input-xlarge ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目毛利率说明：</label>
			<div class="controls">
				<form:textarea path="estimatedGrossProfitMarginDescription" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目风险分析：</label>
			<div class="controls">
				<form:textarea path="riskAnalysis" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">文档附件：</label>
			<div class="controls">
				<form:hidden id="documentAttachmentPath" path="documentAttachmentPath" htmlEscape="false" maxlength="20000" class="input-xlarge"/>
				<sys:ckfinder input="documentAttachmentPath" type="files" uploadPath="/project/approval/project" selectMultiple="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="project:approval:project:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>