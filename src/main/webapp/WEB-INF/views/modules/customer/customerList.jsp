<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户联系人管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnImport").click(function(){
		      $.jBox($("#importBox").html(), {
		     title:"导入数据",
		     buttons:{"关闭":true},
		     bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
		    });
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<div id="importBox"class="hide">
		<form id="importForm" action="${ctx}/customer/customer/import"
		          method="post" enctype="multipart/form-data"
		
		          class="form-search" style="padding-left:20px;
		          text-align:center;" onsubmit="loading('正在导入，请稍等...');">
		     <br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		
		     <input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/> <a href="${ctx}/customer/customer/template">下载模板</a>
	     </form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/customer/customer/">客户列表</a></li>
		<shiro:hasPermission name="customer:customer:edit"><li><a href="${ctx}/customer/customer/form">客户添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="customer" action="${ctx}/customer/customer/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>客户名称：</label>
				<form:input path="customerName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>行业：</label>
				<form:select path="industry" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('customer_industry')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<shiro:hasPermission name="customer:customer:edit">
					<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
				</shiro:hasPermission>
				
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>客户名称</th>
				<th>行业</th>
				<th>客户地点</th>
				<th>负责人</th>
				<th>创建者</th>
				<th>创建时间</th>
				<shiro:hasPermission name="customer:customer:edit,customer:contact:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="customer">
			<tr>
				<td>
					<shiro:hasPermission name="customer:customer:edit">
						<a href="${ctx}/customer/customer/form?id=${customer.id}">
							${customer.customerName}
						</a>
					</shiro:hasPermission>
					<shiro:lacksPermission name="customer:customer:edit">
	    				<a href="${ctx}/customer/customer/form4contact?id=${customer.id}">${customer.customerName}</a>
					</shiro:lacksPermission>
				</td>
				<td>
					${fns:getDictLabel(customer.industry, 'customer_industry', '')}
				</td>
				<td>
					${customer.address}
				</td>
				<td>
					${customer.principal.name}
				</td>
				<td>
					${customer.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${customer.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<shiro:hasPermission name="customer:customer:edit">
	    				<a href="${ctx}/customer/customer/form?id=${customer.id}">修改</a>
						<a href="${ctx}/customer/customer/delete?id=${customer.id}" onclick="return confirmx('确认要删除该客户联系人吗？', this.href)">删除</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="customer:contact:edit">
	    				<a href="${ctx}/customer/customer/form4contact?id=${customer.id}">添加联系人</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>