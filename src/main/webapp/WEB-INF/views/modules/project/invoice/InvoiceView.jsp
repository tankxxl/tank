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
    <script src="${ctxStatic}/vue/vue.min.js"></script>
</head>
<body>
<div id="rrapp" v-cloak>
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
    <form:hidden id="func" path="func" />

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
        <c:if test="${not empty projectInvoice.act.taskId && projectInvoice.act.status != 'finish'}">
            <tr>
                <td class="tit">您的意见</td>
                <td colspan="5">
                    <form:textarea path="act.comment" class="required" rows="5" maxlength="4000" style="width:95%"/>
                    <span class="help-inline"><font color="red">*</font></span>
                </td>
            </tr>
        </c:if>
    </table>

<%-- 定义一系列工具栏 --%>
<div id="toolbar" class="btn-group">
    <c:if test="${projectInvoice.act.taskDefKey eq 'usertask_finance_leader'}">
    <button id="btn_add" @click="myAddClick()" type="button" class="btn btn-default">
        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
    </button>
    <button id="btn_edit" @click="update()" type="button" class="btn btn-default" disabled>
        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
    </button>
    </c:if>
</div>
<table id="table" data-mobile-responsive="true"></table>

<div class="form-actions">
    <shiro:hasPermission name="project:invoice:edit">

    <c:if test="${not empty projectInvoice.act.taskId && projectInvoice.act.status != 'finish'}">
        <%--<input id="btnSubmit" class="btn btn-primary" type="button" value="同 意" onclick="$('#flag').val('yes'); return check()"/>&nbsp;&nbsp;&nbsp;&nbsp;--%>
        <%--<input id="btnSubmit" class="btn btn-warning" type="button" value="驳 回" onclick="$('#flag').val('no')"/>&nbsp;&nbsp;&nbsp;&nbsp;--%>

        <input id="btnSubmit" class="btn btn-primary" type="button" value="同 意" @click="btn1()" />&nbsp;&nbsp;&nbsp;&nbsp;
        <input id="btnSubmit" class="btn btn-warning" type="button" value="驳 回" @click="btn2()" />&nbsp;&nbsp;&nbsp;&nbsp;
    </c:if>
    </shiro:hasPermission>
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
</div>

<c:if test="${not empty projectInvoice.procInsId}">
    <act:histoicFlow procInsId="${projectInvoice.procInsId}" />
</c:if>
</form:form>
<validate:jsValidate modelAttribute="projectInvoice"></validate:jsValidate>
</div> <%-- end v-cloak --%>

<script>

// 执行代码(入口)
$(function () {
    //1.初始化Table
    initTable();
    //2.初始化Button的点击事件
    initBindEvent();
});

// 定义部分
/**
 * index: 父表当前行的行索引
 * row: 父表当前行的Json数据对象
 * $detail: 当前行下面创建的新行里面的td对象
 * 第三个参数尤其重要，因为生成的子表的table是装载在$detail对象里面的。
 * bootstrap table为我们生成了$detail这个对象，然后我们只需要往它里面填充我们想要的table即可。
 *
 */
function initSubTable(index, row, $detail) {
    // 得到发票号id，去查所有版本
    var itemId = row.id;
    // 创建一个table，并给table设置了id
    var cur_table = $detail.html('<table id="child_table"></table>').find('table');
    $(cur_table).bootstrapTable({
        url: '${ctx}/project/invoice/findVerList',
        method: 'get',
        queryParams: { itemId: itemId },
        ajaxOptions: { itemId: itemId },
        clickToSelect: true,
        detailView: false,//父子表
        uniqueId: "ID",
        pageSize: 10,
        pageList: [10, 25],
        columns: [{
            field: 'apply.id',
            title: '项目编号',
            visible: false
        }, {
            field: 'rowId',
            title: '序号',
            align: 'center',
            visible: true,
            formatter: function(value, row, index) {
                row.rowId = index;
                return index + 1;
            }
        }, {
            field: 'apply.projectName',
            title: '项目名称',
            resizable: true,
            visible: false,
            sortable: true
        }, {
            field: 'contract.contractCode',
            title: '合同号',
            visible: false
        }, {
            field: 'contract.id',
            title: '合同id',
            visible: false
        }, {
            field: 'invoiceType',
            title: '发票类型',
            formatter: function (value, row, index) {
                return getDictLabel(${fns:toJson(fns:getDictList('jic_invoice_type'))}, value);
            }
        }, {
            field: 'customerInvoice.customerName',
            title: '客户名称'
        }, {
            field: 'content',
            title: '开票内容'
        }, {
            field: 'spec',
            title: '规格型号'
        }, {
            field: 'num',
            title: '数量'
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
            field: 'ver',
            title: '版本'
        }, {
            field: 'settle',
            title: '结算周期',
            formatter: function (value, row, index) {
                return new Date(row.settlementBegin).Format("yyyy-MM-dd") + '--' +
                    new Date(row.settlementEnd).Format("yyyy-MM-dd");
            }
        }, {
            field: 'invoiceNo',
            title: '发票号'
        }, {
            field: 'remarks',
            title: '备注'
        }]
    });
} // end func InitSubTable()

function initTable () {
    // 先销毁表格
    $('#table').bootstrapTable('destroy').bootstrapTable({
        toolbar: '#toolbar',
        contentType:'application/x-www-form-urlencoded; charset=UTF-8',
        data: ${fns:toJson(projectInvoice.invoiceItemList)},
        selectItemName: 'selectItemName',  // radio or checkbox 的字段名
        method: 'post',                      //请求方式（*）
        queryParams : queryParams,  //传递参数（*）
        dataField : "list", //很重要，前后端关键字段对应，这是后端返回的实体数据！表示后端传递的对象数据，名字要与对象的名字相同。默认值：rows
        totalField: "total", // 很重要，跟后端返回的数据相关。默认值：total，如果在这里不指定自定义的fieldname，就要在responseHandler回调函数中修改了
        classes : 'table table-bordered', // Class样式
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortName: "updateDate",             // 定义排序列
        sortable: true,                     //是否启用排序
        sortOrder: "desc",                   //排序方式
        sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                      //初始化加载第一页，默认第一页,并记录
        pageSize: 30,                     //每页的记录行数（*）
        pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
        search: false,                      //是否显示表格搜索
        strictSearch: true,
        showColumns: true,                  //是否显示所有的列（选择显示的列）
        showRefresh: true,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        // singleSelect: true,				//复选框只能选择一条记录
        clickToSelect: false,                //是否启用点击选中行
        uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
        showToggle: true,                   //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: true,                  //是否显示父子表
        columns: [
            <c:if test="${projectInvoice.act.taskDefKey eq 'usertask_finance_leader'}">
            {
            checkbox: true,
            visible: true                  //是否显示复选框
        },
            </c:if>
            {
            field: 'apply.id',
            title: '项目编号',
            visible: false
        }, {
            field: 'rowId',
            title: '序号',
            align: 'center',
            visible: false,
            formatter: function(value, row, index) {
                row.rowId = index;
                return index + 1;
            }
        }, {
            field: 'apply.projectName',
            title: '项目名称',
            resizable: true,
            sortable: true,
            formatter: function (value, row, index) { // 可以在此合成字段返回：row.field1 + row.field2
                return value;
            }
        }, {
            field: 'contract.contractCode',
            title: '合同号'
        }, {
            field: 'contract.id',
            title: '合同id',
            visible: false
        }, {
            field: 'invoiceType',
            title: '发票类型',
            formatter: function (value, row, index) {
                return getDictLabel(${fns:toJson(fns:getDictList('jic_invoice_type'))}, value);
            }
        }, {
            field: 'customerInvoice.customerName',
            title: '客户名称'
        }, {
            field: 'content',
            title: '开票内容'
        }, {
            field: 'spec',
            title: '规格型号'
        }, {
            field: 'num',
            title: '数量'
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
                field: 'settle',
                title: '结算周期',
                formatter: function (value, row, index) {
                    return new Date(row.settlementBegin).Format("yyyy-MM-dd") + '--' +
                        new Date(row.settlementEnd).Format("yyyy-MM-dd");
                }
        }, {
            field: 'invoiceNo',
            title: '发票号',
            formatter: function (value, row, index) {
                if ( isUndefined(value) ) {
                    return '';
                }
                return value;
            }
            <c:if test="${projectInvoice.act.taskDefKey eq 'usertask_finance_leader'}">
            ,
            editable: {
                type: 'text',
                title: '发票号',
                mode: 'inline',
                emptytext: '点我填写'
            }
            </c:if>
        }, {
            field: 'remarks',
            title: '备注'
        }
        <c:if test="${projectInvoice.act.taskDefKey eq 'usertask_finance_leadertemp'}">

        , {
            field: 'operate',
            title: '操作',
            visible: true,
            align: 'center',
            events: operateEvents,
            formatter: operateFormatter
        }
        </c:if>
        ],
        onDblClickRow: function (row, $element, field) {
            editById(row.id);
        },
        onExpandRow: function (index, row, $detail) {
            // index: 父表当前行的行索引
            // row: 父表当前行的Json数据对象
            // $detail: 当前行下面创建的新行里面的td对象
            // 第三个参数尤其重要，因为生成的子表的table是装载在$detail对象里面的。
            // bootstrap table为我们生成了$detail这个对象，然后我们只需要往它里面填充我们想要的table即可。
            // ButtonInit.InitSubTable(index, row, $detail);
            initSubTable(index, row, $detail);
        },
        onEditableSave: function (field, row, oldValue, $el) {
            // field : 编辑的字段名称，如：returnDate、invoiceNo等
            // ajax提交更新
            // tips(row + oldValue + field);
            // jeesnsDialog.tips(row.returnDate  + JSON.stringify($el));
            $('#table').bootstrapTable("resetView");
            // $('#table').bootstrapTable( {} );
            // $('#table').bootstrapTable('updateRow', {index: row.rowId, row: row});
            jeesns.jeesnsAjax('${ctx}/project/invoice/saveItemAjax', 'POST', row);
            <%--jeesns.jeesnsAjax('${ctx}/project/invoice/test2', 'POST', row);--%>

        }
    });
} // end func initTable()

function operateFormatter(value, row, index) {
    // 也可以用shiro:hasPermission、hasRole来判断
    return [
        <c:if test="${projectInvoice.act.taskDefKey eq 'usertask_finance_leader'}">
        '<a class="offset" href="javascript:void(0)" title="offset">对冲',
        '<i class="fa fa-check"></i>',
        '</a>',
        </c:if>
    ].join('');
}

window.operateEvents = {
    'click .offset': function (e, value, row, index) {
        // 后台直接添加一个对冲发票
        <%--jeesns.jeesnsAjax('${ctx}/project/invoice/saveItemAjax?id=' + row.id, 'POST');--%>
        <%-- 或者前台添加一个对冲发票 --%>
        jeesnsDialog.openEdit('${ctx}/project/invoice/offsetView?id=' + row.id,
            '增加负数对冲开票', '600px', '650px', function(data) {
                data['func'] = 'offset';
                jeesns.jeesnsAjax('${ctx}/project/invoice/saveItemAjax', 'POST', data);
            });
        return false;
    },
    'click .remove': function (e, value, row, index) {
        alert("remove");
        return false;
    },
    'click .using': function (e, value, row, index) {
        alert("using");
        return false;
    },
    'click .upload': function (e, value, row, index) {
        alert("upload");
        // $('.adUpload').fileupload({
        //     url : '/wadmin/ad/adUpload/adId/'+row['adId'],
        //     dataType: 'json',
        //     add: function (e, data) {
        //         data.submit();
        //     },
        //     done: function (e, data) {
        //         var response = data.result;
        //         if (response.result != 0) {
        //             toastr.error(response.message);
        //             return false;
        //         }
        //         toastr.success("上传成功");
        //         $("#table").bootstrapTable('refresh');
        //     }
        // });
        return false;
    }
};

// 得到查询的参数，由bootstrap-table组件调用并传参
function queryParams (params) {
    //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
    var temp = $("#searchForm").serializeJsonObject();
    temp["pageSize"] = params.limit;                        //页面大小
    temp["pageNo"] = (params.offset / params.limit) + 1;  //页码
//        temp["sort"] = params.sort;                         //排序列名
    temp["orderBy"] = params.sort;                         //排序列名，传到后台排序
    temp["sortOrder"] = params.order;                   //排位命令（desc，asc）
    return temp;
} // end func queryParams()

function initBindEvent () {
    // 监听table的事件，设置btn的状态
    $("#table").on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
        $("#btn_delete").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
        $("#btn_edit").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
    });

    // 监听点击事件获取当前行号
    $("#table").on('click-row.bs.table', function(e, row, $element) {
        var index = $element.data('index');
    });

    $("#btn_delete").click(function () {
        // 后台删除
        confirmx('确定要删除选中的记录？', function(){
            var ids = getSelectedIds('table');
            var contractIds = $.map( getSelectedRows('table'), function (row) {
                return row.contract.contractCode;
            });

            jeesns.jeesnsAjax('${ctx}/project/invoice/deleteItemByIds', 'POST', ids, function(resp) {
                // 前台删除
                $('#table').bootstrapTable('remove', {
                    field: 'contract.contractCode',
                    values: contractIds
                });
                // 设置btn_del的状态
                $("#btn_delete").prop('disabled', true);
                $("#btn_edit").prop('disabled', true);
            });
        }); // end confirm
    }); // end click

} // end func initBindEvent

// 全局函数
function submitG() {
    var jform = $("#inputForm");
    jform.validate();
    if (!jform.valid()) {
        jeesnsDialog.tips("请修改。");
        return;
    }
    var json = form2js($("#inputForm")[0]);

    if (!check()) {
        return;
    }

    var data = $("#table").bootstrapTable('getData');
    if (data === undefined || data.length == 0) {
        jeesnsDialog.tips("开票项不能为空。");
        return;
    }
    json.invoiceItemList = data;
    // console.log("form.json=" + JSON.stringify(json));
    jeesns.jeesnsAjax('${ctx}/project/invoice/auditAjax', 'POST', json);
}


function check() {
    // $('#flag').val('yes');
    // 空值的个数
    var nos =  $.map($('#table').bootstrapTable('getData'), function (row) {
        // 空值才返回数据
        // if (!row.invoiceNo || !row.returnDate) {
        //     return "qq";
        // }

        if (!row.invoiceNo ) {
            return "qq";
        }
    });

    // var tt =  $.map($('#table').bootstrapTable('getData'), function (row) {
    //     return row.returnDate;
    // });
    // console.log("数据为：" + tt);
    // 如果没有空值，返回true，提交form
    if (nos.length == 0) {
        // $("#inputForm").submit();
        return true;
    } else {
        <c:if test="${projectInvoice.act.taskDefKey eq 'usertask_finance_leader'}">
        jeesnsDialog.tips("请在表格中输入发票号！");
        // window.alert("请在表格中输入发票号和回款日期");
        return false;
        </c:if>

        <c:if test="${projectInvoice.act.taskDefKey != 'usertask_finance_leader'}">
        return true;
        </c:if>
    }
}

// 全局变量，用于给编辑框传递参数
// 当前选中行
row = null;
// 当前选中行的index
rowIdx = null;
// 全局变量，定义vm变量，
// Vue三部分：el、data、methods
// 创建一个Vue实例或"ViewModel"，它连接View与Model
var vm = new Vue({
    el:'#rrapp',
    data:{ // 这是我们的Model，也可以写到外面(全局)
        showList: true,
        newItem: {},
        user:{
            deptId:null,
            roleIdList:[]
        }
    }, // data end
    methods: {
        btn1: function () {
            $('#flag').val('yes');
            $('#func').val('yes');
            submitG();
        },
        btn2: function () {
            $('#flag').val('no');
            $('#func').val('no');
            submitG();
        },
        btn3: function () {
            $('#flag').val('saveFinishProcess');
            $('#func').val('saveFinishProcess');
            submitG();
        },
        btn4: function () {
            $('#flag').val('saveOnly');
            $('#func').val('saveOnly');
            submitG();
        },
        myAddClick: function(){ // vue增加
            row = null;
            rowIndex = null;
            jeesnsDialog.openEdit('${ctx}/project/invoice/addItemView',
                '增加开票项', '600px', '650px', function(data) {
                    $('#table').bootstrapTable('append', data);
                });
        },
        update: function() {
            // 全局变量，用于给iframe的dialog传值，修改时用
            var indexes = getSelectedIndexes();
            if (!isArraySingle(indexes)) {
                jeesnsDialog.tips("只能选择一条数据");
                return;
            }
            row = getSelectedRows('table')[0];
            rowIndex = indexes[0];

            jeesnsDialog.openEdit('${ctx}/project/invoice/addItemView?id=' + row.id,
                '增加开票项', '600px', '650px', function(data) {
                    $('#table').bootstrapTable('updateRow', {index: rowIndex, row: data});
                });
        }
    }  // method end
});  // end vm

</script>

<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/mobile/bootstrap-table-mobile.js"></script>
<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/resizable/bootstrap-table-resizable.js"></script>
<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/editable/bootstrap-table-editable.js"></script>


<link href="${ctxStatic}/x-editable-1.5.1/dist/bootstrap-editable/css/bootstrap-editable.css" rel="stylesheet" />
<script src="${ctxStatic}/x-editable-1.5.1/dist/bootstrap-editable/js/bootstrap-editable.min.js"></script>

</body>

</html>