<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>回款管理</title>
	<meta name="decorator" content="default"/>
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
		});  // end init

        function formData() {
            var json = form2js($('#inputForm')[0]);
            var json2 = $('#inputForm').serializeJsonObject();
            console.log("json2=" + JSON.stringify(json2));
            console.log(JSON.stringify(json));
            console.log(json);
            return json;
        }

        // 选择项目后触发事件
        function changeProject(projectId, idx) {
            // JavaScript全局变量，用于传递参数，新建表单使用。
            treeGetParam = "?prjId=" + projectId;
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                { id: projectId },
                function (apply) {
                    $("#project_code").text(apply.projectCode);
                    $("#customer_name").text(apply.customer.customerName);
                    $("#customer_contact_name").text(apply.customerContact.contactName);
                    $("#customer_contact_phone").text(apply.customerContact.phone);
                });
        }

        // 选择合同后触发事件
        function changedContract(itemId, idx) {
            $.post('${ctx}/project/contract/projectContract/getItemAsJson',
                {id: itemId}, function (item) {
                    $('#contract_amount').text(item.contractAmount);
                    $('#contract_gross_margin').text(item.grossProfitMargin);
                    $('#contractId').val(item.contract.id);
                });
        }

	</script>
</head>
<body>


<form:form id="inputForm" modelAttribute="projectInvoiceItem"
           action="${ctx}/project/invoice/save"
           method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">项目:</label>
        <div class="controls">
            <sys:treeselect id="apply" name="apply.id" value="${projectInvoiceItem.apply.id}"
                            labelName="apply.projectName" labelValue="${projectInvoiceItem.apply.projectName}"
                            title="项目名称"
                            url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11"
                            cssClass="required"  allowClear="true" notAllowSelectParent="true"
                            customClick="changeProject"/>

            <%--<form:input path="customerName" class="required"/>--%>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">客户名称:</label>
        <div class="controls">
            <form:input path="goodsName" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">客户名称:</label>
        <div class="controls">
            <form:input path="spec" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">开票内容:</label>
        <div class="controls">
            <form:input path="num" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">规格型号:</label>
        <div class="controls">
            <form:input path="price" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">数量:</label>
        <div class="controls">
            <form:input path="apply.projectCode" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">单位:</label>
        <div class="controls">
            <form:input path="apply.projectName" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">单价:</label>
        <div class="controls">
            <form:input path="apply.category" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">金额:</label>
        <div class="controls">
            <form:input path="apply.description" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">利润点:</label>
        <div class="controls">
            <form:input path="apply.estimatedGrossProfitMarginDescription" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">结算周期:</label>
        <div class="controls">
            <form:input path="apply.riskAnalysis" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">合同号:</label>
        <div class="controls">
            <form:input path="apply.ownership" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">发票号:</label>
        <div class="controls">
            <form:input path="apply.proMainStage" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">备注:</label>
        <div class="controls">
            <form:textarea path="remarks" rows="3" maxlength="200" class="input-xlarge"/>
        </div>
    </div>
    <%--<div class="form-actions">--%>
        <%--<shiro:hasPermission name="sys:role:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>--%>
        <%--<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>--%>
    <%--</div>--%>
</form:form>
</body>
</html>