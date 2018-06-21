<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            // 验证值小数位数不能超过两位
            jQuery.validator.addMethod("decimal", function (value, element) {
                var decimal = /^-?\d+(\.\d{1,2})?$/;
                return this.optional(element) || (decimal.test(value));
            }, $.validator.format("小数位数不能超过两位!"));

            // 初始化全局变量，修改表单使用
		    treeGetParam = "?prjId=${projectInvoice.apply.id}";

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

            $("#invoiceType").change(function(){
                if(isNaN(this.value) ){
                    return false;
                }
//                alert(this.value);

                removeRequired("#bankName");
                removeRequired("#customerName");
                removeRequired("#taxNo");
                removeRequired("#bankNo");
                removeRequired("#address");
                removeRequired("#phone");

                var invoiceType = parseInt(this.value);
                if (invoiceType == 1) { // 增值税专用发票6%
                    addRequired("#bankName");
                    addRequired("#customerName");
                    addRequired("#taxNo");
                    addRequired("#bankNo");
                    addRequired("#address");
                    addRequired("#phone");
                } else if (invoiceType == 2) { // 增值税专用发票17%
                    addRequired("#bankName");
                    addRequired("#customerName");
                    addRequired("#taxNo");
                    addRequired("#bankNo");
                    addRequired("#address");
                    addRequired("#phone");
                } else if (invoiceType == 3) { // 增值税普通发票6%
                    addRequired("#bankName");
                } else if (invoiceType == 4) { // 增值税普通发票17%
//                    alert("four");
                }
            });
		});

        // dom(tbody), index, template, json data
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
					if($(this).val() == ss[i]) {
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

        // 选择项目后触发事件
        function changeProject(tree, prjId, prjName) {

            // JavaScript全局变量，用于传递参数，新建表单使用。
		    treeGetParam = "?prjId=" + prjId;
            $('#contractItemId').data('url', '/project/contract/projectContract/treeDataContractItemList?prjId=' + prjId);
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: prjId},
                function (apply) {
                $("#project_code").text(apply.projectCode);
                $("#customer_name").text(apply.customer.customerName);
                $("#customer_contact_name").text(apply.customerContact.contactName);
                $("#customer_contact_phone").text(apply.customerContact.phone);

                //清除合同相关的值
                $("#contract_amount").text("");
                $("#contract_gross_margin").text("");
                $("#contractItemId").val("");
                $("#contractItemName").val("");

                $("#contractId").val("");
            });
        }

        // 选择合同后触发事件
        function changedContract(tree, itemId, itemName) {
            $.post('${ctx}/project/contract/projectContract/getItemAsJson',
                {id: itemId}, function (item) {

                if (item) {
                    $('#contract_amount').text(item.contractAmount);
                    $('#contract_gross_margin').text(item.grossProfitMargin);
                    $('#contractId').val(item.contract.id);
                } else {
                    $('#contract_amount').text("");
                    $('#contract_gross_margin').text("");
                    $('#contractId').val("");
                }

            });
        }

	</script>
</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${ empty projectInvoice.act.taskId}">
        <li><a href="${ctx}/project/invoice/">开票列表</a></li>
    </c:if>
    <li class="active"><a href="${ctx}/project/invoice/form?id=${projectInvoice.id}">开票
        <shiro:hasPermission name="project:invoice:edit">${not empty projectInvoice.id?'修改':'添加'}</shiro:hasPermission>
        <shiro:lacksPermission name="project:invoice:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<%--<spring:htmlEscape defaultHtmlEscape="false" />--%>
<form:form id="inputForm" modelAttribute="projectInvoice" htmlEscape="false"
           action="${ctx}/project/invoice/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="act.taskId"/>
    <form:hidden path="act.taskName"/>
    <form:hidden path="act.taskDefKey"/>
    <form:hidden path="act.procInsId"/>
    <form:hidden path="act.procDefId"/>
    <form:hidden id="flag" path="act.flag"/>
    <%-- 设置id，前端设置值，传回后端，必须 --%>
    <form:hidden id="contractId" path="contract.id" />
    <sys:message content="${message}"/>
    <table class="table-form">
            <caption>项目信息</caption>
        <tr>
            <td class="tit">项目名称</td>
            <td colspan="1" >
                <div style="white-space:nowrap;" >
                    <sys:treeselect id="apply"
                       name="apply.id" value="${projectInvoice.apply.id}"
                       labelName="apply.projectName"
                       labelValue="${projectInvoice.apply.projectName}"
                       title="项目"
                       url="/apply/external/projectApplyExternal/treeData?proMainStage=11"
                       cssClass="required" cssStyle="width: 85%" dataMsgRequired="项目必选"
                       allowClear="true" notAllowSelectParent="true"
                       customFuncOnOK="changeProject"/>
                    <span class="help-inline" style="color: red;">*</span>
                </div>
            </td>

            <td class="tit">项目编码</td>
            <td class="text-center"><label id="project_code" >${projectInvoice.apply.projectCode}</label></td>
        </tr>
        <tr>
            <td class="tit">客户名称</td>
            <td class="text-center"><label id="customer_name">${projectInvoice.apply.customer.customerName}</label></td>
            <td class="tit">联系方式</td>
            <td class="text-center">
                <label id="customer_contact_name">${projectInvoice.apply.customerContact.contactName}</label>
                <label id="customer_contact_phone">${projectInvoice.apply.customerContact.phone}</label>
            </td>
        </tr>

        <tr>
            <td class="tit">销售合同号</td>
            <td class="">
                <div style="white-space:nowrap;" >
                    <sys:treeselect
                            id="contractItem"
                            name="contractItem.id"
                            value="${projectInvoice.contractItem.id}"
                            labelName="contractItem.contractCode"
                            labelValue="${projectInvoice.contractItem.contractCode}"
                            title="合同"
                            url="/project/contract/projectContract/treeDataContractItemList"
                            cssStyle="width: 85%"
                            allowClear="true" dependBy="apply" dependMsg="请先选择项目！"
                            notAllowSelectParent="true"
                            customFuncOnOK="changedContract"/>
                </div>
            </td>
            <td class="tit">合同金额</td>
            <td class="text-center"><label id="contract_amount">${projectInvoice.contractItem.contractAmount}</label></td>
            <%--<td class="tit">合同毛利率</td>--%>
            <%--<td class="text-center"><label id="contract_gross_margin">${projectInvoice.contractItem.grossProfitMargin}</label></td>--%>
        </tr>

        <tr>
            <td  class="tit" >合同类别</td>
            <td class="text-center" >
                <form:select path="contractType" class="" style="width:89%;">
                    <form:option value="" label=""/>
                    <form:options items="${fns:getDictList('jic_contract_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                </form:select>
            </td>

            <td  class="tit" >本次开票金额</td>
            <td  >
                <div class="input-append">
                    <form:input path="invoiceAmount" style="width:100%" number="true" min= "0" max="99999999" class="checkNum input-medium required" />
                    <span class="add-on">元</span>
                </div>
            </td>
        </tr>

        <tr>
            <td  class="tit" >开票类型</td>
            <td class="text-center">
                <form:select path="invoiceType" class="required" style="width:89%;">
                    <form:option value="" label=""/>
                    <form:options items="${fns:getDictList('jic_invoice_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                </form:select>
            </td>

            <td class="tit">预计回款日期</td>
            <td>
                <input name="returnDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
                       value="<fmt:formatDate value="${projectInvoice.returnDate}" pattern="yyyy-MM-dd"/>"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false, minDate: new Date() });"/>
            </td>
        </tr>

        <%--<tr>--%>
            <%--<td  class="tit" colspan="2"></td>--%>
            <%--<td class="tit">开票日期</td>--%>
            <%--<td>--%>
                <%--<input name="invoiceDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"--%>
                       <%--value="<fmt:formatDate value="${projectInvoice.invoiceDate}" pattern="yyyy-MM-dd"/>"--%>
                       <%--onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>--%>
            <%--</td>--%>
        <%--</tr>--%>

        <tr>
            <td class="tit">客户名称</td>
            <td ><form:input path="customerName" style="width:93%" maxlength="100" class="input-medium"/> </td>
            <td class="tit">税号</td>
            <td ><form:input path="taxNo"  style="width:93%" maxlength="10" class="input-medium"/> </td>
        </tr>

        <tr>
            <td class="tit">开户行名称</td>
            <td ><form:input path="bankName" style="width:93%" class="input-medium"/> </td>
            <td class="tit">开户行账号</td>
            <td ><form:input path="bankNo" style="width:93%" maxlength="100" class="input-medium"/> </td>
        </tr>

        <tr>
            <td class="tit">地址</td>
            <td > <form:input path="address"  style="width:93%" class="input-medium"/> </td>
            <td class="tit">电话</td>
            <td > <form:input path="phone" style="width:93%" class="input-medium"/> </td>
        </tr>

        <tr>
            <td  class="tit"  >备注</td>
            <td   colspan="5">
                <div style="white-space:nowrap;">
                    <form:textarea path="remarks" style="width:98%"  maxlength="255"
                                   placeholder=""/>
                </div>
            </td>
        </tr>

        <tr>
            <td class="tit" >文件附件</td>
            <td   colspan="5">
                <form:hidden id="attachment" path="attachment" maxlength="20000"  />
                <sys:ckfinder input="attachment" type="files" uploadPath="/project/purchase" selectMultiple="true" />
            </td>
        </tr>

        <tr>
            <td  class="tit" colspan="6">填表说明</td>
        </tr>
        <tr>
            <td colspan="6">
                <span class="help-block" >
                    1、 必须将《项目收入成本预测表》作为附件，否则不予批准；<br/>
                    2、 售前工程师、项目经理、解决方案部负责人、服务交付部或软件开发部负责人、技术部分管领导负责逐级确认投标书中的技术部分，包括产品配置、技术方案、技术偏离表、技术承诺等内容；<br/>
                </span>
            </td>
        </tr>
    </table>
    <br/><br/>

    <div class="">

        <c:if test="${ not empty projectInvoiceList}">
        <table id="contentTable1" class="table table-striped table-bordered table-condensed">

            <caption>已开发票信息</caption>
            <thead>
            <tr>
                <th>开票金额</th>
                <th>开票日期</th>
            </tr>
            </thead>
            <tbody id="invoiceList"></tbody>
            <tfoot>
            <tr>
                <td>
                    <label id="total" >已开发票总计：123</label>
                </td>
                <td>
                    <label id="remain" ></label>
                </td>
            </tr>
        </table>
        </c:if>

        <script type="text/template" id="entityTpl">//<!--
        <tr id="invoiceList{{idx}}">
            <input id="invoiceList{{idx}}_id" name="invoiceList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
            <input id="invoiceList{{idx}}_delFlag" name="invoiceList[{{idx}}].delFlag" type="hidden" value="0"/>
            <td>{{row.invoiceAmount}}</td>
            <td>{{row.invoiceDate}}</td>
        </tr>//-->
        </script>


        <table id="contentTable" class="table table-striped table-bordered table-condensed">
                <%--<thead>--%>
                <%--<tr><td class="tit">产品配置清单</td> <tr>--%>
            <caption>本次申请开票信息</caption>
            <thead>
            <tr>
                <th>商品名称</th>
                <th>规格型号</th>
                <th>数量(套)</th>
                <th>单价(含税)</th>
                <th>金额(含税)</th>
                <%--<th>本次结转成本金额</th>--%>
                <shiro:hasPermission name="project:invoice:edit">
                    <th width="10">&nbsp;</th>
                </shiro:hasPermission>
            </tr>
            </thead>
            <tbody id="invoiceItemList"></tbody>

            <shiro:hasPermission name="project:invoice:edit">
            <tfoot>
            <tr>
                <td colspan="6">
                    <a href="javascript:" onclick="addRow('#invoiceItemList', itemRowIdx, itemTpl); itemRowIdx = itemRowIdx + 1;"
                       class="btn">新增</a>
                </td>
            </tr>
            </tfoot></shiro:hasPermission>
        </table>

        <script type="text/template" id="itemTpl">//<!--
        <tr id="invoiceItemList{{idx}}">
            <input id="invoiceItemList{{idx}}_id" name="invoiceItemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
            <input id="invoiceItemList{{idx}}_delFlag" name="invoiceItemList[{{idx}}].delFlag" type="hidden" value="0"/>
            <td>
                <input id="invoiceItemList{{idx}}_goodsName" name="invoiceItemList[{{idx}}].goodsName" style="width:90%" type="text"
                value="{{row.goodsName}}" maxlength="64" class="input-small" required/>
            </td>
            <td>
                <input id="invoiceItemList{{idx}}_spec" style="width:90%" name="invoiceItemList[{{idx}}].spec" type="text"
                value="{{row.spec}}" class="input-small" required/>
            </td>

            <td>
                <input id="invoiceItemList{{idx}}_num" style="width:90%" name="invoiceItemList[{{idx}}].num" type="text" maxlength="20" number="true" min="0" max="99999999" class="checkNum input-small"
                    value="{{row.num}}" required/>
            </td>

            <td>
                <div class="input-append">
                <input id="invoiceItemList{{idx}}_price" name="invoiceItemList[{{idx}}].price" style="width:85%" type="text"
                value="{{row.price}}" maxlength="25" number="true" min="0" max="99999999" class="checkNum input-small" required/>
                <span class="add-on">元</span>
                </div>

            </td>
            <td>
                <div class="input-append">
                <input id="invoiceItemList{{idx}}_amount" name="invoiceItemList[{{idx}}].amount" style="width:85%" type="text"
                value="{{row.amount}}" maxlength="25" number="true" min="0" max="99999999" class="checkNum input-small" required/>
                <span class="add-on">元</span>
                </div>
            </td>



            <td class="text-center" width="10">
                {{#delBtn}}<span class="close" onclick="delRow(this, '#invoiceItemList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
            </td>
        </tr>//-->
        </script>
        <script type="text/javascript">

            <%--初始化全局变量--%>
            <%--treeGetParam = "?prjId=" + ${projectExecution.apply.id};--%>
            var itemRowIdx = 0, itemTpl = $("#itemTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
            // another table
            var rowIdx = 0, entityTpl = $("#entityTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");

            $(document).ready(function() {
                var data = ${fns:toJson(projectInvoice.invoiceItemList)};
                for (var i=0; i<data.length; i++){
                    addRow('#invoiceItemList', itemRowIdx, itemTpl, data[i]);
                    itemRowIdx = itemRowIdx + 1;
                }
                if (itemRowIdx ==0) {
                    addRow('#invoiceItemList', itemRowIdx, itemTpl, data[i]);
                    itemRowIdx = itemRowIdx + 1;
                }

                var data1 = ${fns:toJson(projectInvoiceList)};
                var sum = 0;
                for (var i=0; i<data1.length; i++){
                    addRow('#invoiceList', rowIdx, entityTpl, data1[i]);
                    sum = sum + Number(data1[i].invoiceAmount);
                    rowIdx = rowIdx + 1;
                }
                $("#total").text("已开票金额总计：" + sum);

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

            }); // end ready
        </script>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="project:invoice:edit">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
        <c:if test="${not empty projectInvoice.id}">
            <input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
        </shiro:hasPermission>

        <%--<shiro:hasPermission name="apply:external:projectApplyExternal:onlySave">--%>
            <%--<input id="btnSubmit" class="btn btn-primary" type="submit" onclick="$('#flag').val('saveOnly')" value="保存"/>&nbsp;--%>
        <%--</shiro:hasPermission>--%>

        <shiro:hasPermission name="apply:external:projectApplyExternal:super">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
        </shiro:hasPermission>

        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
    </div>

    <c:if test="${not empty projectInvoice.procInsId}">
        <act:histoicFlow procInsId="${projectInvoice.procInsId}" />
    </c:if>
</form:form>
<validate:jsValidate modelAttribute="projectInvoice"></validate:jsValidate>
</body>
</html>