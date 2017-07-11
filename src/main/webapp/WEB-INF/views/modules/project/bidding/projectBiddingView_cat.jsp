<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>投标管理</title>
	<meta name="decorator" content="default"/>
	<%-- 表格分类展示 --%>
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
                    $("#code_label").text(apply.projectCode);
                    $("#saler_label").text(apply.saler.name);
                    $("#pm_label").text(apply.projectManager.name);
                    $("#office_label").text(apply.saler.office.name);
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
		<li><a href="${ctx}/project/bidding/projectBidding/">投标申请列表</a></li>
		<li class="active"><a href="${ctx}/project/bidding/projectBidding/form?id=${projectBidding.id}">项目投标
			<shiro:hasPermission name="project:bidding:projectBidding:edit">${not empty projectBidding.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li>
	</c:when>
	<c:otherwise> <!-- 我的任务 -->
		<li class="active"><a>项目投标<shiro:hasPermission name="project:bidding:projectBidding:edit">${not empty projectBidding.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:bidding:projectBidding:edit">查看</shiro:lacksPermission></a></li>
	</c:otherwise>
</c:choose>
</ul><br/>
<form:form id="inputForm" modelAttribute="projectBidding" htmlEscape="false"
		   action="${ctx}/project/bidding/projectBidding/save" method="post" class="form-horizontal">
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
			<td  class="tit" colspan="2">项目名称</td>
			<td class="" >
				${projectBidding.apply.projectName}
			</td>
			<td  class="tit">项目编号</td>
			<td  >
				<input type="hidden"  id="apply_profit_margin" value="${projectBidding.apply.estimatedGrossProfitMargin }"/>
				<label id="code_label">${projectBidding.apply.projectCode }</label>
			</td>
			<td  class="tit">项目销售</td>
			<td  >
				<label id="saler_label">${projectBidding.apply.saler.name }</label>
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">发标日期</td>
			<td  colspan="1">
				${projectBidding.issueDate}
				<%--<input name="issueDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"--%>
					   <%--value="<fmt:formatDate value="${projectBidding.issueDate}" pattern="yyyy-MM-dd"/>"--%>
					   <%--onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>--%>
				<%--<span class="help-inline"><font color="red">*</font> </span>--%>
			</td>
			<td  class="tit">招标单位</td>
			<td>
				${projectBidding.tenderee}
				<%--<form:input path="tenderee" style="width:95%" maxlength="100" class="required"/>--%>
			</td>
			<td  class="tit">投标参与人员</td>
			<td>
				${projectBidding.tenderee}
				<%--<form:input path="tenderee" style="width:95%" maxlength="100" class="required"/>--%>
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">投标日期</td>
			<td colspan="1">
				${projectBidding.issueDate}
			</td>

			<td  class="tit">项目经理</td>
			<td>
				<label id="pm_label">${projectBidding.apply.projectManager.name }</label>
			</td>

			<td  class="tit">投标部门</td>
			<td>
				<label id="office_label">${projectBidding.apply.saler.office.name }</label>
			</td>
		</tr>

		<tr>
			<td rowspan="3" class="tit">情况说明</td>
			<td  class="tit">招标方情况说明</td>
			<td colspan="5">
				${projectBidding.content}
				<%--<form:textarea path="content"--%>
							   <%--style="width:98%"  maxlength="255"--%>
							   <%--placeholder="包含但不限于招标方名称；曾合作经历等与项目相关具体情况。"/>--%>
			</td>
		</tr>

		<tr>
			<td  class="tit">评委情况说明</td>
			<td colspan="5">
				${projectBidding.content}
				<%--<form:textarea path="content"--%>
							   <%--style="width:98%"  maxlength="255"--%>
							   <%--placeholder="包含但不限于开标公司；评委/专家组人员介绍等基本情况。"/>--%>
			</td>
		</tr>

		<tr>
			<td  class="tit">参标公司情况</td>
			<td colspan="5">
					${projectBidding.content}
					<%--<form:textarea path="content"--%>
					<%--style="width:98%"  maxlength="255"--%>
					<%--placeholder="包含但不限于本次参与竞标的公司名称、实力等情况；本项目竞争分析等具体情况。"/>--%>
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">投标前期准备<br>工作内容</td>
			<td colspan="5">
				${projectBidding.content}
				<%--<form:textarea path="content"--%>
							   <%--style="width:98%"  maxlength="255"--%>
							   <%--placeholder="包含但不限于：项目新投标或续标，公司资质、授权书、报价单及标书等相关文件。"/>--%>
			</td>
		</tr>

		<tr>
			<td  class="tit" rowspan="2">投标情况</td>
			<td  class="tit">投标价格</td>
			<td>
				${projectBidding.content}
				<%--<form:input path="content" style="width:95%" maxlength="100" class="required"/>--%>
			</td>
		</tr>
		<tr>
			<td  class="tit">投标主要内容</td>
			<td colspan="3">
					${projectBidding.content}
					<%--<form:textarea path="content"--%>
					<%--style="width:98%"  maxlength="255"--%>
					<%--placeholder="包含但不限于：项目新投标或续标，公司资质、授权书、报价单及标书等相关文件。"/>--%>
			</td>
		</tr>



		<tr>
			<td  class="tit" rowspan="2">投标结果</td>
			<td class="tit">最终价格</td>
			<td>
				<form:input path="content" style="width:80%"  maxlength="5" class="checkNum"  number="true" type="text" /><span class="help-inline"><font color="red">*</font> </span>
			</td>
		</tr>

		<tr>
			<td  class="tit">修改内容</td>
			<td  colspan="6">
				<form:textarea path="content" style="width:98%" rows="4" maxlength="255" />
			</td>
		</tr>

		<tr>
			<td  class="tit" colspan="2">投标过程中的问题</td>
			<td  colspan="6">
				<form:textarea path="content" style="width:98%" rows="4" maxlength="255" />
			</td>
		</tr>

		<tr>
			<td  class="tit">开标日期</td>
			<td>2011-09-09</td>
			<td class="tit">是否中标</td>
			<td>已中标</td>
			<td class="tit">合同号</td>
			<td>1231231231</td>
		</tr>

		<tr>
			<td>丟标分析</td>
			<td  colspan="6">
				<form:textarea path="content" style="width:98%" rows="4" maxlength="255" />
			</td>
		</tr>




		<tr>
			<td colspan="1" class="tit">附件</td>
			<td class="" colspan="5">
				<form:hidden id="profitMarginFile" path="profitMarginFile" htmlEscape="false" maxlength="2000" class="input-xlarge required"/>
				<sys:ckfinder input="profitMarginFile" type="files" uploadPath="/project/bidding/projectBidding" selectMultiple="true" readonly="true"/>
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

		<tr>
			<td  class="tit" colspan="6">填表说明</td>
		</tr>
		<tr>
			<td colspan="6">
				<div >
					1、项目投标审批通过后，本表原件由市场营销中心存档。（申请部门、运营管理部、财务部均留存复印件）<br>
					2、如对项目信息有更详细的说明，可附页说明，其他文档作为附件提交。<br>
					3、若投标价格与最终报价相同，由申请部门领导、分管领导签字审批。<br>
					4、若投标价格与最终报价不相同，按投标审批栏逐级审批。<br>
					5、打印要求：投标备案表打印时需双面打印。
				</div>
			</td>
		</tr>

	</table>

	<div class="form-actions">
		<shiro:hasPermission name="project:bidding:projectBidding:edit">
			<c:if test="${not empty projectBidding.act.taskId && projectBidding.act.status != 'finish'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
				<input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
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