<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		    treeGetParam = "?prjId=${projectPurchase.apply.id}";

			//$("#name").focus();
			$("#inputForm").validate({
                rules: {
                    estimatedGrossProfitMargin: {
                        required: true,
                        range: [0,100],
                        minlength: 2
                    }
                },
                messages: {
                    estimatedGrossProfitMargin: "Please specify your name",
                    email: {
                        required: "We need your email address to contact you",
                        email: "Your email address must be in the format of name@domain.com",
                        minlength: jQuery.validator.format("At least {0} characters required!")
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
						error.appendTo(element.parent().parent()); // 后面
					} else {
						error.insertAfter(element);
					}
				}
			});
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
        }

        function changedContract(itemId, idx) {
            $.post('${ctx}/project/contract/projectContract/getItemAsJson',
                {id: itemId}, function (item) {
                console.log(item);
                $('#contract_amount').text(item.contractAmount);
                $('#contract_gross_margin').text(item.grossProfitMargin);
                $('#contractId').val(item.contract.id);
            });
        }

	</script>
</head>
<body>
<ul class="nav nav-tabs">
    <%--<c:if test="${ empty projectPurchase.act.taskId}">--%>
        <%--<li><a href="${ctx}/project/purchase/">合同采购列表</a></li>--%>
    <%--</c:if>--%>
    <li class="active"><a href="${ctx}/project/purchase/form?id=${projectPurchase.id}">合同采购
        <shiro:hasPermission name="project:purchase:edit">${not empty projectPurchase.id?'修改':'添加'}</shiro:hasPermission>
        <shiro:lacksPermission name="project:purchase:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectPurchase" htmlEscape="false"
           action="${ctx}/project/purchase/save" method="post" class="form-horizontal">
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
                <div style="white-space:nowrap;" >
                    <sys:treeselect
                       id="apply"
                       name="apply.id"
                       value="${projectPurchase.apply.id}"
                       labelName="apply.projectName"
                       labelValue="${projectPurchase.apply.projectName}"
                       title="项目"
                       url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11"
                       cssClass="required"
                       cssStyle="width: 85%"
                       dataMsgRequired="项目必选"
                       allowClear="true"
                       notAllowSelectParent="true"
                       customClick="changeProject"/>
                    <span class="help-inline"><font color="red">*</font> </span>
                </div>
            </td>

            <td class="tit">项目编码</td>
            <td class="text-center"><label id="project_code" >${projectPurchase.apply.projectCode}</label></td>
            <td class="tit">项目类型</td>
            <td style="width:100px"><label id="project_category">${fns:getDictLabel(projectPurchase.apply.category , 'pro_category', '')}</label></td>

        </tr>
        <tr>
            <td class="tit">客户名称</td>
            <td class="text-center"><label id="customer_name">${projectPurchase.apply.customer.customerName}</label></td>
            <td class="tit">客户联系人</td>
            <td class="text-center"><label id="customer_contact_name">${projectPurchase.apply.customerContact.contactName}</label></td>
            <td class="tit">客户电话</td>
            <td class="text-center"><label id="customer_contact_phone">${projectPurchase.apply.customerContact.phone}</label></td>
        </tr>

        <tr>
            <td class="tit">销售合同号</td>
            <td class="">
                <div style="white-space:nowrap;" >
                    <sys:treeselect
                            id="contractItem"
                            name="contractItem.id"
                            value="${projectPurchase.contractItem.id}"
                            labelName="contractItem.contractCode"
                            labelValue="${projectPurchase.contractItem.contractCode}"
                            title="合同"
                            url="/project/contract/projectContract/treeDataContractItemList"
                            cssStyle="width: 85%"
                            allowClear="true"
                            dependBy="apply"
                            dependMsg="请先选择项目！"
                            notAllowSelectParent="true"
                            customClick="changedContract"/>
                </div>
            </td>
            <td class="tit">合同金额</td>
            <td class="text-center"><label id="contract_amount">${projectPurchase.contractItem.contractAmount}</label></td>
            <td class="tit">合同毛利率</td>
            <td class="text-center"><label id="contract_gross_margin">${projectPurchase.contractItem.grossProfitMargin}</label></td>
        </tr>

            <tr>
                <td  class="tit" >销售合同状况</td>
                <td class="text-center" colspan="3">
                    <form:checkboxes path="executionBasisList" items="${fns:getDictList('jic_execution_basis')}" itemLabel="label" itemValue="value" htmlEscape="false" class=""/>

                    <a href="${ctx}/project/execution/view?id=${projectPurchase.execution.id}" target="_jeesnsOpen"
                       title="${projectPurchase.apply.projectName}" width="800px" height="500px">
                        <span class="label label-info">查看合同执行</span>
                    </a>

                </td>
                <td class="tit">合同类型</td>
                <td class="text-center">
                    <form:select path="contractType">
                        <form:options items="${fns:getDictList('jic_contract_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                    </form:select>
                </td>
            </tr>

        <tr>
            <td class="tit">采购合同号</td>
            <td>
                <form:input path="purchaseCode" style="width:95%" maxlength="10" class="input-medium"/>
            </td>
            <td class="tit">供应商来源</td>
            <td>
                <form:select path="supplierOrigin" style="width:95%;">
                    <%--<form:option value="" label=""/>--%>
                    <form:options items="${fns:getDictList('jic_execution_origin')}" itemLabel="label" itemValue="value"/>
                </form:select>

            </td>
            <td class="tit">采购金额</td>
            <td>
                <div class="input-append">
                <form:input path="amount" style="width:60%" maxlength="10" number="true" min= "0" max="99999999" class="checkNum input-medium"/>
                <span class="add-on">元</span>
                </div>
            </td>
        </tr>
        <tr>
            <td class="tit">供应商名称</td>
            <td >
                <form:input path="supplier" style="width:95%" maxlength="100" class="input-medium"/>
            </td>
            <td class="tit">联系人</td>
            <td class="text-center">
                <form:input path="supplierPerson"  style="width:95%" maxlength="10" class="input-medium"/>

            </td>
            <td class="tit">电话</td>
            <td class="text-center">
                <form:input path="supplierPhone" style="width:90%" maxlength="10" class="input-medium"/>
            </td>
        </tr>


            <tr>
                <td  class="tit"  >采购金额</td>
                <td  class="tit" colspan="5">
                    <div style="white-space:nowrap;">
                        <form:textarea path="amountInfo" style="width:98%"  maxlength="255"
                           placeholder="采购金额如不在预算内请说明"/>
                    </div>
                </td>
            </tr>

            <tr>
                <td  class="tit"  >付款条件</td>
                <td  class="tit" colspan="5">
                    <div style="white-space:nowrap;">
                        <form:textarea path="paymentInfo" style="width:98%"  maxlength="255"
                           placeholder="采购合同付款条件如不与销售合同背靠背请说明"/>
                    </div>
                </td>
            </tr>

            <tr>
                <td  class="tit"  >产品配置清单</td>
                <td  class="tit" colspan="5">
                    <div style="white-space:nowrap;">
                        <form:textarea path="inventoryInfo" style="width:98%" maxlength="255"
                            placeholder="采购合同配置如不与项目需求相符请说明"/>
                    </div>
                </td>
            </tr>

            <tr>
                <td  class="tit"  >保修条款</td>
                <td  class="tit" colspan="5">
                    <div style="white-space:nowrap;">
                        <form:textarea path="warrantyInfo" style="width:98%"  maxlength="255"
                           placeholder="采购合同保修条款如不与项目需求相符请说明"/>
                    </div>
                </td>
            </tr>


            <tr>
                <td  class="tit"  >交货日期</td>
                <td  class="tit" colspan="5">
                    <div style="white-space:nowrap;">
                        <form:textarea path="deliveryInfo" style="width:98%" maxlength="255"
                            placeholder="采购合同交货日期如不与项目需求相符请说明"/>
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

    <div class="form-actions">
        <shiro:hasPermission name="project:purchase:edit">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
        <c:if test="${not empty projectPurchase.id}">
            <input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
        </shiro:hasPermission>

        <shiro:hasPermission name="apply:external:projectApplyExternal:super">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
        </shiro:hasPermission>

        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
    </div>

    <c:if test="${not empty projectPurchase.procInsId}">
        <act:histoicFlow procInsId="${projectPurchase.procInsId}" />
    </c:if>
</form:form>
<validate:jsValidate modelAttribute="projectPurchase"></validate:jsValidate>
</body>
</html>