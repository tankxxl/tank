<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>条线管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, rootId = "0";
			addRow("#treeTableList", tpl, data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 5});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if ((${fns:jsGetVal('row.parentId')}) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {
							type: getDictLabel(${fns:toJson(fns:getDictList('sys_area_type'))}, row.type)
						}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/project/line/">条线列表</a></li>
		<shiro:hasPermission name="sys:area:edit">
			<li><a href="${ctx}/project/line/form">条线添加</a></li>
		</shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th>条线名称</th>
			<th>条线编码</th>
			<%--<th>区域类型</th>--%>
			<th>备注</th>
			<shiro:hasPermission name="sys:area:edit"><th>操作</th></shiro:hasPermission>
		</tr></thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/project/line/form?id={{row.id}}">{{row.name}}</a></td>
			<td>{{row.code}}</td>
			<%--<td>{{dict.type}}</td>--%>
			<td>{{row.remarks}}</td>
			<shiro:hasPermission name="sys:area:edit"><td>
				<a href="${ctx}/project/line/form?id={{row.id}}">修改</a>
				<a href="${ctx}/project/line/delete?id={{row.id}}" onclick="return confirmx('要删除该条线及所有子条线吗？', this.href)">删除</a>
				<a href="${ctx}/project/line/form?parent.id={{row.id}}">添加子条线</a>
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>