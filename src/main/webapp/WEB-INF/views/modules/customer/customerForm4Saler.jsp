<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户联系人管理</title>
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/customer/customer/">客户联系人列表</a></li>
		<li class="active"><a href="${ctx}/customer/customer/form?id=${customer.id}">客户联系人<shiro:hasPermission name="customer:customer:edit">${not empty customer.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="customer:customer:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="customer" action="${ctx}/customer/customer/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="oldName" name="oldName" type="hidden" value="${customer.customerName}">
		<sys:message content="${message}"/>		
			<div class="control-group">
				<%--<label class="control-label">客户联系人表：</label><br/>--%>
				<%--<div class="controls">--%>
					<table id="contentTable" class="table table-striped table-bordered table-condensed">
						<thead>
							<tr>
								<th class="hide"></th>
								<th>联系人名称</th>
								<th>性别</th>
								<th>职位</th>
								<th>电话</th>
								<th>手机</th>
								<th>备注信息</th>
								<shiro:hasPermission name="customer:contact:edit"><th width="10">&nbsp;</th></shiro:hasPermission>
							</tr>
						</thead>
						<tbody id="customerContactList">
						</tbody>
						<shiro:hasPermission name="customer:contact:edit"><tfoot>
							<tr><td colspan="7"><a href="javascript:" onclick="addRow('#customerContactList', customerContactRowIdx, customerContactTpl);customerContactRowIdx = customerContactRowIdx + 1;" class="btn">新增</a></td></tr>
						</tfoot></shiro:hasPermission>
					</table>
					<script type="text/template" id="customerContactTpl">//<!--
						<tr id="customerContactList{{idx}}">
							<td class="hide">
								<input id="customerContactList{{idx}}_id" name="customerContactList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
								<input id="customerContactList{{idx}}_delFlag" name="customerContactList[{{idx}}].delFlag" type="hidden" value="0"/>
							</td>
							<td>
								<input id="customerContactList{{idx}}_contactName" name="customerContactList[{{idx}}].contactName" type="text" value="{{row.contactName}}" maxlength="64" class="input-small "/>
							</td>
							<td>
								<select id="customerContactList{{idx}}_sex" name="customerContactList[{{idx}}].sex" data-value="{{row.sex}}" class="input-small ">
									<option value=""></option>
									<c:forEach items="${fns:getDictList('sex')}" var="dict">
										<option value="${dict.value}">${dict.label}</option>
									</c:forEach>
								</select>
							</td>
							<td>
								<input id="customerContactList{{idx}}_position" name="customerContactList[{{idx}}].position" type="text" value="{{row.position}}" maxlength="64" class="input-small "/>
							</td>
							<td>
								<input id="customerContactList{{idx}}_phone" name="customerContactList[{{idx}}].phone" type="text" value="{{row.phone}}" maxlength="255" class="input-small "/>
							</td>
							<td>
								<input id="customerContactList{{idx}}_mobilePhone" name="customerContactList[{{idx}}].mobilePhone" type="text" value="{{row.mobilePhone}}" maxlength="255" class="input-small "/>
							</td>
							<td>
								<textarea id="customerContactList{{idx}}_remarks" name="customerContactList[{{idx}}].remarks" rows="2" maxlength="255" class="input-small ">{{row.remarks}}</textarea>
							</td>
							<shiro:hasPermission name="customer:contact:edit"><td class="text-center" width="10">
								{{#delBtn}}<span class="close" onclick="delRow(this, '#customerContactList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
							</td></shiro:hasPermission>
						</tr>//-->
					</script>
					<script type="text/javascript">
						var customerContactRowIdx = 0, customerContactTpl = $("#customerContactTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
						$(document).ready(function() {
							var data = ${fns:toJson(customer.customerContactList)};
							for (var i=0; i<data.length; i++){
								addRow('#customerContactList', customerContactRowIdx, customerContactTpl, data[i]);
								customerContactRowIdx = customerContactRowIdx + 1;
							}
						});
					</script>
				<%--</div>--%>
			</div>
		<div class="form-actions">
			<shiro:hasPermission name="customer:contact:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>