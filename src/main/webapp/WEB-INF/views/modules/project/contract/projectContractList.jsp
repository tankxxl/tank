<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>合同管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            function ajaxSubmit() {
                var json = form2js($("#searchForm")[0]);
                jeesns.jeesnsAjax('${ctx}/project/contract/projectContract/stat', 'POST', json);
            }

            $("#btnExportList").click(function(){
                top.$.jBox.confirm("确认要导出数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        <%--var url = $("#searchForm").attr("action");--%>
                        <%--$("#searchForm").attr("action","${ctx}/project/contract/projectContract/exportList");--%>
                        <%--$("#searchForm").submit();--%>
                        <%--$("#searchForm").attr("action", url);--%>

						ajaxSubmit();
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });
			
			$("#contentTable").find("input[export]").each(function(){
				$(this).click(function(){
					var proId =$(this).attr("proId");
					top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/project/contract/projectContract/export?id="+proId);
							$("#searchForm").submit();
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			});
			
			$(".trace").each(function() {
				$(this).click(function(){
					if ($(this).attr("procInsId") =="" || $(this).attr("procInsId") == null){
						top.$.jBox.tip('请先启动流程再跟踪。');
						return false;
					}
				});
			});

            $("#btnImport").click(function(){
                $.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true},
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
<div id="importBox" class="hide">
	<form id="importForm" action="${ctx}/project/contract/projectContract/import" method="post" enctype="multipart/form-data"
		  class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
		<a href="${ctx}/project/contract/projectContract/import/template">下载模板</a>
	</form>
</div>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/project/contract/projectContract/">合同列表</a></li>
	<shiro:hasPermission name="project:contract:projectContract:edit"><li><a href="${ctx}/project/contract/projectContract/form">合同添加</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="projectContract" htmlEscape="false"
		   action="${ctx}/project/contract/projectContract/" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<ul class="ul-form">
		<li><label>项目编号：</label>
			<form:input path="apply.projectCode" maxlength="64" class="input-medium"/>
		</li>
		<li><label>项目名称：</label>
			<form:input path="apply.projectName" maxlength="64" class="input-medium"/>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<input id="btnExportList" type="button" class="btn btn-primary" value="导出"/>
		</li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目编号</th>
				<th>项目名称</th>
				<th>更新时间</th>
				<th>审批状态</th>
				<shiro:hasPermission name="project:contract:projectContract:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="projectContract">
			<tr>
				<td><a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}">
					${projectContract.apply.projectCode}
				</a></td>
				<td>
					${projectContract.apply.projectName}
				</td>
				
				<td>
					<fmt:formatDate value="${projectContract.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				
				<c:choose>
					<c:when test="${projectContract.procStatus != '2'}">
						<td style="color:red;">
					</c:when>
					<c:otherwise>
						<td>
					</c:otherwise>
				</c:choose>
					${fns:getDictLabel(projectContract.procStatus, 'AuditStatus', '无启动流程')}
				</td>
				
				<shiro:hasPermission name="project:contract:projectContract:edit">
				<td>

					<c:choose>
						<c:when test="${projectContract.procStatus == null}">
							<a href="${ctx}/project/contract/projectContract/modify?id=${projectContract.id}">修改</a>
						</c:when>
						<c:otherwise>

						</c:otherwise>
					</c:choose>

					<c:if test="${projectContract.procStatus == '2'}">
					<input export="btnExport" class="btn btn-primary" type="button" proId="${projectContract.id}" value="导出"/>
					</c:if>
    				<a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}">详情</a>

    				
    				<c:if test="${projectContract.procStatus != '2'}">
						<a class="trace" target="_blank" procInsId="${projectContract.procInsId}" href="${ctx}/act/task/trace1?procInsId=${projectContract.procInsId}">跟踪</a>
					</c:if>
					<c:if test="${projectContract.procStatus == '2'}">
						<a href="${ctx}/project/contract/projectContract/delete?id=${projectContract.id}" onclick="return confirmx('确认要删除该合同吗？', this.href)">删除</a>
					</c:if>


					<%--<a href="${ctx}/project/execution/view?id=${projectPurchase.execution.id}" target="_jeesnsOpen"--%>
					   <%--title="${projectPurchase.apply.projectName}" width="800px" height="500px">--%>
						<%--<span class="label label-info">开票回款信息</span>--%>
					<%--</a>--%>
					<%--<a href="${ctx}/project/execution/view?id=${projectPurchase.execution.id}" target="_jeesnsOpen"--%>
					   <%--title="${projectPurchase.apply.projectName}" width="800px" height="500px">--%>
						<%--<span class="label label-info">开票申请</span>--%>
					<%--</a>--%>

				
    				<%-- <a href="${ctx}/project/contract/projectContract/form?id=${projectContract.id}">详情</a>
    				<a class="trace" target="_blank" procInsId="${projectContract.procInsId}" href="${ctx}/act/task/trace1?procInsId=${projectContract.procInsId}">跟踪1</a>
					<a class="trace" target="_blank" procInsId="${projectContract.procInsId}" href="${ctx}/act/task/trace2?procInsId=${projectContract.procInsId}">跟踪2</a>
					<a href="${ctx}/project/contract/projectContract/delete?id=${projectContract.id}" onclick="return confirmx('确认要删除该合同吗？', this.href)">删除</a> --%>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>