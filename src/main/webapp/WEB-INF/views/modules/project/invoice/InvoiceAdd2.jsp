<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>回款管理</title>
	<meta name="decorator" content="default"/>
    <%-- jeesns cols-12 --%>
	<script type="text/javascript">
		$(document).ready(function() {

            // 验证值小数位数不能超过两位
            jQuery.validator.addMethod("decimal", function (value, element) {
                var decimal = /^-?\d+(\.\d{1,2})?$/;
                return this.optional(element) || (decimal.test(value));
            }, $.validator.format("小数位数不能超过两位!"));

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
<div class="wrapper">

    <section class="content">
        <div class="row">
            <div class="col-md-12">

<form:form id="inputForm" modelAttribute="projectInvoice"
           action="${ctx}/project/invoice/save"
           method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>

    <div class="form-group">
        <label class="col-sm-1 control-label">项目:</label>
        <div class="col-sm-3">
            <form:input path="customerName" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-1 control-label">客户名称:</label>
        <div class="col-sm-3">
            <form:input path="taxNo" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">客户名称:</label>
        <div class="col-sm-3">
            <form:input path="bankName" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">开票内容:</label>
        <div class="col-sm-3">
            <form:input path="bankNo" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">规格型号:</label>
        <div class="col-sm-3">
            <form:input path="phone" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">数量:</label>
        <div class="col-sm-3">
            <form:input path="apply.projectCode" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">单位:</label>
        <div class="col-sm-3">
            <form:input path="apply.projectName" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">单价:</label>
        <div class="col-sm-3">
            <form:input path="apply.category" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">金额:</label>
        <div class="col-sm-3">
            <form:input path="apply.description" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-1 control-label">利润点:</label>
        <div class="col-sm-3">
            <form:input path="apply.estimatedGrossProfitMarginDescription" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="control-group">
        <label class="col-sm-1 control-label">结算周期:</label>
        <div class="col-sm-3">
            <form:input path="apply.riskAnalysis" class="required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="control-group">
        <label class="col-sm-1 control-label">合同号:</label>
        <div class="col-sm-3">
            <form:input path="apply.ownership" class=" form-control required"/>
            <span class="help-inline"><font color="red">*</font> 工作流用户组标识</span>
        </div>
    </div>

    <div class="control-group">
        <label class="col-sm-1 control-label">发票号:</label>
        <div class="col-sm-8">
            <form:input path="apply.proMainStage" class="form-control required"/>

        </div>
    </div>

    <div class="control-group">
        <label class="col-sm-1 control-label">备注:</label>
        <div class="col-sm-3">
            <form:textarea path="remarks" rows="3" maxlength="200" class="input-xlarge"/>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="sys:role:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

            </div>
        </div>
    </section>
</div>
</body>
</html>