<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>薪资等级管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/salary/salaryLevel/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
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
	
	
	<style>
		.form-search .ul-form li label {
   		 text-align: right;
    		width: 100px;
		}
	</style>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/salary/salaryLevel/import" method="post" enctype="multipart/form-data"
			class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/salary/salaryLevel/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/salary/salaryLevel/">薪资等级列表</a></li>
		<shiro:hasPermission name="salary:salaryLevel:edit"><li><a href="${ctx}/salary/salaryLevel/form">薪资等级添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="salaryLevel" action="${ctx}/salary/salaryLevel/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>工程师职级：</label>
				<form:radiobuttons path="grade" items="${fns:getDictList('profession_grade')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li><label>工种：</label>
				<form:radiobuttons path="profession" items="${fns:getDictList('profession')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
				<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>工程师职级</th>
				<th>工种</th>
				<th>月薪资</th>
				<th>社保基数</th>
				<th>养老</th>
				<th>失业</th>
				<th>工伤</th>
				<th>生育</th>
				<th>医疗</th>
				<th>公积金</th>
				<th>五险一金小时成本合计</th>
				<th>工资小时成本</th>
				<th>人工小时总成本</th>
				<shiro:hasPermission name="salary:salaryLevel:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="salaryLevel">
			<tr>
				<td><a href="${ctx}/salary/salaryLevel/form?id=${salaryLevel.id}">
					${fns:getDictLabel(salaryLevel.grade, 'profession_grade', '')}
				</a></td>
				<td>
					${fns:getDictLabel(salaryLevel.profession, 'profession', '')}
				</td>
				<td>
					${salaryLevel.payMonthly}
				</td>
				<td>
					${salaryLevel.nssf}
				</td>
				<td>
					${salaryLevel.pension}
				</td>
				<td>
					${salaryLevel.unemployment}
				</td>
				<td>
					${salaryLevel.occupationalInjury}
				</td>
				<td>
					${salaryLevel.birth}
				</td>
				<td>
					${salaryLevel.medical}
				</td>
				<td>
					${salaryLevel.providentFund}
				</td>
				<td>
					${salaryLevel.insuranceAndHousingFundHourly}
				</td>
				<td>
					${salaryLevel.salaryHourly}
				</td>
				<td>
					${salaryLevel.laborHourly}
				</td>
				<shiro:hasPermission name="salary:salaryLevel:edit"><td>
    				<a href="${ctx}/salary/salaryLevel/form?id=${salaryLevel.id}">修改</a>
					<a href="${ctx}/salary/salaryLevel/delete?id=${salaryLevel.id}" onclick="return confirmx('确认要删除该薪资等级吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>