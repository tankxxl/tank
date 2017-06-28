<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
            // 初始化全局变量，修改表单使用
		    treeGetParam = "?prjId=${projectPurchase.apply.id}";

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

        // 选择项目后触发事件
        function changeProject(projectId, idx) {

            // JavaScript全局变量，用于传递参数，新建表单使用。
		    treeGetParam = "?prjId=" + projectId;
		    console.log("changeProject=" + treeGetParam);
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {

                $("#project_code").text(apply.projectCode);
                $("#customer_name").text(apply.customer.customerName);
                $("#customer_contact_name").text(apply.customerContact.contactName);
                $("#customer_contact_phone").text(apply.customerContact.phone);

//                treeUrl = apply.id;
                <%--var ss = ${fns:getDictLabel(apply.category , 'pro_category', apply.category)};--%>
//                console.log(ss);
//                $("#project_category").text(ss);
            });
        }

        // 选择合同后触发事件
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
    <c:if test="${ empty projectPurchase.act.taskId}">
        <li><a href="${ctx}/project/purchase/">合同采购列表</a></li>
    </c:if>
    <li class="active"><a href="${ctx}/project/purchase/form?id=${projectPurchase.id}">合同采购
        <shiro:hasPermission name="project:purchase:edit">
            ${not empty projectPurchase.act.taskId?'审批':'查看'}
        </shiro:hasPermission>
        <shiro:lacksPermission name="project:purchase:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<%--<spring:htmlEscape defaultHtmlEscape="false" />--%>
<form:form id="inputForm" modelAttribute="projectPurchase" htmlEscape="false"
           action="${ctx}/project/purchase/saveAudit" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="act.taskId"/>
    <form:hidden path="act.taskName"/>
    <form:hidden path="act.taskDefKey"/>
    <form:hidden path="act.procInsId"/>
    <form:hidden path="act.procDefId"/>
    <form:hidden id="flag" path="act.flag"/>
    <%--设置id，前端设置值，传回后端--%>
    <form:hidden id="contractId" path="contract.id" />
    <sys:message content="${message}"/>
    <table class="table-form">
        <%--<tr>--%>
            <%--<th colspan="6" class="tit">项目信息</th>--%>
        <%--</tr>--%>
            <caption>项目信息</caption>
        <tr>
            <td class="tit">项目名称</td>
            <td colspan="1" class="tit_content text-center">${projectPurchase.apply.projectName}</td>

            <td class="tit">项目编码</td>
            <td class=" text-center">
                <label id="project_code">${projectPurchase.apply.projectCode }</label></td>
            <td class="tit">项目类型</td>
            <td class="text-center"><label id="project_category">${fns:getDictLabel(projectPurchase.apply.category , 'pro_category', '')}</label></td>

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
                <label>${projectPurchase.contractItem.contractCode}</label>
            </td>
            <td class="tit">合同金额</td>
            <td class="text-center"><label>${projectPurchase.contractItem.contractAmount}</label></td>
            <td class="tit">合同毛利率</td>
            <td class="text-center"><label>${projectPurchase.contractItem.grossProfitMargin}</label></td>
        </tr>

        <tr>
            <td  class="tit" >销售合同状况</td>
            <td class="text-center" colspan="3">
                ${fns:getDictLabels(projectPurchase.contractInfo, 'jic_execution_basis', '')}
            </td>
            <td class="tit">合同类型</td>
            <td class="text-center">
                ${fns:getDictLabels(projectPurchase.contractType, 'jic_contract_type', '')}
            </td>
        </tr>

        <tr>
            <td class="tit">采购合同号</td>
            <td>
                ${projectPurchase.purchaseCode}
            </td>
            <td class="tit">供应商来源</td>
            <td>
                ${fns:getDictLabel(projectPurchase.supplierOrigin, 'jic_execution_origin', '')}
            </td>
            <td class="tit">采购金额</td>
            <td>
                ${projectPurchase.amount}
            </td>
        </tr>
        <tr>
            <td class="tit">供应商名称</td>
            <td >
                ${projectPurchase.supplier}
            </td>
            <td class="tit">联系人</td>
            <td class="text-center">
                ${projectPurchase.supplierPerson}
            </td>
            <td class="tit">电话</td>
            <td class="text-center">
                ${projectPurchase.supplierPhone}
            </td>
        </tr>

        <tr>
            <td class="tit">采购金额偏差说明</td>
            <td class="tit_content" colspan="5">
                ${projectPurchase.amountInfo}
            </td>
        </tr>

        <tr>
            <td  class="tit"  >付款条件偏差说明</td>
            <td  class="tit_content" colspan="5">
                ${projectPurchase.paymentInfo}
            </td>
        </tr>

        <tr>
            <td  class="tit"  >产品配置清单偏差说明</td>
            <td  class="tit_content" colspan="5">
                ${projectPurchase.inventoryInfo}
            </td>
        </tr>

        <tr>
            <td  class="tit"  >保修条款偏差说明</td>
            <td  class="tit_content" colspan="5">
                ${projectPurchase.warrantyInfo}
            </td>
        </tr>

        <tr>
            <td  class="tit"  >交货日期偏差说明</td>
            <td  class="tit_content" colspan="5">
                ${projectPurchase.deliveryInfo}
            </td>
        </tr>

        <tr>
            <td class="tit" >文件附件</td>
            <td   colspan="5" >
                <form:hidden id="attachment" path="attachment" maxlength="20000"  />
                <sys:ckfinder input="attachment" type="files" uploadPath="/project/purchase" selectMultiple="true" readonly="true"/>
            </td>
        </tr>

            <c:if test="${not empty projectPurchase.act.taskId && projectPurchase.act.status != 'finish'}">
                <tr>
                    <td class="tit">您的意见</td>
                    <td colspan="5">
                        <form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
                        <span class="help-inline"><font color="red">*</font></span>
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

    <div class="form-actions">
        <shiro:hasPermission name="project:purchase:edit">

        <c:if test="${not empty projectPurchase.act.taskId && projectPurchase.act.status != 'finish'}">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
        <input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
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