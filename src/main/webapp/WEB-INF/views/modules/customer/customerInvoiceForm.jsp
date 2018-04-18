<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票客户管理</title>
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

        // 父页面调用，用来收集dialog中的值
        function formData() {
		    return "no";
        }
	</script>
</head>
<body>
<c:if test="${customerInvoice.func ne 'view'}">
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/customer/invoice/">开票客户列表</a></li>
		<li class="active"><a href="${ctx}/customer/invoice/form?id=${customerInvoice.id}">开票客户
			<shiro:hasPermission name="customer:invoice:edit">
				${not empty customerInvoice.id?'修改':'添加'}
			</shiro:hasPermission>
			<shiro:lacksPermission name="customer:invoice:edit">查看</shiro:lacksPermission></a>
		</li>
	</ul>
</c:if>
	<br/>
	<form:form id="inputForm" modelAttribute="customerInvoice" htmlEscape="false"
			   action="${ctx}/customer/invoice/save"
			   method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">开票客户名称：</label>
			<div class="controls">
				<c:choose>
				<c:when test="${customerInvoice.func == 'view'}">
					${customerInvoice.customerName}
				</c:when>
				<c:otherwise>
					<form:input path="customerName" maxlength="64" class="input-xlarge required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">税号：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.taxNo}
					</c:when>
					<c:otherwise>
						<form:input path="taxNo" maxlength="64" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">开户行名称：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.bankName}
					</c:when>
					<c:otherwise>
						<form:input path="bankName" maxlength="64" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">开户行账号：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.bankNo}
					</c:when>
					<c:otherwise>
						<form:input path="bankNo" maxlength="64" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">地址：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.address}
					</c:when>
					<c:otherwise>
						<form:input path="address" maxlength="255" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">电话：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.phone}
					</c:when>
					<c:otherwise>
						<form:input path="phone" maxlength="20" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">归属区域:</label>
			<div class="controls">
				<sys:treeselect id="area" name="area.id" value="${customerInvoice.area.id}" labelName="area.name" labelValue="${customerInvoice.area.name}"
								title="区域" url="/sys/area/treeData" cssClass="required"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">邮寄联系人：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.mailPerson}
					</c:when>
					<c:otherwise>
						<form:input path="mailPerson" maxlength="64" class="input-xlarge "/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮寄地址：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.mailAddress}
					</c:when>
					<c:otherwise>
						<form:input path="mailAddress" maxlength="255" class="input-xlarge "/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${customerInvoice.func == 'view'}">
						${customerInvoice.remarks}
					</c:when>
					<c:otherwise>
						<form:textarea path="remarks" rows="4" maxlength="255" class="input-xxlarge "/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<c:if test="${customerInvoice.func ne 'view'}">
		<div class="form-actions">
			<shiro:hasPermission name="customer:invoice:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
		</c:if>
	</form:form>
</body>
</html>