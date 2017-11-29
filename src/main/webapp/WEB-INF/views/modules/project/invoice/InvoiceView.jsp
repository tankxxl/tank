<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
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
</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${ empty projectInvoice.act.taskId}">
        <li><a href="${ctx}/project/invoice/">开票列表</a></li>
    </c:if>
    <li class="active"><a href="${ctx}/project/invoice/form?id=${projectInvoice.id}">开票
        <shiro:hasPermission name="project:invoice:edit">
        ${not empty projectInvoice.act.taskId?'审批':'查看'}</shiro:hasPermission>
        <shiro:lacksPermission name="project:invoice:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectInvoice" htmlEscape="false"
           action="${ctx}/project/invoice/saveAudit" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="act.taskId"/>
    <form:hidden path="act.taskName"/>
    <form:hidden path="act.taskDefKey"/>
    <form:hidden path="act.procInsId"/>
    <form:hidden path="act.procDefId"/>
    <form:hidden id="flag" path="act.flag"/>
    <%--设置id，前端设置值，传回后端--%>
    <form:hidden id="contractId" path="contract.id" />
    <sys:message content="${message}"/>

    <table class="table-form">
        <caption>项目信息</caption>
        <tr>
            <td class="tit" >申请人</td>
            <td colspan="2">
                ${projectInvoice.createBy.name}
            </td>

            <td class="tit" >部门</td>
            <td colspan="2">
                ${projectInvoice.createBy.office.name}
            </td>
        </tr>

        <tr>
            <td class="tit">备注</td>
            <td colspan="5">
                ${projectInvoice.remarks}
            </td>
        </tr>

        <tr>
            <td class="tit" >文件附件</td>
            <td colspan="5">
                <form:hidden id="attachment" path="attachment" maxlength="20000"  />
                <sys:ckfinder input="attachment" type="files" uploadPath="/project/purchase" readonly="true" />
            </td>
        </tr>
    </table>

    <table id="table" data-mobile-responsive="true"></table>

    <div class="form-actions">
        <shiro:hasPermission name="project:invoice:edit">

        <c:if test="${not empty projectInvoice.act.taskId && projectInvoice.act.status != 'finish'}">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="同 意" onclick="$('#flag').val('yes')"/>&nbsp;
            <input id="btnSubmit" class="btn btn-inverse" type="submit" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
    </div>

    <c:if test="${not empty projectInvoice.procInsId}">
        <act:histoicFlow procInsId="${projectInvoice.procInsId}" />
    </c:if>
</form:form>
<validate:jsValidate modelAttribute="projectInvoice"></validate:jsValidate>

<script>
    // 执行代码
    $(function () {
        //1.初始化Table
        var oTable = new TableInit();
        oTable.Init();

        //2.初始化Button的点击事件
        var oButtonInit = new ButtonInit();
        oButtonInit.Init();
    });

    // 定义部分
    // 定义一个对象
    var TableInit = function () {
        var oTableInit = new Object();

        // 定义对象的方法，初始化Table
        oTableInit.Init = function () {
            // 先销毁表格
            $('#table').bootstrapTable('destroy');

            $('#table').bootstrapTable({
                toolbar: '#toolbar',
                contentType:'application/x-www-form-urlencoded; charset=UTF-8',
                data: ${fns:toJson(projectInvoice.invoiceItemList)},
                selectItemName: 'selectItemName',  // radio or checkbox 的字段名
                method: 'post',                      //请求方式（*）
                queryParams : oTableInit.queryParams,  //传递参数（*）
                dataField : "list", //很重要，这是后端返回的实体数据！表示后端传递的对象数据，名字要与对象的名字相同。默认值：rows
                totalField: "total", // 很重要，跟后端返回的数据相关。默认值：total，如果在这里不指定自定义的fieldname，就要在responseHandler回调函数中修改了
                classes : 'table table-bordered', // Class样式
                striped: true,                      //是否显示行间隔色
                cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true,                   //是否显示分页（*）
                sortName: "updateDate",             // 定义排序列
                sortable: true,                     //是否启用排序
                sortOrder: "desc",                   //排序方式
                sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
                pageNumber: 1,                      //初始化加载第一页，默认第一页,并记录
                pageSize: 30,                     //每页的记录行数（*）
                pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
                search: false,                      //是否显示表格搜索
                strictSearch: true,
                showColumns: true,                  //是否显示所有的列（选择显示的列）
                showRefresh: true,                  //是否显示刷新按钮
                minimumCountColumns: 2,             //最少允许的列数
                uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
                showToggle: true,                   //是否显示详细视图和列表视图的切换按钮
                cardView: false,                    //是否显示详细视图
                detailView: false,                  //是否显示父子表
                rowStyle: function (row, index) { //设置行的特殊样式
                    var strclass = "";
                    if (index == 1) {
                        strclass = "danger";
                    }
                    return { classes: strclass };
//				return strclass;
                },
                columns: [{
                    field: 'apply.id',
                    title: '项目编号',
                    titleTooltip: "tips",
                    align: 'center',
                    valign: 'middle',
                    visible: false
                    // width: '180'
                }, {
                    field: 'apply.projectName',
                    title: '项目名称',
                    sortable: true,
                    formatter: function (value, row, index) { // 可以在此合成字段返回：row.field1 + row.field2
                        return value;
                    }
                }, {
                    field: 'clientName',
                    title: '客户名称'
                }, {
                    field: 'content',
                    title: '开票内容'
                }, {
                    field: 'spec',
                    title: '规格型号',
                    formatter: function (value, row, index) {
                        return value; <%--return getDictLabel(${fns:toJson(fns:getDictList('pro_category'))}, value);--%>
                    }
                }, {
                    field: 'num',
                    title: '数量',
                    formatter: function (value, row, index) {
//                    return new Date(value).Format("yyyy-MM-dd");
                        return value;
                    }

                }, {
                    field: 'unit',
                    title: '单位'
                }, {
                    field: 'price',
                    title: '单价'
                }, {
                    field: 'amount',
                    title: '金额'
                }, {
                    field: 'profit',
                    title: '利润点'
                }, {
                    field: 'settlement',
                    title: '结算周期'
                }, {
                    field: 'contract.contractCode',
                    title: '合同号'
                }, {
                    field: 'contract.id',
                    title: '合同id',
                    visible: false
                }, {
                    field: 'invoiceNo',
                    title: '发票号'
                }, {
                    field: 'remarks',
                    title: '备注'
                }, {
                    field: 'id',
                    title: '操作',
                    align: 'center',
                    formatter:function(value,row,index){
                        var btnExport = '', btnView = '', btnDelete = '', btnTrace = '', btnEdit = '';
                        if (row.procStatus == '2') {
                            btnExport = '<input export="btnExport" class="btn btn-primary" type="button" proId="' + row.id + '" value="导出"/>&nbsp';
                        }
                        btnView = '<a href="${ctx}/apply/external/projectApplyExternal/form?id=' + row.id + '">详情</a>&nbsp';
                        return btnExport + btnTrace + btnDelete + btnEdit;
                    }
                } ],
            });
        }; // end Init()

        //定义对象的私有方法-queryParams，得到查询的参数
        oTableInit.queryParams = function (params) {
            //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            var temp = $("#searchForm").serializeJsonObject();
            temp["pageSize"] = params.limit;                        //页面大小
            temp["pageNo"] = (params.offset / params.limit) + 1;  //页码
//        temp["sort"] = params.sort;                         //排序列名
            temp["orderBy"] = params.sort;                         //排序列名，传到后台排序
            temp["sortOrder"] = params.order;                   //排位命令（desc，asc）
            //特殊格式的条件处理
//        temp["WHC_Age"] = $("#WHC_Age").val();
            return temp;
        }; // end queryParams()
        return oTableInit;
    }; // end var TableInit

    // buttonInit对象
    var ButtonInit = function () {
        var oInit = new Object();
        var postdata = {};

        oInit.Init = function () {

            // 监听table的事件，设置btn的状态
            $("#table").on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
                $("#btn_delete").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
                $("#btn_edit").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
            });

            $("#btnSubmit").click(function () {
                var oTable = new TableInit();
                oTable.Init();
//            $("#table").bootstrapTable('refresh', oTable.queryParams);
            });

        };
        return oInit;
    }; // end var ButtonInit

    // 全局函数
    function search() {
        var opt = {
            url: 'doDynamicsList',
            silent: true,
            query:{
                'sd.dno':searchForm.dno.value,
                'sd.userInfo.userName':searchForm.userName.value,
                'sd.userInfo.name':searchForm.name.value,
                'sd.title':searchForm.title.value,
                'sd.text':searchForm.text.value
            }
        };
        // 需要先摧毁table
        $("#table").bootstrapTable('destroy');
        $('#table').bootstrapTable('refresh',opt);
    }

</script>

</body>
</html>