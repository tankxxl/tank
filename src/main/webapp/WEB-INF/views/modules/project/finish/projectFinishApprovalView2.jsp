<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>结项审批管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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
	</script>
	<style type="text/css">
		.tit_content{
			text-align:center
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
	<c:if test="${ empty projectFinishApproval.act.taskId}">
		<li><a href="${ctx}/project/finish/projectFinishApproval/">结项审批列表</a></li>
	</c:if>
		<%-- <li class="active"><a href="${ctx}/project/finish/projectFinishApproval/form?id=${projectFinishApproval.id}">结项审批<shiro:hasPermission name="project:finish:projectFinishApproval:edit">${not empty projectFinishApproval.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="project:finish:projectFinishApproval:edit">查看</shiro:lacksPermission></a></li> --%>
		<li class="active"><a>结项审批查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="projectFinishApproval" action="${ctx}/project/finish/projectFinishApproval/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		
		<table class="table-form">
			<tr>
				<th colspan="6" class="tit">项目信息</th>
			</tr>
			<tr>
				<td class="tit">项目名称</td>
				<td colspan="1" >
						<div style="white-space:nowrap;" >
						<sys:treeselect id="apply" name="apply.id"  value="${projectFinishApproval.apply.id}" labelName="apply.projectName" labelValue="${projectFinishApproval.apply.projectName}"
							title="立项名称" url="/apply/external/projectApplyExternal/treeData" cssClass="required"  allowClear="true" notAllowSelectParent="true"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
					<script type="text/javascript">
					
					$("#applyName").attr("style","width:350px");
					$("#applyButton, #applyName").unbind('click');
					$("#applyButton, #applyName").click(function(){
						// 是否限制选择，如果限制，设置为disabled
						if ($("#applyButton").hasClass("disabled")){
							return true;
						}
						// 正常打开	${ctx}/tag/treeselect   /jeesite/a/tag/treeselect
						top.$.jBox.open("iframe:${ctx}/tag/treeselect?url="+encodeURIComponent("/apply/external/projectApplyExternal/treeData")+"&proMainStage=03&module=&checked=&extId=&isAll=", "选择立项名称", 300, 420, {
							ajaxData:{selectIds: $("#applyId").val()},buttons:{"确定":"ok", "清除":"clear", "关闭":true}, submit:function(v, h, f){
								if (v=="ok"){
									var tree = h.find("iframe")[0].contentWindow.tree;//h.find("iframe").contents();
									var ids = [], names = [], nodes = [];
									if ("" == "true"){
										nodes = tree.getCheckedNodes(true);
									}else{
										nodes = tree.getSelectedNodes();
									}
									for(var i=0; i<nodes.length; i++) {//
										if (nodes[i].isParent){
											top.$.jBox.tip("不能选择父节点（"+nodes[i].name+"）请重新选择。");
											return false;
										}//
										ids.push(nodes[i].id);
										names.push(nodes[i].name);//
										break; // 如果为非复选框选择，则返回第一个选择  
									}
									$("#applyId").val(ids.join(",").replace(/u_/ig,""));
									$("#applyName").val(names.join(","));
									
									
									var url ="${ctx }/apply/external/projectApplyExternal/proApply4finish?id="+ids.join(",").replace(/u_/ig,"");
								    $.ajax( {  
								        type : "get",  
								        url : url,  
								        dataType:"json",
								        success : function(apply) {
								            $("#project_code").text(apply.projectCode);
								            $("#customer_name").text(apply.customerName);
								    		$("#saler_name").text(apply.salerName);
								    		$("#saler_office_name").text(apply.salerOfficeName);
								        }  
								    });
								}//
								else if (v=="clear"){
									$("#applyId").val("");
									$("#applyName").val("");
				                }//
								if(typeof applyTreeselectCallBack == 'function'){
									applyTreeselectCallBack(v, h, f);
								}
							}, loaded:function(h){
								$(".jbox-content", top.document).css("overflow-y","hidden");
							}
						});
					});

					</script>
				</td>
				
				<td class="tit">项目编码</td>
				<td tit_content><label id="project_code" >${projectFinishApproval.apply.projectCode }</label></td>
			</tr>
		
			<tr>
				<td class="tit">销售名称</td>
				<td class="tit_content"><label id="saler_name">${projectFinishApproval.apply.saler.name }</label></td>
				<td class="tit">客户联系人</td>
				<td class="tit_content"><label id="saler_office_name">${projectFinishApproval.apply.saler.office.name }</label></td>
			</tr>
			<tr>
				<td class="tit">客户名称</td>
				<td class="tit_content"><label id="customer_name">${projectFinishApproval.apply.customer.customerName }</label></td>
			</tr>
			<tr>
				<td class="tit">结项种类</td>
				<td class="tit_content">
					<form:checkboxes path="category" items="${fns:getDictList('jic_pro_finish_type')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
				<td class="tit">风险评估</td>
				<td class="tit_content">
					<div style="white-space:nowrap;">
						<form:input path="riskAssessment" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</td>
			</tr>
			<tr>
				<td class="tit">项目核算</td>
				<td class="tit_content">
					<form:input path="projectAccounting" htmlEscape="false" maxlength="255" class="input-xlarge "/>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
			</tr>
			<tr>
				<td colspan="1" class="tit">项目核算表附件</td>
				<td class="" colspan="5">
					<form:hidden id="projectAccountingFile" path="projectAccountingFile" htmlEscape="false" maxlength="2000" class="input-xlarge required"/>
					<sys:ckfinder input="projectAccountingFile" type="files" uploadPath="/project/finish/projectFinishApproval" selectMultiple="true"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</td>
			</tr>
			
		</table>
		<%-- <div class="form-actions">
			<shiro:hasPermission name="project:finish:projectFinishApproval:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> --%>
		<act:histoicFlow procInsId="${projectFinishApproval.procInsId}" />
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>