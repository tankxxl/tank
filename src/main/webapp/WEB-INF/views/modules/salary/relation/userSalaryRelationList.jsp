<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#saveBtn").click(function(){
				var userIds = new Array();
				var salaryIds = new Array();
				var salaryFlags = new Array();
				var $tr4Update =$("#contentTable").children("tbody").children("tr").filter("[flag='1']");
				for(var i=0;i<$tr4Update.length;i++){  
					userIds.push($($tr4Update[i]).attr("userId"));
					salaryIds.push($($tr4Update[i]).attr("salaryId"));
					salaryFlags.push($($tr4Update[i]).attr("salaryFlag"));
				}
				$.post("${ctx}/salary/relation/userSalaryRelation/save", 
						{
							"userIds" : userIds,
							"salaryIds" : salaryIds,
							"salaryFlags" : salaryFlags
						},
						function(data) {
							top.$.jBox.tip("保存成功");
							$("#searchForm").submit();
						}
				);
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/salary/relation/userSalaryRelation/list");
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/salary/relation/userSalaryRelation/list">用户列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userSalaryRelation" action="${ctx}/salary/relation/userSalaryRelation/list" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<ul class="ul-form">
			<li><label>归属公司：</label><sys:treeselect id="company" name="user.company.id" value="${userSalaryRelation.user.company.id}" labelName="user.company.name" labelValue="${userSalaryRelation.user.company.name}" 
				title="公司" url="/sys/office/treeData?type=1" cssClass="input-small" allowClear="true"/></li>
			<li><label>登录名：</label><form:input path="user.loginName" htmlEscape="false" maxlength="50" class="input-medium"/></li>
			<li class="clearfix"></li>
			<li><label>归属部门：</label><sys:treeselect id="office" name="user.office.id" value="${userSalaryRelation.user.office.id}" labelName="user.office.name" labelValue="${userSalaryRelation.user.office.name}" 
				title="部门" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/></li>
			<li><label>姓&nbsp;&nbsp;&nbsp;名：</label><form:input path="user.name" htmlEscape="false" maxlength="50" class="input-medium"/></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>归属公司</th><th>归属部门</th><th class="sort-column login_name">登录名</th><th class="sort-column name">姓名</th><th>等级</th><th>工种</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="userSalaryRelation">
			<%-- salaryFlag 是用于判断是否插入salaryFlag=true 还是修改salaryFlag=false --%>
			<tr flag="0" userId="${userSalaryRelation.user.id }" salaryFlag="${not empty userSalaryRelation.salary.id}">
				<td>${userSalaryRelation.user.company.name}</td>
				<td>${userSalaryRelation.user.office.name}</td>
				<td>${userSalaryRelation.user.loginName}</td>
				<td>${userSalaryRelation.user.name}</td>
				
				<td class="grade"><c:if test="${not empty userSalaryRelation.salary.grade}">${fns:getDictLabel(userSalaryRelation.salary.grade, 'profession_grade', '')}</c:if></td>
				<td class="profession"><c:if test="${not empty userSalaryRelation.salary.profession}">${fns:getDictLabel(userSalaryRelation.salary.profession, 'profession', '')}</c:if> </td>
				<td>
					<sys:treeselect id="salary" name="salaryId" value="${userSalaryRelation.salary.id}" labelName="payMonthly" labelValue="${userSalaryRelation.salary.payMonthly}" 
					title="工资选择" url="/salary/salaryLevel/treeDate4UserSalaryRelation" cssClass="input-small" allowClear="true"/>
				</td>
				<script type="text/javascript">
					$("#salaryButton, #salaryName").unbind('click');
					
					$("#salaryButton, #salaryName").click(function(){
						// 是否限制选择，如果限制，设置为disabled
						if ($("#salaryButton").hasClass("disabled")){
							return true;
						}
						
						var $currentTd =$($(this).parents("td")[0]);//得到当前对应的td
						console.log($currentTd);
						// 正常打开	${ctx}/tag/treeselect   /jeesite/a/tag/treeselect
						top.$.jBox.open("iframe:${ctx}/tag/treeselect?url="+encodeURIComponent("/salary/salaryLevel/treeDate4UserSalaryRelation")+"&module=&checked=&extId=&isAll=", "选择工资选择", 300, 420, {
							ajaxData:{selectIds: $("#salaryId").val()},buttons:{"确定":"ok", "清除":"clear", "关闭":true}, submit:function(v, h, f){
								if (v=="ok"){
									var tree = h.find("iframe")[0].contentWindow.tree;//h.find("iframe").contents();
									var ids = [], names = [], nodes = [];
									if ("" == "true"){
										nodes = tree.getCheckedNodes(true);
									}else{
										nodes = tree.getSelectedNodes();
									}
									for(var i=0; i<nodes.length; i++) {//
										ids.push(nodes[i].id);
										names.push(nodes[i].name);//
										break; // 如果为非复选框选择，则返回第一个选择  
									}
									
									
									var values =names.join(",").split("-");
									
									//回显用
									$currentTd.prev().prev().text(values[1]);
									$currentTd.prev().text(values[0]);
									$currentTd.children("div").first().children("[name='payMonthly']").first().val(values[2]);
									//$currentTd.children("div").first().children("[name='salaryId']").first().val(ids.join(",").replace(/u_/ig,""));
									
									//传后台用
									$currentTd.parent().attr("flag","1");
									$currentTd.parent().attr("salaryId",ids.join(",").replace(/u_/ig,""));
								}//
								else if (v=="clear"){
									$("#salaryId").val("");
									$("#salaryName").val("");
				                }//
								if(typeof salaryTreeselectCallBack == 'function'){
									salaryTreeselectCallBack(v, h, f);
								}
							}, loaded:function(h){
								$(".jbox-content", top.document).css("overflow-y","hidden");
							}
						});
					});
				</script>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div style="text-align:center"> <input id="saveBtn" class="btn btn-primary" type="button" value="保存"/></div>
</body>
</html>