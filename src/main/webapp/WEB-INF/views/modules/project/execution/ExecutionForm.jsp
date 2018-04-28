<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>合同执行管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		    treeGetParam = "?prjId=${projectExecution.apply.id}";
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
            //
		});

        // dom, index, template, json data
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

        function changeProject(projectId, idx) {
		    treeGetParam = "?prjId=" + projectId;
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {

                $("#project_code").text(apply.projectCode);
                $("#customer_name").text(apply.customer.customerName);
                $("#customer_contact_name").text(apply.customerContact.contactName);
                $("#customer_contact_phone").text(apply.customerContact.phone);
            });


            $("#contract_amount").text("");
            $("#contract_gross_margin").text("");

            $("#contractItemId").val("");
            $("#contractItemName").val("");

            $("#contractId").val("");
        }

        function changedContract(itemId, idx) {
            $.post('${ctx}/project/contract/projectContract/getItemAsJson',
                {id: itemId}, function (item) {

                if (item) {
                    $('#contract_amount').text(item.contractAmount);
                    $('#contract_gross_margin').text(item.grossProfitMargin);
                    // 合同item-id改变后，同步修改合同申请单id
                    $('#contractId').val(item.contract.id);

                    $('#amount').val(item.contractAmount);
                    $('#grossMargin').val(item.grossProfitMargin);
                    $('#executionAmount').hide();
                    $('#grossMargin').attr("disabled",true);
                } else {
                    $('#contract_amount').text("");
                    $('#contract_gross_margin').text("");
                    $('#contractId').val("");

                    $('#amount').val("");
                    $('#grossMargin').val("");

                    $('#executionAmount').show();
                }
            });
        }

	</script>
</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${ empty projectExecution.act.taskId}">
        <li><a href="${ctx}/project/execution/">合同执行列表</a></li>
    </c:if>
    <li class="active"><a href="${ctx}/project/execution/form?id=${projectExecution.id}">合同执行
        <shiro:hasPermission name="project:execution:edit">${not empty projectExecution.id?'修改':'添加'}</shiro:hasPermission>
        <shiro:lacksPermission name="project:execution:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectExecution"
           action="${ctx}/project/execution/save" method="post" class="form-horizontal">
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
            <td colspan="1" >
                    <sys:treeselect
                       id="apply"
                       name="apply.id"
                       value="${projectExecution.apply.id}"
                       labelName="apply.projectName"
                       labelValue="${projectExecution.apply.projectName}"
                       title="项目"
                       url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11"
                       cssClass="required"
                       cssStyle="width: 90%;"
                       dataMsgRequired="项目必选"
                       allowClear="true"
                       notAllowSelectParent="true"
                       customClick="changeProject"/>
                    <span class="help-inline"><font color="red">*</font> </span>
            </td>

            <td class="tit">项目编码</td>
            <td class="text-center"><label id="project_code">${projectExecution.apply.projectCode}</label></td>
            <td class="tit">项目类型</td>
            <td style="width:100px"><label id="project_category">${fns:getDictLabel(projectExecution.apply.category , 'pro_category', '')}</label></td>

        </tr>
        <tr>
            <td class="tit">客户名称</td>
            <td class="text-center"><label id="customer_name">${projectExecution.apply.customer.customerName}</label></td>
            <td class="tit">客户联系人</td>
            <td class="text-center"><label id="customer_contact_name">${projectExecution.apply.customerContact.contactName}</label></td>
            <td class="tit">客户电话</td>
            <td class="text-center"><label id="customer_contact_phone">${projectExecution.apply.customerContact.phone}</label></td>
        </tr>

        <tr>
            <td class="tit">合同号</td>
            <td class="">
                    <sys:treeselect
                        id="contractItem"
                        name="contractItem.id"
                        value="${projectExecution.contractItem.id}"
                        labelName="contractItem.contractCode"
                        labelValue="${projectExecution.contractItem.contractCode}"
                        title="合同"
                        url="/project/contract/projectContract/treeDataContractItemList"
                        cssStyle="width: 90%;"
                        allowClear="true"
                        dependBy="apply"
                        dependMsg="请先选择项目！"
                        notAllowSelectParent="true"
                        customClick="changedContract"/>
            </td>

            <td class="tit">合同金额</td>
            <td class="text-center">
                <label id="contract_amount">${projectExecution.contractItem.contractAmount}</label></td>
            <td class="tit">合同毛利率</td>
            <td class="text-center"><label id="contract_gross_margin">${projectExecution.contractItem.grossProfitMargin}</label></td>
        </tr>

        <tr id="executionAmount">
            <%--<td class="tit">执行合同号</td>--%>
            <%--<td>--%>
                <%--<form:input path="executionContractNo" style="width:88%" htmlEscape="false" maxlength="10" number="true" min="0" max="99999999" class="checkNum input-medium"/>--%>
            <%--</td>--%>
            <td class="tit" colspan="2"></td>
            <td class="tit">执行金额</td>
            <td class="text-center">
                <div class="input-append">
                <form:input path="amount" style="width:122px" htmlEscape="false" maxlength="10" number="true" min="0" max="99999999" class="checkNum input-medium"/><span class="add-on">元</span>
                </div>
            </td>
            <td class="tit">执行毛利率</td>
            <td class="text-center">
                <div class="input-append">
                    <form:input path="grossMargin"  style="width:80px" htmlEscape="false" maxlength="5" number="true" min="0" max="999" class="checkNum input-medium"/>
                    <span class="add-on">%</span>
                </div>
            </td>
        </tr>
        <tr>
            <td  class="tit"  >付款条件</td>
            <td  class="tit" colspan="5">
                <div style="white-space:nowrap;">
                <form:textarea path="paymentTerm" style="width:98%"  htmlEscape="false"  maxlength="255"/>
                </div>
            </td>
        </tr>

        <tr>
            <td  class="tit" >合同执行依据</td>
            <td class="text-center" colspan="5">
                <form:checkboxes path="executionBasisList" items="${fns:getDictList('jic_execution_basis')}" itemLabel="label" itemValue="value" htmlEscape="false" class=""/>
            </td>
        </tr>

        <tr>
            <td class="tit">交货地址</td>
            <td class="">
                <form:input path="deliveryAddress" style="width:600px" htmlEscape="false" maxlength="64" class="input-xlarge"/>
            </td>
            <td class="tit">交货联系人</td>
            <td class="">
                <form:input path="deliveryPerson" style="width:122px" htmlEscape="false" maxlength="64" class="input-xlarge"/>
            </td>
            <td class="tit">联系电话</td>
            <td class="">
                <form:input path="deliveryPhone" style="width:122px" htmlEscape="false" maxlength="64" class="input-xlarge"/>
            </td>
        </tr>

        <tr>
            <td class="tit" >文件附件</td>
            <td   colspan="5">
                <form:hidden id="attachment" path="attachment" htmlEscape="false" maxlength="20000"  />
                <sys:ckfinder input="attachment" type="files" uploadPath="/project/execution" selectMultiple="true" />
            </td>
        </tr>

            <tr>
                <td  class="tit" colspan="6">填表说明</td>
            </tr>
            <tr>
                <td colspan="6">
                    <span class="help-block" >
                        1、必须将《项目收入成本预测表》、销售合同或中标通知或用户确认单或请购单，申请采购产品品牌、型号、配置清单作为附件，否则不予批准(如已经完成销售签约流程，则无需再次上传《项目收入成本预测表》及销售合同)；<br/>
                        2、交货地址如是多地点交货,需上传附件,注明交付情况；<br/>
                        3、保修期限需注明：起止时间,如:服务需注明开始、终止日期，产品需注明自…….之日起……年，服务标准，如：7*24或5*8等等，原厂服务或第三方服务或其他。<br/>
                    </span>
                </td>
            </tr>
    </table>
    <br/><br/>

    <div class="">
        <table id="contentTable" class="table table-striped table-bordered table-condensed">
                <caption>产品配置清单</caption>
                <thead>
                <tr>
                    <th>品牌</th>
                    <th>采购金额</th>
                    <th>交货日期</th>
                    <th>保修条款</th>
                    <th>供应商</th>
                    <th>供应商渠道</th>
                    <th>联系人</th>
                    <th>电话</th>
                    <th>付款条件</th>
                    <%--<th>指定原因</th>--%>
                    <shiro:hasPermission name="project:execution:edit">
                        <th width="10">&nbsp;</th>
                    </shiro:hasPermission>
                </tr>
            </thead>
            <tbody id="executionItemList"></tbody>

            <shiro:hasPermission name="project:execution:edit">
            <tfoot>
                <tr>
                    <td colspan="9">
                        <a href="javascript:" onclick="addRow('#executionItemList', itemRowIdx, itemTpl); itemRowIdx = itemRowIdx + 1;"
                           class="btn">新增</a>
                    </td>
                </tr>
            </tfoot></shiro:hasPermission>
        </table>

        <script type="text/template" id="itemTpl">//<!--
        <tr id="executionItemList{{idx}}">
            <input id="executionItemList{{idx}}_id" name="executionItemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
            <input id="executionItemList{{idx}}_delFlag" name="executionItemList[{{idx}}].delFlag" type="hidden" value="0"/>
            <td>
                <input id="executionItemList{{idx}}_brand" name="executionItemList[{{idx}}].brand" style="width:90%" type="text"
                value="{{row.brand}}" maxlength="64" class="input-small" required/>
            </td>

            <td>
            <div class="input-append">
                <input id="executionItemList{{idx}}_amount" style="width:75%" name="executionItemList[{{idx}}].amount" type="text"
                value="{{row.amount}}" number="true" min="0" max="99999999" class="checkNum input-small" required/>
                <span class="add-on">元</span>
                </div>
            </td>

            <td>
            <input id="executionItemList{{idx}}_deliveryDate" style="width:90%" name="executionItemList[{{idx}}].deliveryDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
                    value="{{row.deliveryDate}}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
            </td>

            <td>
                <input id="executionItemList{{idx}}_warranty" name="executionItemList[{{idx}}].warranty" style="width:90%" type="text"
                value="{{row.warranty}}" maxlength="255" class="input-small" required/>
            </td>
            <td>
                <input id="executionItemList{{idx}}_supplier" name="executionItemList[{{idx}}].supplier" style="width:90%" type="text"
                value="{{row.supplier}}" maxlength="255" class="input-small" required/>
            </td>
            <td>

                <form:select id="executionItemList{{idx}}_supplierOrigin" name="executionItemList[{{idx}}].supplierOrigin" path="" style="width:90%" class="input-medium required" data-value="{{row.supplierOrigin}}">
                    <form:option value="" label=""/>
                    <form:options items="${fns:getDictList('jic_execution_origin')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="executionItemList{{idx}}_contactPerson" name="executionItemList[{{idx}}].contactPerson" type="text" style="width:90%"
                value="{{row.contactPerson}}" maxlength="255" class="input-small" />
            </td>
            <td>
                <input id="executionItemList{{idx}}_contactPhone" name="executionItemList[{{idx}}].contactPhone" style="width:90%" type="text"
                value="{{row.contactPhone}}" maxlength="255" class="input-small" />
            </td>
            <td>
                <input id="executionItemList{{idx}}_paymentTerm" name="executionItemList[{{idx}}].paymentTerm" style="width:90%" type="text"
                value="{{row.paymentTerm}}" maxlength="255" class="input-small" />
            </td>

            <td class="text-center" width="10">
                {{#delBtn}}<span class="close" onclick="delRow(this, '#executionItemList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
            </td>
        </tr>//-->
        </script>
        <script type="text/javascript">
            var itemRowIdx = 0, itemTpl = $("#itemTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
            $(document).ready(function() {
                var data = ${fns:toJson(projectExecution.executionItemList)};
                for (var i=0; i<data.length; i++){
                    addRow('#executionItemList', itemRowIdx, itemTpl, data[i]);
                    itemRowIdx = itemRowIdx + 1;
                }
                if (itemRowIdx ==0) {
                    addRow('#executionItemList', itemRowIdx, itemTpl, data[i]);
                    itemRowIdx = itemRowIdx + 1;
                }

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
        <shiro:hasPermission name="project:execution:edit">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
        <c:if test="${not empty projectExecution.id}">
            <input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
        </shiro:hasPermission>

        <shiro:hasPermission name="apply:external:projectApplyExternal:super">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
        </shiro:hasPermission>

        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
    </div>

    <c:if test="${not empty projectExecution.procInsId}">
        <act:histoicFlow procInsId="${projectExecution.procInsId}" />
    </c:if>
</form:form>
<validate:jsValidate modelAttribute="projectExecution"></validate:jsValidate>
</body>
</html>