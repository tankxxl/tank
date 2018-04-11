<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
	<%-- 查询form-发票列表-用于发票统计 --%>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(document).ready(function() {

            $("#contentTable").find("input[export]").each(function(){
                $(this).click(function(){
                    var proId =$(this).attr("proId");
                    top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
                        if(v=="ok"){
                            $("#searchForm").attr("action","${ctx}/project/invoice/export?id="+proId);
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
        });

        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        // demo data transfer into a query string:"a=1&b=2&c=3"
        var params = {
			a: 1,
			b: 2,
			c: 3
		};
     	// ES5
		// var queryString = Object.keys(params).map(function(key) {
		//     return key + '=' + params[key];
		// }).join('&');
		// using jQuery
		var queryString1 = $.param(params);
		// parameter encoding
		// var queryString2 = Object.keys(params).map((key) => {
		//     return encodeURIComponent(key) + '=' + encodeURIComponent(params[key])
		// }).join('&');


	</script>
</head>
<%-- 发票列表 --%>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/project/invoiceItem/">发票统计</a></li>
	<%--<shiro:hasPermission name="project:invoice:edit"><li><a href="${ctx}/project/invoice/form">开票添加</a></li></shiro:hasPermission>--%>
</ul>
<form:form id="searchForm"
		   modelAttribute="projectInvoiceItem"
		   action="${ctx}/project/invoiceStat/stat"
		   method="post"
		   htmlEscape="false"
		   class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

	<ul class="ul-form">
		<li><label>项目编号：</label>
			<form:input path="apply.projectCode" type="text" placeholder="项目编号" maxlength="64" class="input-medium"/>
		</li>

		<li><label>项目名称：</label>
			<form:input path="apply.projectName" type="text" placeholder="项目名称" maxlength="64" class="input-medium"/>
		</li>

		<li><label>合同编号：</label>
			<form:input path="contract.contractCode" type="text" placeholder="合同编号" maxlength="64" class="input-medium"/>
		</li>

		<li><label>开票日期：</label>

			<input id="queryBeginDate"  name="queryBeginDate"  type="text" readonly="readonly" class="input-mini Wdate"
				   value="<fmt:formatDate value="${projectInvoiceItem.queryBeginDate}" pattern="yyyy-MM-dd"/>"
				   onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
			　--　
			<input id="queryEndDate" name="queryEndDate" type="text" readonly="readonly" class="input-mini Wdate"
				   value="<fmt:formatDate value="${projectInvoiceItem.queryEndDate}" pattern="yyyy-MM-dd"/>"
				   onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
		</li>
		<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询">
			<i class="icon-search"></i>
			</input>
		</li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>

<%-- 定义一系列工具栏 --%>
<%-- <div id="toolbar" class="btn-group" style="display:none;">
	<button id="btn_resign" type="button" class="btn btn-default">
		<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>重开
	</button>
	<shiro:hasAnyRoles name="usertask_finance_leader">
	<button id="btn_edit" type="button" class="btn btn-default">
		<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
	</button>
	</shiro:hasAnyRoles>
</div>
<table id="table" data-mobile-responsive="true"></table>--%>

<script>

function ajaxSubmit() {
	var json = form2js($("#searchForm")[0]);
	jeesns.jeesnsAjax('${ctx}/project/invoiceStat/stat', 'POST', json);
}

// 全局变量，用于给编辑框传递参数
// 当前选中行
row = null;
// 当前选中行的index，行号，用于update
rowIdx = null;

// 执行代码
$(function () {
	//1.初始化Table
	initTable();
	//2.初始化btn的点击事件
	$("#btnSubmit").click(function () {
		ajaxSubmit();
	});
	$("#btn_resign").click(function () {
        var ids = getSelectedIds('table');
        <%--jeesns.jeesnsAjax('${ctx}/project/invoice/resignView', 'POST', ids);--%>
        post('${ctx}/project/invoice/resignView', {itemIds: ids});
    });
});

    // 定义部分
    function initTable() { // 初始化Table
// 先销毁表格
        $('#table').bootstrapTable('destroy').bootstrapTable({
            toolbar: '#toolbar',
            url: '${ctx }/project/invoiceItem/table',
            method: 'post',                      //请求方式（*）
            contentType: "application/x-www-form-urlencoded",
            selectItemName: 'selectItemName',  // radio or checkbox 的字段名
            queryParams : queryParams,  //传递参数（*）
            dataField : "list", //很重要，这是后端返回的实体数据！表示后端传递的对象数据，名字要与对象的名字相同。
            totalField: "count",
            classes : 'table table-bordered table-hover', // Class样式
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortName: "updateDate",             // 定义排序列
            sortOrder: "desc",                   //排序方式
            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            pageSize: 30,                     //每页的记录行数（*）
            strictSearch: true,
            search: false,               //是否显示表格搜索，此搜索是客户端搜索，不会进服务端
            showColumns: true,                  //是否显示所有的列（选择显示的列）
            showRefresh: true,                  //是否显示刷新按钮
            uniqueId: "id",                     //每一行的唯一标识，一般为主键列
            idField: "id",
            showToggle: true,                   //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                  //是否显示父子表
            columns: [{
                checkbox: true,
                visible: true                  //是否显示复选框
            }, {
                field: 'apply.projectCode',
                title: '项目编号',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) {
                    if (value) {
                        return '<a href="${ctx}/apply/external/projectApplyExternal/form?id=' + row.apply.id + '">' + value + '</a>';
                    } else {
                        return value;
                    }
                }
            }, {
                field: 'apply.projectName',
                title: '项目名称',
                resizable: true,
                sortable: true,
                formatter: function (value, row, index) {
                    if (value) {
                        return '<a href="${ctx}/apply/external/projectApplyExternal/form?id=' + row.apply.id + '">' + value + '</a>';
                    } else {
                        return value;
                    }
                }
            }, {
                field: 'contract.contractCode',
                title: '合同编号',
                formatter: function (value, row, index) {
                    if (value) {
                        return '<a href="${ctx}/project/contract/projectContract/form?id=' + row.contract.id + '">' + value + '</a>';
                    } else {
                        return value;
                    }
                }
            }, {
                field: 'customerInvoice.customerName',
				title: '开票客户名称'

			}, {
                field: 'invoiceNo',
                title: '发票号'
            }, {
                field: 'returnAmount',
                title: '回款金额'
            }, {
                field: 'returnDate',
                title: '回款日期',
                formatter: function (value, row, index) {
                    if (isUndefined(value)) {
                        return '';
					} else if (value) {
                        return new Date(value).Format("yyyy-MM-dd");
                    } else {
                        return value;
                    }
                }
            }, {
                field: 'saler.name',
                title: '销售人员'
            }, {
                field: 'updateDate',
                title: '更新时间',
                formatter: function (value, row, index) {
                    return new Date(value).Format("yyyy-MM-dd");
                }
            } ]
        }); // 定义table

    } // end func initTable()

//得到查询的参数
function queryParams(params) {
    //这里是在ajax发送请求的时候设置一些参数 params有什么东西，自己看看源码就知道了
	//这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
	var temp = $("#searchForm").serializeJsonObject();
	temp["pageSize"] = params.limit;                        //页面大小
	temp["pageNo"] = (params.offset / params.limit) + 1;  //页码
	temp["orderBy"] = params.sort;                         //排序列名
	temp["sortOrder"] = params.order;                   //排位命令（desc，asc）
	return temp;
}; // end func queryParams()
</script>

<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/mobile/bootstrap-table-mobile.js"></script>
<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/resizable/bootstrap-table-resizable.js"></script>
<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/editable/bootstrap-table-editable.js"></script>


<link href="${ctxStatic}/x-editable-1.5.1/dist/bootstrap-editable/css/bootstrap-editable.css" rel="stylesheet" />
<script src="${ctxStatic}/x-editable-1.5.1/dist/bootstrap-editable/js/bootstrap-editable.min.js"></script>

</body>
</html>