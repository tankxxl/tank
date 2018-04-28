<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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

        function changeProject(projectId, idx) {
		    treeGetParam = "?prjId=" + projectId;
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {

                $("#project_code").text(apply.projectCode);
                $("#customer_name").text(apply.customer.customerName);
                $("#customer_contact_name").text(apply.customerContact.contactName);
                $("#customer_contact_phone").text(apply.customerContact.phone);

                $("#contract_amount").text("");
                $("#contract_gross_margin").text("");
                $("#contractItemId").val("");
                $("#contractItemName").val("");

                $("#contractId").val("");
            });
        }

        function changedContract(itemId, idx) {
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
        <shiro:hasPermission name="project:invoice:edit">
        ${not empty projectInvoice.act.taskId?'审批':'查看'}</shiro:hasPermission>
        <shiro:lacksPermission name="project:invoice:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectInvoice" htmlEscape="false"
           action="${ctx}/project/invoice/saveAudit" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="act.taskId"/>
    <form:hidden path="act.taskName"/>
    <form:hidden path="act.taskDefKey"/>
    <form:hidden path="act.procInsId"/>
    <form:hidden path="act.procDefId"/>
    <form:hidden id="flag" path="act.flag"/>
    <form:hidden id="contractId" path="contract.id" />
    <sys:message content="${message}"/>
    <table class="table-form">
            <caption>项目信息</caption>
        <tr>
            <td class="tit">项目名称</td>
            <td colspan="1" class="tit_content text-center" >
            ${projectInvoice.apply.projectName}
            </td>

            <td class="tit">项目编码</td>
            <td class="text-center">
                <label id="project_code" >${projectInvoice.apply.projectCode}</label>
            </td>
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
            <td class="">${projectInvoice.contractItem.contractCode}</td>
            <td class="tit">合同金额</td>
            <td class="text-center"><label id="contract_amount">${projectInvoice.contractItem.contractAmount}</label></td>
            <%--<td class="tit">合同毛利率</td>--%>
            <%--<td class="text-center"><label id="contract_gross_margin">${projectInvoice.contractItem.grossProfitMargin}</label></td>--%>
        </tr>

        <tr>
            <td class="tit">合同类别</td>
            <td class="text-center">
                ${fns:getDictLabel(projectInvoice.contractType , 'jic_contract_type', '')}
            </td>
            <td class="tit">开票金额</td>
            <td > ${projectInvoice.invoiceAmount} </td>
        </tr>

        <tr>
            <td class="tit" >开票类型</td>
            <td class="text-center">
                ${fns:getDictLabel(projectInvoice.invoiceType , 'jic_invoice_type', '')}
            </td>

            <c:choose>
                <c:when test="${projectExecution.act.taskDefKey eq 'usertask_invoice'}">
                    <td class="tit">开票日期</td>
                    <td>
                        <input name="invoiceDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
                               value="<fmt:formatDate value="${projectInvoice.invoiceDate}" pattern="yyyy-MM-dd"/>"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                    </td>
                </c:when>
                <c:otherwise>
                    <td class="tit">开票日期</td>

                    <td><fmt:formatDate value="${projectInvoice.invoiceDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                </c:otherwise>
            </c:choose>

        </tr>

        <tr>
            <td class="tit">客户名称</td>
            <td > ${projectInvoice.customerName} </td>
            <td class="tit">税号</td>
            <td > ${projectInvoice.taxNo} </td>
        </tr>

        <tr>
            <td class="tit">开户行名称</td>
            <td > ${projectInvoice.bankName} </td>

            <td class="tit">开户行账号</td>
            <td > ${projectInvoice.bankNo} </td>

        </tr>

        <tr>
            <td class="tit">地址</td>
            <td > ${projectInvoice.address} </td>
            <td class="tit">电话</td>
            <td > ${projectInvoice.phone} </td>
        </tr>

        <tr>
            <td class="tit">备注</td>
            <td colspan="5"> ${projectInvoice.remarks} </td>
        </tr>

        <tr>
            <td class="tit" >文件附件</td>
            <td colspan="5">
                <form:hidden id="attachment" path="attachment" maxlength="20000"  />
                <sys:ckfinder input="attachment" type="files" uploadPath="/project/purchase" selectMultiple="true" readonly="true"/>
            </td>
        </tr>
        <c:if test="${not empty projectInvoice.act.taskId && projectInvoice.act.status != 'finish'}">
        <tr>
            <td class="tit">您的意见</td>
            <td colspan="5">
                <form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
                <span class="help-inline" style="color: red;">*</span>
            </td>
        </tr>
        </c:if>

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
                    <label id="total" >已开发票总计：</label>
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


        <c:if test="${ not empty returnList}">
            <table id="tableReturn" class="table table-striped table-bordered table-condensed">

                <caption>已回款信息</caption>
                <thead>
                <tr>
                    <th>回款金额</th>
                    <th>回款日期</th>
                </tr>
                </thead>
                <tbody id="returnList"></tbody>
                <tfoot>
                <tr>
                    <td>
                        <label id="totalReturn" >已回款总计：</label>
                    </td>
                    <td>
                        <label id="remainReturn" ></label>
                    </td>
                </tr>
            </table>
        </c:if>

        <script type="text/template" id="returnTpl">//<!--
        <tr id="returnList{{idx}}">
            <input id="returnList{{idx}}_id" name="returnList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
            <input id="returnList{{idx}}_delFlag" name="returnList[{{idx}}].delFlag" type="hidden" value="0"/>
            <td>{{row.amount}}</td>
            <td>{{row.returnDate}}</td>
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
                <th>本次结转成本金额</th>
                <shiro:hasPermission name="project:invoice:edit">
                    <th width="10">&nbsp;</th>
                </shiro:hasPermission>
            </tr>
            </thead>
            <tbody id="invoiceItemList"></tbody>
        </table>

        <%--开票信息--%>
        <script type="text/template" id="itemTpl">//<!--
        <tr id="invoiceItemList{{idx}}">
            <input id="invoiceItemList{{idx}}_id" name="invoiceItemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
            <input id="invoiceItemList{{idx}}_delFlag" name="invoiceItemList[{{idx}}].delFlag" type="hidden" value="0"/>
            <td> {{row.goodsName}} </td>
            <td> {{row.spec}} </td>

            <td> {{row.num}} </td>

            <td> {{row.price}} </td>
            <td> {{row.amount}} </td>

            <td> {{row.costAmount}} </td>
        </tr>//-->
        </script>
        <script type="text/javascript">

            <%--初始化全局变量--%>
            <%--treeGetParam = "?prjId=" + ${projectExecution.apply.id};--%>
            var itemRowIdx = 0, itemTpl = $("#itemTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
            // another table
            var rowIdx = 0, entityTpl = $("#entityTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");

            var rowReturnIdx = 0, returnTpl = $("#returnTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");

            $(document).ready(function() {
//                开票信息-只读
                var data = ${fns:toJson(projectInvoice.invoiceItemList)};
                for (var i=0; i<data.length; i++){
                    addRow('#invoiceItemList', itemRowIdx, itemTpl, data[i]);
                    itemRowIdx = itemRowIdx + 1;
                }
                if (itemRowIdx ==0) {
                    addRow('#invoiceItemList', itemRowIdx, itemTpl, data[i]);
                    itemRowIdx = itemRowIdx + 1;
                }

//                已开发票信息-只读
                var data1 = ${fns:toJson(projectInvoiceList)};
                var sum = 0;
                for (var i=0; i<data1.length; i++){
                    addRow('#invoiceList', rowIdx, entityTpl, data1[i]);
                    sum = sum + Number(data1[i].invoiceAmount);
                    rowIdx = rowIdx + 1;
                }
                $("#total").text("已开票金额总计：" + sum);

                // 已回款信息-只读
                var dataReturn = ${fns:toJson(returnList)};
                var sum = 0;
                for (var i=0; i<dataReturn.length; i++){
                    addRow('#returnList', rowReturnIdx, returnTpl, dataReturn[i]);
                    sum = sum + Number(dataReturn[i].amount);
                    rowReturnIdx = rowReturnIdx + 1;
                }
                $("#totalReturn").text("已回款金额总计：" + sum);

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

        <c:if test="${not empty projectInvoice.act.taskId && projectInvoice.act.status != 'finish'}">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
            <input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
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