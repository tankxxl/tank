<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>合同管理</title>
	<meta name="decorator" content="default"/>
	<%-- 销售、采购合同共用 --%>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
            $("#rmb").text(digitUppercase(${projectContract.amount }));

			$("#inputForm").validate({

                rules: {
                    // 合同总金额
                    amount: {
                        required: true,
                        number: true,
                        min: 0.01,
                        max: 99999999,
                        minNumber: $("#amount").val()  // 调用自定义验证
                    }
                },

                messages: {
                    ownership: "请填写",
                    estimatedGrossProfitMargin: {
                        required: "请填写毛利率",
                        minlength: jQuery.validator.format("至少要填写 {0} 位数字。")
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
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

            $("#amount").keyup(function () {
//                top.$.jBox.tip("input.value=" + digitUppercase(parseFloat(this.value)));
                $("#rmb").text(digitUppercase(parseFloat(this.value)));
            });
            

		});

        function numChanged(dom) {
            var idx = $(dom).attr("idx");
//            projectContractItemList{{idx}}_unitPrice
            var numId = "projectContractItemList" + idx + "_num";
            var unitPriceId = "projectContractItemList" + idx + "_unitPrice";
			var amountId = "projectContractItemList" + idx + "_amount";

            var num = $("#" + numId).val();
            var unitPrice = $("#" + unitPriceId).val();

//            alert("num=" + num + ", unitPrice=" + unitPrice);
            if(isNaN(num) || isNaN(unitPrice) || !num || !unitPrice){
                return ;
            }
            var amount = parseFloat(num) * parseFloat(unitPrice);
            $("#" + amountId).val(amount);
        }

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
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {
                    $("#project_code").text(apply.projectCode);
//                    $("#customer_name").text(apply.customer.customerName);
//                    $("#customer_contact_name").text(apply.customerContact.contactName);
//                    $("#customer_phone").text(apply.customerContact.phone);
//                    $("#project_category").text(apply.category);
            });
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<c:if test="${ empty projectContract.act.taskId}">
		<li><a href="${ctx}/project/contract/projectContract/">合同列表</a></li>
	</c:if>
	<li class="active"><a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}&contractType=3">
		${fns:getDictLabel(projectContract.contractType, 'jic_contract_type', '合同')}
		<shiro:hasPermission name="project:contract:projectContract:edit">
			${not empty projectContract.id?'修改':'添加'}
		</shiro:hasPermission>
		<shiro:lacksPermission name="project:contract:projectContract:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectContract" htmlEscape="false"
		   action="${ctx}/project/contract/projectContract/save" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<%-- 写到form里的参数才会传到后台，其它参数一概不管 --%>
	<form:hidden path="contractType"/>
	<form:hidden id="flag" path="act.flag"/>

	<form:hidden path="procStatus"/>

	<sys:message content="${message}"/>
	<table class="table-form">
		<%--<tr><th colspan="6" class="tit">项目信息</th></tr>--%>
		<%--<caption>项目信息</caption>--%>
		<caption>北京建投科信科技发展股份有限公司合同审批表</caption>
		<tr>
			<td class="tit">项目名称</td>
			<td >
				<sys:treeselect id="apply" name="apply.id"
					value="${projectContract.apply.id}"
					labelName="apply.projectName"
					labelValue="${projectContract.apply.projectName}"
					title="项目名称"
					cssStyle="width:80%;"
					url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11&isAll=true"
					cssClass="required"  allowClear="true" notAllowSelectParent="true"
					customClick="changeProject"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>

			<td class="tit">项目编码</td>
			<td ><label id="project_code" >${projectContract.apply.projectCode }</label></td>
		</tr>

		<tr>
			<shiro:hasPermission name="project:contract:projectContract:modify">
			<td class="tit">合同编号</td>
			<td colspan="1" class="">
				<form:input path="contractCode" style="width:90%" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</td>
			</shiro:hasPermission>

			<td class="tit">合同类型</td>
			<td colspan="1" class="">
					${fns:getDictLabel(projectContract.contractType, 'jic_contract_type', '')}
			</td>
			<c:if test="${projectContract.contractType eq '4'}">
			<td class="tit">毛利率%</td>
			<td>
				${projectContract.profitMargin}
			</td>
			</c:if>
		</tr>

		<c:if test="${not empty projectContract.createBy.name}">
		<tr>
			<td class="tit">申请部门</td>
			<td class=""><label id="customer_name">
					${projectContract.createBy.office.name}-
					${projectContract.createBy.name}
			</label></td>
			<td class="tit">申请日期</td>
			<td class=""><label id="customer_contact_name">
				<fmt:formatDate value="${projectContract.createDate}" pattern="yyyy-MM-dd"/>
			</label></td>
		</tr>
		</c:if>

		<tr>
			<td class="tit">合同名称</td>
			<td colspan="3" class="">
				<form:input path="contractName" style="width:90%"/>
			</td>
		</tr>

		<c:if test="${projectContract.contractType eq '4'}">
			<tr>
				<td class="tit">采购申请编号</td>
				<td colspan="3" class="">
					<form:input path="purchaseCode" style="width:90%"/>
				</td>
			</tr>
		</c:if>

		<tr>
			<td class="tit">
				<c:if test="${projectContract.contractType eq '3'}">
					客户名称
				</c:if>
				<c:if test="${projectContract.contractType eq '4'}">
					供应商名称
				</c:if>
			</td>

			<td colspan="3" class="">
				<form:input path="clientName" style="width:90%"/>
			</td>
		</tr>

		<tr>
			<td class="tit">合同总金额</td>
			<td colspan="1" class="">
				<div class="input-append">
					<form:input path="amount" style="width:122px" maxlength="10" number="true" min="0" max="99999999" class="checkNum input-medium"/><span class="add-on">元</span>
				</div>
			</td>
			<td class="tit">大写</td>
			<td colspan="1" class=""><label id="rmb">${projectContract.apply.customer.customerName }</label></td>
		</tr>

		<tr>
			<td class="tit">合同有效期</td>
			<td colspan="1" class="">
				<input name="beginDate" type="text" readonly="readonly" class="input-medium Wdate"
					   value="<fmt:formatDate value="${projectContract.beginDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</td>
			<td class="tit">至</td>
			<td colspan="1" class="">
				<input name="endDate" type="text" readonly="readonly" class="input-medium Wdate"
					   value="<fmt:formatDate value="${projectContract.endDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</td>
		</tr>

		<c:if test="${projectContract.contractType eq '3'}">
		<tr>
			<td  class="tit" >交货时间及地点</td>
			<td  colspan="3"><form:input path="deliveryAddress" style="width:90%"/></td>
		</tr>
		</c:if>

		<tr>
			<td  class="tit" >付款方式</td>
			<td  colspan="3"><form:input path="paymentType" style="width:90%"/></td>
		</tr>

		<tr>
			<td  class="tit" >印章类型</td>
			<td  colspan="1">
				<form:checkboxes path="sealType" items="${fns:getDictList('jic_seal_type')}" itemLabel="label" itemValue="value" />
			</td>
		</tr>

		<tr>
			<td class="tit" >文件附件</td>
			<td  colspan="3">
				<form:hidden id="attachment" path="attachment" maxlength="20000"  />
				<sys:ckfinder input="attachment" type="files" uploadPath="/project/contract/projectContract"
							  selectMultiple="true" />
			</td>
		</tr>

	</table>
	<br/><br/>
	<div class="">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><td class="tit" colspan="5">合同标的物明细</td><td colspan="6" class="tit"></td> <tr>
				<tr>
					<th>货物/商品名称</th>
					<th>数量</th>
					<th>单价(元)</th>
					<th>金额(元)</th>
					<th>维保时间</th>
					<shiro:hasPermission name="project:contract:projectContract:edit"><th width="10">&nbsp;</th></shiro:hasPermission>
				</tr>
			</thead>
			<tbody id="projectContractItemList">
			</tbody>
			<shiro:hasPermission name="project:contract:projectContract:edit"><tfoot>
				<tr>
					<td colspan="6">
						<a href="javascript:" onclick="addRow('#projectContractItemList', projectContractItemRowIdx, projectContractItemTpl); projectContractItemRowIdx = projectContractItemRowIdx + 1;"
						   class="btn">新增</a>
					</td>
				</tr>
			</tfoot></shiro:hasPermission>
		</table>
		<script type="text/template" id="projectContractItemTpl">//<!--
			<tr id="projectContractItemList{{idx}}">
					<input id="projectContractItemList{{idx}}_id" name="projectContractItemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
					<input id="projectContractItemList{{idx}}_delFlag" name="projectContractItemList[{{idx}}].delFlag" type="hidden" value="0"/>
				<td>
					<input id="projectContractItemList{{idx}}_goodsName" style="width:90%" name="projectContractItemList[{{idx}}].goodsName" type="text" value="{{row.goodsName}}" maxlength="64" class="input-small"/>
				</td>
				<td>
					<input id="projectContractItemList{{idx}}_num" idx="{{idx}}" onkeyup="numChanged(this);" style="width:90%" name="projectContractItemList[{{idx}}].num" type="text" value="{{row.num}}" class="checkNum input-small required"/>
				</td>

				<td>
					<input id="projectContractItemList{{idx}}_unitPrice" idx="{{idx}}" onkeyup="numChanged(this);" style="width:90%" name="projectContractItemList[{{idx}}].unitPrice" type="text" value="{{row.unitPrice}}" class="checkNum input-small required"/>
				</td>

				<td>
					<input id="projectContractItemList{{idx}}_amount" style="width:90%" name="projectContractItemList[{{idx}}].amount" type="text" value="{{row.amount}}" maxlength="255" readonly="true" class="checkNum input-small required"/>
				</td>
				<td>
					<input id="projectContractItemList{{idx}}_maintenanceDate" style="width:90%" name="projectContractItemList[{{idx}}].maintenanceDate" type="text" value="{{row.maintenanceDate}}" maxlength="255" class="input-small required"/>
				</td>

				<shiro:hasPermission name="project:contract:projectContract:edit"><td class="text-center" width="10">
					{{#delBtn}}<span class="close" onclick="delRow(this, '#projectContractItemList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
				</td></shiro:hasPermission>
			</tr>//-->
		</script>
		<script type="text/javascript">
			var projectContractItemRowIdx = 0, projectContractItemTpl = $("#projectContractItemTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			$(document).ready(function() {
				var data = ${fns:toJson(projectContract.projectContractItemList)};
                <%--console.log('${projectContract.projectContractItemList}');--%>
                <%--console.log(${fns:toJson(projectContract)});--%>
//                console.log("asdf");
//                var data = "";
				for (var i=0; i<data.length; i++){
					addRow('#projectContractItemList', projectContractItemRowIdx, projectContractItemTpl, data[i]);
					projectContractItemRowIdx = projectContractItemRowIdx + 1;
				}
				if (projectContractItemRowIdx ==0) {
					addRow('#projectContractItemList', projectContractItemRowIdx, projectContractItemTpl, data[i]);
					projectContractItemRowIdx = projectContractItemRowIdx + 1;
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
		<shiro:hasPermission name="project:contract:projectContract:edit">

			<c:if test="${projectContract.contractType eq '4'}">
			<label>需求部门审批人：</label>
			<sys:treeselect id="projectManager" name="projectManager.id"
							value="${projectContract.projectManager.id}" labelName="projectManager.name"
							labelValue="${projectContract.projectManager.name}"
							dataMsgRequired="需求部门必填" title="需求部门审批人" url="/sys/office/treeData?type=3&isAll=true"
							cssClass="required"  allowClear="true" notAllowSelectParent="true" />

		<span class="help-inline"><font color="red">*</font> </span>
			</c:if>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
			<c:if test="${not empty projectContract.id}">
				<input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
			</c:if>
		</shiro:hasPermission>

		<shiro:hasPermission name="apply:external:projectApplyExternal:super">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectContract.id}">
		<act:histoicFlow procInsId="${projectContract.procInsId}" />
	</c:if>
</form:form>
</body>
</html>