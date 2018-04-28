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

        function changeProject(projectId, idx) {
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {
                    $("#project_code").text(apply.projectCode);
                    $("#project_saler").text(apply.saler.name);
                    $("#customer_name").text(apply.customer.customerName);
                    $("#apply_profit_margin").val(apply.estimatedGrossProfitMargin);
                    changeFun();
                });
        }

	</script>
	<style type="text/css">
		.tit_content{
			text-align:center
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:choose>
			<c:when test="${ empty projectBidding.act.taskId}">
				<li><a href="${ctx}/project/bidding/projectBidding/">项目投标列表</a></li>
				<li class="active"><a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">项目投标<shiro:hasPermission name="project:bidding:projectBidding:edit">${not empty projectBidding.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li>
   			</c:when>
   			<c:otherwise> <!-- 我的任务 -->
   				<li class="active"><a>项目投标<shiro:hasPermission name="project:bidding:projectBidding:edit">${not empty projectBidding.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li>
   			</c:otherwise>
  		</c:choose>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="projectBidding" action="${ctx}/project/bidding/projectBidding/save" method="post" htmlEscape="false" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="act.taskId"/>
		<form:hidden path="act.taskName"/>
		<form:hidden path="act.taskDefKey"/>
		<form:hidden path="act.procInsId"/>
		<form:hidden path="act.procDefId"/>
		<form:hidden id="flag" path="act.flag"/>
		<sys:message content="${message}"/>	
		<table class="table-form">
			<!-- 共6列 -->
			<tr>
				<td  class="tit">项目名称</td>
				<td class="" >
					<div style="white-space:nowrap;">

						<sys:treeselect
								id="apply"
								name="apply.id"
								value="${projectBidding.apply.id}"
								labelName="apply.projectName"
								labelValue="${projectBidding.apply.projectName}"
								title="项目"
								url="/apply/external/projectApplyExternal/treeData?proMainStage=10"
								cssClass="required"
								cssStyle="width: 250px;"
								dataMsgRequired="项目必选"
								allowClear="true"
								notAllowSelectParent="true"
								customClick="changeProject"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</td>
				<td  class="tit">项目编号</td>
				<td class="tit_content" >
					<input type="hidden"  id="apply_profit_margin" value="${projectBidding.apply.estimatedGrossProfitMargin }"/>
					<label id="project_code">${projectBidding.apply.projectCode }</label>
				</td>
				<td  class="tit">项目销售</td>
				<td class="tit_content" >
					<label id="project_saler">${projectBidding.apply.saler.name }</label>
				</td>
			</tr>
			<tr>
				<td  class="tit">客户名称</td>
				<td class="tit_content" colspan="2">
					<label id="customer_name">${projectBidding.apply.customer.customerName }</label>
				</td>
				<%--<td colspan="1" class="tit">行业</td>--%>
				<%--<td class="tit_content" colspan="1">--%>
					<%--<label id="customer_industry" style="width:70px">${fns:getDictLabel(projectBidding.apply.customer.industry , 'customer_industry', '')}</label>--%>
				<%--</td>--%>

			</tr>
			<tr>
				<td colspan="1" class="tit">招标方(若无第三方可不填)</td>
				<td class="" colspan="3">
					<form:input path="tenderer" maxlength="500" class="input-xlarge "/>
				</td>

				<td colspan="1" class="tit">是否有外包</td>
				<td colspan="1" class="">
                    <form:select path="outsourcing">
					    <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				    </form:select>
				<span class="help-inline"><font color="red">*</font></span>
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">标书种类</td>
				<td class="" colspan="1">
					<form:checkboxes path="categoryList" items="${fns:getDictList('tender_category')}" itemLabel="label" itemValue="value" htmlEscape="false" class=""/>
				</td>
				
				<td colspan="1" class="tit">标书购买价</td>
				<td class="" colspan="1">
					<form:input path="price" style="width:122px" htmlEscape="false" maxlength="64" class="input-xlarge number"/>
				</td>

				<td colspan="1" class="tit">用印内容</td>
				<td class="" colspan="1">
					<form:checkboxes path="printingPasteList" items="${fns:getDictList('tender_printing_paste')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</td>

				<%--<td colspan="1" class="tit">投标结果</td>--%>
				<%--<td colspan="1" class="">--%>
					<%--<form:select path="outsourcing">--%>
						<%--<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
					<%--</form:select>--%>
					<%--<span class="help-inline"><font color="red">*</font></span>--%>
				<%--</td>--%>

			</tr>
			<tr>
				<td colspan="1" class="tit">技术负责人</td>
				<td class="" colspan="5">
					<sys:treeselect id="projectManager" name="projectManager.id"
						value="${projectBidding.projectManager.id}"
						labelName="projectManager.name" labelValue="${projectBidding.projectManager.name}"
						title="技术负责人" url="/sys/office/treeData?type=3&isAll=true"
						cssClass="required"  allowClear="true" notAllowSelectParent="true" />
				</td>
			</tr>

			<tr>
                <td colspan="1" class="tit">投标金额(元)</td>
                <td class="" >
                    <form:input path="amount"  number="true" class="checkNum required"/>
                    <span class="help-inline"><font color="red">*</font> </span>
                </td>

                <td colspan="1" class="tit">投标毛利(元)</td>
                <td class="" >
                    <form:input path="grossMargin"  number="true" class="checkNum required"/>
                    <span class="help-inline"><font color="red">*</font> </span>
                </td>

				<td colspan="1" class="tit">毛利率％</td>
				<td class="" >
					<form:input path="profitMargin"  number="true" class="checkNum required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">投标内容与立项内容<br/>偏差说明</td>
				<td class="" colspan="5">
					<div style="white-space:nowrap;">
						<form:textarea path="content" style="width:98%" rows="4" maxlength="2000" class="input-xxlarge "/>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">毛利分析表附件</td>
				<td class="" colspan="5">
					<form:hidden id="profitMarginFile" path="profitMarginFile" maxlength="2000" class="input-xlarge required"/>
					<sys:ckfinder input="profitMarginFile" type="files" uploadPath="/project/bidding/projectBidding" selectMultiple="true" />
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
			</tr>
			<tr>
				<td  class="tit" colspan="6">填表说明</td>
			</tr>
			<tr>
				<td colspan="6">
				<div >
                    1、销售人员发起投标审批，必须将《项目收入成本预测表》作为附件，否则不予批准；<br>
                    2、售前工程师、解决方案部负责人、服务交付部或软件开发部负责人、技术部门分管领导负责逐级确认投标书中的技术部分，包括产品配置、技术方案、技术偏离表、技术承诺等内容；<br>
                    3、商务部负责人对《项目收入成本预测表》中的产品采购、工程外包等内容进行确认；<br>
                    4、人力资源部负责对《项目收入成本预测表》中涉及人员工时、人员外包等内容进行确认；<br>
                    5、项目销售、业务部负责人、业务部门分管领导负责逐级确认投标报价以及商务承诺等内容；<br>
                    6、项目管理部和法务对项目中的风险和法律内容进行监控；<br>
                    7、 项目投标审批完成后，由项目管理部专人负责打印本表及附件《项目收入成本预测表》存档。
                </div>
				</td>
			</tr>
			
		</table>	
		
		<div class="form-actions">
			<shiro:hasPermission name="project:bidding:projectBidding:edit">
			
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary cancel" type="submit" value="暂存" onclick="$('#flag').val('saveOnly')"/>&nbsp;&nbsp;
				<c:if test="${not empty projectBidding.id}">
					<input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;&nbsp;
				</c:if>
			</shiro:hasPermission>

			<shiro:hasPermission name="apply:external:projectApplyExternal:super">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')"/>&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')"/>&nbsp;&nbsp;
			</shiro:hasPermission>

			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
		</div>
		
		<c:if test="${not empty projectBidding.id}">
			<act:histoicFlow procInsId="${projectBidding.procInsId}" />
		</c:if>
		
	</form:form>
</body>
</html>