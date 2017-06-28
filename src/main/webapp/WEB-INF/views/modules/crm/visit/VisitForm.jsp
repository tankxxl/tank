<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>拜访管理</title>
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/crm/visit/">拜访列表</a></li>
		<li class="active"><a href="${ctx}/crm/visit/form?id=${visit.id}">客户<shiro:hasPermission name="client:client:edit">${not empty client.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="client:client:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm"
		modelAttribute="visit"
		action="${ctx}/crm/visit/save"
		method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">客户名称：</label>
			<div class="controls">
				<sys:treeselect
						id="client"
						name="client.id"
						value="${visit.client.id}"
						labelName="client.name"
						labelValue="${visit.client.name}"
						title="用户"
						url="/sys/office/treeData?type=3"
						cssClass="input-small"
						allowClear="true"
						notAllowSelectParent="true"/>

				<form:input path="client.customerName" htmlEscape="false" maxlength="64" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户联系人：</label>
			<sys:treeselect
					id="principal"
					name="principal.id"
					value="${client.principal.id}"
					labelName="principal.name"
					labelValue="${client.principal.name}"
					title="联系人"
					url="/sys/office/treeData?type=3"
					cssClass="input-small"
					allowClear="true"
					notAllowSelectParent="true"/>
		</div>
		<div class="control-group">
			<label class="control-label">拜访标题：</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="20" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">拜访内容：</label>
			<div class="controls">
				<form:input path="content" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">拜访时间：</label>
			<div class="controls">
				<form:input path="visitDate" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="client:client:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>