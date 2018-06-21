<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>项目投标管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			changeFun  =function(){
				if($("#applyId").val() ==''){
					alert("请选择项目名称");
					return false;
				}
				//若毛利率低于公司的设置百分比或投标内容与立项内容偏差较大，弹出提示，投标内容与立项内容偏差说明必填
				if(parseFloat($("#profitMargin").val())< ${fns:getDictLabel("key", 'estimatedGrossProfitMargin', '5')}||Math.abs(parseFloat(this.value)-parseFloat($("#apply_profit_margin"))) > ${fns:getDictLabel('key', 'bidding_profitMargin', '0')}){
					alert("预计毛利率低于公司要求 ${fns:getDictLabel("key", 'estimatedGrossProfitMargin', '5')}(%)或投标内容与立项内容偏差较大(公司毛利偏差为${fns:getDictLabel('key', 'bidding_profitMargin', '0')})，请填写投标内容与立项内容偏差说明");
					$("#content").after("<span class='help-inline'><font color='red'>*</font> </span>");
					$("#content").addClass('required');
				}
				else{
					$("#content").nextAll().remove();
					$("#content").removeClass('required');
				}
			}
			$("#profitMargin").change(function(){
				changeFun();
			});
			
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
		});

        // 选择项目后触发事件
        function changeProject(projectId, idx) {
            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {
                    $("#project_code").text(apply.projectCode);
                    $("#project_saler").text(apply.saler.name);
                    $("#customer_name").text(apply.customer.customerName);
                    $("#apply_profit_margin").val(apply.estimatedGrossProfitMargin);

                    changeFun();//目的是触发判断 毛利偏差值
                });
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
<c:choose>
	<c:when test="${ empty projectBidding.act.taskId}">
		<li><a href="${ctx}/project/bidding/projectBidding/">项目投标列表</a></li>
		<li class="active"><a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">项目投标
			<shiro:hasPermission name="project:bidding:projectBidding:edit">
				${not empty projectBidding.id?'查看':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li>
	</c:when>
	<c:otherwise> <!-- 我的任务 -->
		<li class="active"><a>项目投标<shiro:hasPermission name="project:bidding:projectBidding:edit">
			${not empty projectBidding.id?'审批':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li>
	</c:otherwise>
</c:choose>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectBidding" htmlEscape="false"
		   action="${ctx}/project/bidding/projectBidding/saveAudit" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="act.taskId"/>
	<form:hidden path="act.taskName"/>
	<form:hidden path="act.taskDefKey"/>
	<form:hidden path="act.procInsId"/>
	<form:hidden path="act.procDefId"/>
	<form:hidden id="flag" path="act.flag"/>
	<sys:message content="${message}"/>
	<%-- 后台数据放到前端，供前端js运算处理 --%>
	<form:hidden id="apply_profit" path="apply.estimatedGrossProfitMargin" />
	<%-- 这两种写法效果一样，注意写法上的区别 --%>
	<input type="hidden"  id="apply_profit_margin" value="${projectBidding.apply.estimatedGrossProfitMargin }"/>
	<table class="table-form">
		<!-- 共6列 -->
		<tr>
			<td  class="tit">项目名称</td>
			<td class="" >${projectBidding.apply.projectName}</td>
			<td  class="tit">项目编号</td>
			<td class="" >${projectBidding.apply.projectCode }</td>
			<td  class="tit">项目销售</td>
			<td class="" ><label id="project_saler">${projectBidding.apply.saler.name }</label></td>
		</tr>

		<tr>
			<td  class="tit">客户名称</td>
			<td class="" colspan="5"><label id="customer_name">${projectBidding.apply.customer.customerName }</label></td>
			<%--<td colspan="1" class="tit">行业</td>--%>
			<%--<td class="tit_content" colspan="1">--%>
				<%--<label id="customer_industry" style="width:70px">${fns:getDictLabel(projectBidding.apply.customer.industry , 'customer_industry', '')}</label>--%>
			<%--</td>--%>
		</tr>

		<tr>
			<td colspan="1" class="tit">招标方(若无第三方可不填)</td>
			<td class="" colspan="3">${projectBidding.tenderer }</td>

			<td colspan="1" class="tit">是否有外包</td>
			<td colspan="1" class="">${fns:getDictLabel(projectBidding.outsourcing, 'yes_no', '否')}</td>
		</tr>

		<tr>
			<td colspan="1" class="tit">标书种类</td>
			<td class="" colspan="1">
					${fns:getDictLabels(projectBidding.category, 'tender_category', '')}
				<%--<form:checkboxes path="categoryList" items="${fns:getDictList('tender_category')}" itemLabel="label" itemValue="value"/>--%>
			</td>

			<td colspan="1" class="tit">标书购买价</td>
			<td class="" colspan="1">${projectBidding.price }</td>

			<%--<td colspan="1" class="tit">投标结果</td>--%>
			<%--<td colspan="1" class="">--%>
				<%--<form:select path="outsourcing">--%>
					<%--<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
				<%--</form:select>--%>
				<%--<span class="help-inline"><font color="red">*</font></span>--%>
			<%--</td>--%>
		</tr>

		<tr>
			<td colspan="1" class="tit">用印内容</td>
			<td class="" colspan="5">
				${fns:getDictLabels(projectBidding.printingPaste, 'tender_printing_paste', '')}
				<%--<form:checkboxes path="printingPasteList" items="${fns:getDictList('tender_printing_paste')}" itemLabel="label" itemValue="value" />--%>
			</td>
		</tr>

		<tr>
			<td colspan="1" class="tit">投标金额(元)</td>
			<td class="" >${projectBidding.amount }</td>

			<td colspan="1" class="tit">投标毛利(元)</td>
			<td class="" >${projectBidding.grossMargin }</td>

			<td colspan="1" class="tit">毛利率％</td>
			<td class="" >${projectBidding.profitMargin }</td>
		</tr>

		<tr>
			<td colspan="1" class="tit">投标内容与立项内容<br/>偏差说明</td>
			<td class="" colspan="5">${projectBidding.content }</td>
		</tr>

		<tr>
			<td colspan="1" class="tit">毛利分析表附件</td>
			<td class="" colspan="5">
				<form:hidden id="profitMarginFile" path="profitMarginFile" maxlength="2000" class="input-medium required"/>
				<sys:ckfinder input="profitMarginFile" type="files" uploadPath="/project/bidding/projectBidding"
					selectMultiple="true" readonly="true"/>
			</td>
		</tr>

		<c:if test="${not empty projectBidding.act.taskId && projectBidding.act.status != 'finish'}">
			<tr>
				<td class="tit">您的意见</td>
				<td colspan="5">
					<form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
					<span class="help-inline"><font color="red">*</font></span>
				</td>
			</tr>
		</c:if>

		<%--<tr>--%>
			<%--<td  class="tit" colspan="6">填表说明</td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%--<td colspan="6">--%>
			<%--<div >--%>
				<%--1、销售人员发起投标审批，必须将《项目收入成本预测表》作为附件，否则不予批准；<br>--%>
				<%--2、售前工程师、解决方案部负责人、服务交付部或软件开发部负责人、技术部门分管领导负责逐级确认投标书中的技术部分，包括产品配置、技术方案、技术偏离表、技术承诺等内容；<br>--%>
				<%--3、商务部负责人对《项目收入成本预测表》中的产品采购、工程外包等内容进行确认；<br>--%>
				<%--4、人力资源部负责对《项目收入成本预测表》中涉及人员工时、人员外包等内容进行确认；<br>--%>
				<%--5、项目销售、业务部负责人、业务部门分管领导负责逐级确认投标报价以及商务承诺等内容；<br>--%>
				<%--6、项目管理部和法务对项目中的风险和法律内容进行监控；<br>--%>
				<%--7、 项目投标审批完成后，由项目管理部专人负责打印本表及附件《项目收入成本预测表》存档。--%>
			<%--</div>--%>
			<%--</td>--%>
		<%--</tr>--%>
	</table>

	<div class="form-actions">
		<shiro:hasPermission name="project:bidding:projectBidding:edit">
		<c:if test="${not empty projectBidding.act.taskId && projectBidding.act.status != 'finish'}">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-warning" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;&nbsp;&nbsp;&nbsp;
		</c:if>
		</shiro:hasPermission>

		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
	</div>

	<c:if test="${not empty projectBidding.id}">
		<act:histoicFlow procInsId="${projectBidding.procInsId}" />
	</c:if>
</form:form>
</body>
</html>