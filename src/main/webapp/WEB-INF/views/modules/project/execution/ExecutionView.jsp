<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>合同执行</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
    $(document).ready(function() {
        // 初始化全局变量，修改表单使用
        treeGetParam = "?prjId=${projectExecution.apply.id}";

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

        if ($("#contractCode").text() != "") {
            $("#executionInfo").hide();
        }
    });
    //
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
</script>
<style type="text/css">
.tit_content {
	text-align: center
}
</style>
</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${ empty projectExecution.act.taskId}">
        <li><a href="${ctx}/project/execution/">合同执行列表</a></li>
    </c:if>
    <li class="active"><a href="${ctx}/project/execution/form?id=${projectExecution.id}">合同执行
    <shiro:hasPermission name="project:execution:edit">
        ${not empty projectExecution.act.taskId?'审批':'查看'}
    </shiro:hasPermission>
    <shiro:lacksPermission name="project:execution:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br />
<form:form id="inputForm" modelAttribute="projectExecution" htmlEscape="false"
           action="${ctx}/project/execution/saveAudit" method="post" class="form-horizontal">
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
		<table class="table table-striped table-bordered table-condensed">
			<tr>
				<th colspan="6" class="tit">项目信息</th>
			</tr>
			<tr>
				<td class="tit text-center">项目名称</td>
				<td colspan="1" class="tit_content text-left">${projectExecution.apply.projectName}</td>
				<td class="tit">项目编码</td>
				<td class=" text-left">
                    <label id="project_code">${projectExecution.apply.projectCode }</label></td>
				<td class="tit">项目类型</td>
				<td class="tit_content">
                    <label id="project_category">${fns:getDictLabel(projectExecution.apply.category , 'pro_category', '')}</label></td>
			</tr>

			<tr>
				<td class="tit">客户名称</td>
				<td class="tit_content">
                    <label id="customer_name">${projectExecution.apply.customer.customerName }</label></td>
				<td class="tit">客户联系人</td>
				<td class="tit_content">
                    <label id="customer_contact_name">${projectExecution.apply.customerContact.contactName }</label></td>
				<td class="tit">客户电话</td>
				<td class="tit_content">
                    <label id="customer_phone">${projectExecution.apply.customerContact.phone }</label></td>
			</tr>

            <tr>
                <td class="tit">合同号</td>
                <td class="tit_content">
                    <label id="contractCode">${projectExecution.contractItem.contractCode}</label></td>
                <td class="tit">合同金额</td>
                <td class="tit_content">
                    <label>${projectExecution.contractItem.contractAmount}</label></td>
                <td class="tit">合同毛利率</td>
                <td class="tit_content">
                    <label>${projectExecution.contractItem.grossProfitMargin}</label></td>
            </tr>

            <tr id="executionInfo">
                <%--<td class="tit">执行合同号</td>--%>
                <%--<td class="tit_content">--%>
                <%--<c:choose>--%>
                <%--<c:when test="${projectExecution.act.taskDefKey eq 'usertask_commerce_leader'}">--%>
                    <%--<form:input path="executionContractNo" style="width:88%" htmlEscape="false" maxlength="50" class="input-medium required"/>--%>
                <%--</c:when>--%>
                <%--<c:otherwise>--%>
                    <%--<label>${projectExecution.executionContractNo}</label>--%>
                <%--</c:otherwise>--%>
                <%--</c:choose>--%>
                <%--</td>--%>
                <td class="tit" colspan="2"></td>
                <td class="tit">执行金额</td>
                <td class="tit_content">
                    <label>${projectExecution.amount}</label>
                </td>
                <td class="tit">执行毛利率</td>
                <td class="tit_content">
                    <label>${projectExecution.grossMargin}</label>
                </td>
            </tr>

            <tr>
                <td class="tit"  >付款条件</td>
                <td class="tit_content" colspan="5">
                    ${projectExecution.paymentTerm}
                </td>
            </tr>

            <tr>
                <td  class="tit" >合同执行依据</td>
                <td class="tit_content" colspan="5">
                    ${fns:getDictLabels(projectExecution.executionBasis, 'jic_execution_basis', '')}
                </td>
            </tr>

            <tr>
                <td class="tit">交货地址</td>
                <td class="tit_content">
                    <label>${projectExecution.deliveryAddress}</label>
                </td>
                <td class="tit">交货联系人</td>
                <td class="tit_content">
                    <label>${projectExecution.deliveryPerson}</label>
                </td>
                <td class="tit">联系电话</td>
                <td class="tit_content">
                    <label>${projectExecution.deliveryPhone}</label>
                </td>
            </tr>
			
			<tr>
				<td class="tit" >文件附件</td>
				<td   colspan="5">
					<form:hidden id="attachment" path="attachment" htmlEscape="false" maxlength="20000"  />
					<sys:ckfinder input="attachment" type="files" uploadPath="/project/execution" selectMultiple="true" readonly="true"/>
				</td>
			</tr>

            <c:if test="${not empty projectExecution.act.taskId && projectExecution.act.status != 'finish'}">
                <tr>
                    <td class="tit">您的意见</td>
                    <td colspan="5">
                        <form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
                        <span class="help-inline"><font color="red">*</font></span>
                    </td>
                </tr>
            </c:if>
			
		</table>
		<br />
		<br />

        <table id="contentTable" class="table table-striped table-bordered table-condensed table-hover">
            <%--<thead>--%>
            <%--<tr><td class="tit">产品配置清单</td> <tr>--%>
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
                <%--<shiro:hasPermission name="project:execution:edit">--%>
                    <%--<th width="10">&nbsp;</th>--%>
                <%--</shiro:hasPermission>--%>
            </tr>
            </thead>
            <tbody id="itemList"></tbody>
        </table>

        <%--<td>{{row.supplierOrigin}}</td>--%>

        <script type="text/template" id="itemTpl">//<!--
        <tr id="executionItemList{{idx}}">
            <input id="executionItemList{{idx}}_id" name="executionItemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
            <input id="executionItemList{{idx}}_delFlag" name="executionItemList[{{idx}}].delFlag" type="hidden" value="0"/>
            <td>{{row.brand}}</td>
            <td>{{row.amount}}</td>
            <td style="width:95px" class="input-medium">{{row.deliveryDate}}</td>
            <td>{{row.warranty}}</td>
            <td>{{row.supplier}}</td>
            <td>{{row.supplierOrigin4Export}}</td>


        <c:choose>
			<c:when test="${projectExecution.act.taskDefKey eq 'usertask_commerce_specialist'}">
				<td>
                    <input id="executionItemList{{idx}}_contactPerson" name="executionItemList[{{idx}}].contactPerson" type="text" style="width:90%"
                    value="{{row.contactPerson}}" maxlength="255" class="input-small required" />
                </td>
                <td>
                    <input id="executionItemList{{idx}}_contactPhone" name="executionItemList[{{idx}}].contactPhone" style="width:90%" type="text"
                    value="{{row.contactPhone}}" maxlength="255" class="input-small required" />
                </td>
                <td>
                    <input id="executionItemList{{idx}}_paymentTerm" name="executionItemList[{{idx}}].paymentTerm" style="width:90%" type="text"
                    value="{{row.paymentTerm}}" maxlength="255" class="input-small required" />
                </td>
   			</c:when>
   			<c:otherwise>
   				<td>{{row.contactPerson}}</td>
                <td>{{row.contactPhone}}</td>
                <td>{{row.paymentTerm}}</td>
   			</c:otherwise>
  		</c:choose>
        </tr>//-->
        </script>
        <script type="text/javascript">
            var itemRowIdx = 0, itemTpl = $("#itemTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
            $(document).ready(function() {
                var data = ${fns:toJson(projectExecution.executionItemList)};
                for (var i=0; i<data.length; i++){
                    addRow('#itemList', itemRowIdx, itemTpl, data[i]);
                    itemRowIdx = itemRowIdx + 1;
                }
            }); // end ready
        </script>


<br/><br/>

<div class="form-actions">
    <shiro:hasPermission name="project:execution:edit">
        <c:if test="${not empty projectExecution.act.taskId && projectExecution.act.status != 'finish'}">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
            <input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
    </shiro:hasPermission>

    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
</div>

<c:if test="${not empty projectExecution.procInsId}">
    <act:histoicFlow procInsId="${projectExecution.procInsId}" />
</c:if>


</form:form>
</body>
</html>