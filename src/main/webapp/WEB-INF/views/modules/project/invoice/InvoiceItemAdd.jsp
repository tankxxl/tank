<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>回款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            // 初始化全局变量，修改表单使用
            <%--treeGetParam = "?prjId=${projectInvoiceItem.apply.id}";--%>
            treeGetParam = "?prjId=" + parent.row.apply.id;

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
			}); // end validate()
//            js2form(document.getElementById('inputForm'), parent.row);
//            console.log("parent.row=" + JSON.stringify(parent.row));
//            $("#invoiceType").val("专票");
//            $("#invoiceType").find("option[text='专票']").attr("selected",true);

		});  // end init

        function formData() {
            var json = form2js($('#inputForm')[0], '.', false);
            console.log("dialog=" + JSON.stringify(json));
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
//                当项目改变时，合同字段要清零
                    $("#contractId").val("");
                    $("#contractName").val("");

//                    $("#project_code").text(apply.projectCode);
//                    $("#customer_name").text(apply.customer.customerName);
//                    $("#customer_contact_name").text(apply.customerContact.contactName);
//                    $("#customer_contact_phone").text(apply.customerContact.phone);
                });
        }

        // 选择合同后触发事件
        function changedContract(itemId, idx) {
            $.post('${ctx}/project/contract/projectContract/getAsJson',
                {id: itemId}, function (item) {
//                    $('#contract_amount').text(item.contractAmount);
//                    $('#contract_gross_margin').text(item.grossProfitMargin);
//                    $('#contractId').val(item.contract.id);
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
                            cssStyle="width: 85%"
                            url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11"
                            cssClass="required"  allowClear="true" notAllowSelectParent="true"
                            customClick="changeProject"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">合同号:</label>
        <div class="controls">
            <sys:treeselect
                    id="contract"
                    name="contract.id"
                    value="${projectInvoiceItem.contract.id}"
                    labelName="contract.contractCode"
                    labelValue="${projectInvoiceItem.contract.contractCode}"
                    title="合同"
                    url="/project/contract/projectContract/treeContractList"
                    cssStyle="width: 85%"
                    allowClear="true"
                    dependBy="apply"
                    dependMsg="请先选择项目！"
                    notAllowSelectParent="true"
                    customClick="changedContract"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">开票类型:</label>
        <div class="controls">
            <form:select path="invoiceType" class="" style="width:80%;">
                <form:option value="" label=""/>
                <form:options items="${fns:getDictList('jic_invoice_type')}" itemLabel="label" itemValue="value"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">客户名称:</label>
        <div class="controls">
            <form:input path="clientName" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">开票内容:</label>
        <div class="controls">
            <form:input path="content" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">规格型号:</label>
        <div class="controls">
            <form:input path="spec" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">数量:</label>
        <div class="controls">
            <form:input path="num" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">单位:</label>
        <div class="controls">
            <form:input path="unit" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">单价:</label>
        <div class="controls">
            <form:input path="price" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">金额:</label>
        <div class="controls">
            <form:input path="amount" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">利润点:</label>
        <div class="controls">
            <form:input path="profit" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">结算周期:</label>
        <div class="controls">
            <form:input path="settlement" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">发票号:</label>
        <div class="controls">
            <form:input path="invoiceNo" class="required"/>
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