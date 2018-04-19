<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票添加</title>
    <%-- 发票form --%>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
            // 初始化全局变量，修改表单使用
            <%-- jsp中的js代码错误一定要处理 --%>
            mOldCode = null;
            if (parent.row) {
                treeGetParam = "?prjId=" + parent.row.apply.id;
                mOldCode = parent.row.contract.contractCode;
            }
			$("#inputForm").validate({
                rules: {
                    "contract.contractCode": { // 使用name而不是id
                        required: true,
                        <%-- 自己写一个oldCode参数，后面还会默认加上正在验证的控件的值，用的是控件的name作为key，
                             如本次请求中url?oldCode=&contract.contractCode=xxx，
                             由于key中间有点，所有controller中接收最后一个参数值时，
                             应该使用@RequestParam("contract.contractCode") String code接收 --%>
                        remote: "${ctx}/project/invoice/hasCode?oldCode=" + mOldCode
                    }

                },
                messages: {
                    "contract.contractCode": {remote: "合同号已存在，请走重开流程"}
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
					// error.appendTo( element.parent().next() );
				}
			}); // end validate()

            // 必填项提示，后面加星提示
            $('.required').after('<span style="color:red">&nbsp;*</span>');

            // 当合同变化时校验
            // $("#contractName").change(function(){
            //     $("#inputForm").validate().element($("#contractName"));
            // });

            fillForm();

            $("#taxRate").on("change.select2", function(e) {
                // console.log("change.select2 "+JSON.stringify({val:e.val, added:e.added, removed:e.removed}));
                var amount = $("#amount").val();
                var taxRate = e.val;

                amount = amount || 0;

                var amountNoTaxData = {"amount": amount, "taxRate": taxRate};
                var amountNoTaxExpression = Mustache.render("round({{amount}}/(1 + {{taxRate}}), 2)", amountNoTaxData);
                var amountNoTax = math.eval(amountNoTaxExpression);

                // var amountNoTax = math.eval('round(' + amount + '/(1+0.06),2)');
                var tax = math.eval('round(' + amount + '-' + amountNoTax + ',2)');

                $("#amountNoTax").val(amountNoTax);
                $("#tax").val(tax);
            });

            $("#amount").on("change", function(e) {

            });


		});  // end init

        // 回调函数，收集子dlg中的表单数据，父页面调用，用来收集dialog中的值
        function formData() {
            if (!$("#inputForm").valid()) {
                jeesnsDialog.tips("输入有误。");
                return;
            }

            var json = form2js($('#inputForm')[0], '.', false);
            // console.log("dialog=" + JSON.stringify(json));
            return json;
        }
        
        function fillForm() {
            <c:if test="${projectInvoiceItem.func == 'front'}">
            if (parent.row) {
                js2form(document.getElementById('inputForm'), parent.row);

                var ddd = $("#invoiceType").select2();
                ddd.val(parent.row.invoiceType).trigger("change");
            }
            </c:if>
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
                });
        }

        // 选择合同后触发事件
        function changedContract(itemId, idx) {
            $.post('${ctx}/project/contract/projectContract/getAsJson',
                {id: itemId}, function (item) {
//                    $('#contractId').val(item.contract.id);
                });
        }

	</script>

    <script src="${ctxStatic}/mathjs-4.1.1/math.js" type="text/javascript"></script>
</head>
<body>
<br/>
<form:form id="inputForm" modelAttribute="projectInvoiceItem"
           action="${ctx}/project/invoice/save"
           method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="invoice.id"/>
    <sys:message content="${message}"/>
    <c:set var="func" value="${projectInvoiceItem.func}"/>

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
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">合同号:</label>
        <div class="controls">
            <%-- no used --%>
            <input id="oldContractCode" name="oldContractCode" type="hidden" value="${projectInvoiceItem.contract.contractCode}" />
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
                    cssClass="required"
                    customClick="changedContract"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">开票类型:</label>
        <div class="controls">
            <%--<form:select path="invoiceType" class="required" style="width:80%;">--%>
                <%--<form:option value="" label=""/>--%>
                <%--<form:options items="${fns:getDictList('jic_invoice_type')}" itemLabel="label" itemValue="value"/>--%>
            <%--</form:select>--%>

            <form:select path="invoiceType" items="${fns:getDictList('jic_invoice_type')}"
                         itemLabel="label" itemValue="value" class="required" style="width:80%;">
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">开票客户名称:</label>
        <div class="controls">
            <sys:treeselect id="customerInvoice"
                name="customerInvoice.id" value="${projectInvoiceItem.customerInvoice.id}"
                labelName="customerInvoice.customerName" labelValue="${projectInvoiceItem.customerInvoice.customerName}"
                title="选择开票客户"
                cssStyle="width: 85%"
                url="/customer/invoice/treeData"
                cssClass="required"  allowClear="true" notAllowSelectParent="true" />
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">开票内容:</label>
        <div class="controls">
            <form:input path="content" class="required"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">规格型号:</label>
        <div class="controls">
            <form:input path="spec"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">数量:</label>
        <div class="controls">
            <form:input path="num"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">单位:</label>
        <div class="controls">
            <form:input path="unit"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">单价:</label>
        <div class="controls">
            <form:input path="price"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">含税金额:</label>
        <div class="controls">
            <form:input path="amount"/>
        </div>
    </div>

    <shiro:hasAnyRoles name="usertask_finance_leader">
    <div class="control-group">
        <label class="control-label">税率:</label>
        <div class="controls">
            <form:select path="taxRate" style="width:80%;">
                <form:option value="" label=""/>
                <form:options items="${fns:getDictList('jic_tax_rate')}" itemLabel="label" itemValue="value"/>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">不含税金额:</label>
        <div class="controls">
            <form:input path="amountNoTax"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">税额:</label>
        <div class="controls">
            <form:input path="tax"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">发票编号:</label>
        <div class="controls">
            <form:input path="invoiceNo" class="required"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">是否作废:</label>
        <div class="controls">
            <form:select path="invalid" class="" style="width:80%;">
                <form:option value="" label=""/>
                <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value"/>
            </form:select>
        </div>
    </div>
    </shiro:hasAnyRoles>

    <div class="control-group">
        <label class="control-label">利润点:</label>
        <div class="controls">
            <form:input path="profit"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">结算周期:</label>
        <div class="controls">
            <input id="settlementBegin"  name="settlementBegin"  type="text" readonly="readonly" class="input-mini Wdate"
                   value="<fmt:formatDate value="${projectInvoiceItem.settlementBegin}" pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
            　--　
            <input id="settlementEnd" name="settlementEnd" type="text" readonly="readonly" class="input-mini Wdate"
                   value="<fmt:formatDate value="${projectInvoiceItem.settlementEnd}" pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
        </div>
    </div>

    <c:if test="${0 eq 1}">
    <div class="control-group">
        <label class="control-label">回款日期:</label>
        <div class="controls">
            <form:input path="returnDate" />
        </div>
    </div>
    </c:if>

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
<%-- 在dialog的form定义一个隐藏的btn，在dlg的按钮回调中查找并提交form，还没验证通过 --%>
    <button class="layui-btn layui-btn-small bcql-icon icon-save" style="display: none;" onclick="test();" value="test"></button>
</form:form>
</body>

</html>