<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
            // 二选一的情况，二个输入框，有一个输入就算通过。groups可以把多处报错放到一处，depends可以解决多选一的情况。
            // jQuery validate插件生效的入口，在form组件上执行validate();方法，validate所有的配置都在方法参数里
			$("#inputForm").validate({
				submitHandler: function(form){ //验证通过后的执行方法
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) { //错误提示在什么地方
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
                    // 指明错误放置的位置，默认情况是：error.appendTo(element.parent());
                    // 即把错误信息放在验证的元素后面
                }
			});

            // 必填项提示，后面加星提示
            $('.required').after('<span style="color:red">&nbsp;*</span>');

        });
	</script>

    <script src="${ctxStatic}/bootstrap-table/dist/extensions/mobile/bootstrap-table-mobile.js"></script>
    <script src="${ctxStatic}/bootstrap-table/dist/extensions/resizable/bootstrap-table-resizable.js"></script>
    <script src="${ctxStatic}/bootstrap-table/dist/extensions/editable/bootstrap-table-editable.js"></script>
    <script src="${ctxStatic}/vue/vue.min.js"></script>
    <script src="${ctxStatic}/modules/project/common.js"></script>
</head>
<body>
<%-- Vue，这是我们的View，script标签中是Model和ViewModel --%>
<div id="rrapp" v-cloak>
<ul class="nav nav-tabs">
    <c:if test="${ empty projectInvoice.act.taskId}">
        <li><a href="${ctx}/project/invoice/">开票申请列表</a></li>
    </c:if>
    <li class="active"><a href="${ctx}/project/invoice/form?id=${projectInvoice.id}">开票申请
        <shiro:hasPermission name="project:invoice:edit">${not empty projectInvoice.id?'修改':'添加'}</shiro:hasPermission>
        <shiro:lacksPermission name="project:invoice:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="projectInvoice" htmlEscape="false"
           action="${ctx}/project/invoice/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="act.taskId"/>
    <form:hidden path="act.taskName"/>
    <form:hidden path="act.taskDefKey"/>
    <form:hidden path="act.procInsId"/>
    <form:hidden path="act.procDefId"/>
    <form:hidden id="flag" path="act.flag"/>
    <%--设置id，前端设置值，传回后端--%>
    <%--<form:hidden id="contractId" path="contract.id" />--%>
    <form:hidden id="func" path="func" />
    <sys:message content="${message}"/>
    <table class="table-form">
            <caption>项目信息</caption>
        <tr>
            <td  class="tit" >申请人</td>
            <td colspan="2">
                <c:if test="${not empty projectInvoice.createBy.name}">
                    ${projectInvoice.createBy.name}
                </c:if>
                <c:if test="${empty projectInvoice.createBy.name}">
                    ${fns:getUser().name}
                </c:if>
            </td>

            <td  class="tit" >部门</td>
            <td colspan="2">
                <c:if test="${not empty projectInvoice.createBy.office}">
                    ${projectInvoice.createBy.office.name}
                </c:if>
                <c:if test="${empty projectInvoice.createBy.office}">
                    ${fns:getUser().office.name}
                </c:if>
            </td>
        </tr>

        <tr>
            <td class="tit">备注</td>
            <td colspan="5">
                <div style="white-space:nowrap;">
                    <form:textarea path="remarks" class="required" style="width:96%" maxlength="255"
                                   placeholder=""/>
                </div>
            </td>
        </tr>

        <tr>
            <td class="tit" >文件附件</td>
            <td colspan="5">
                <form:hidden id="attachment" path="attachment" maxlength="20000"  />
                <sys:ckfinder input="attachment" type="files" uploadPath="/project/invoice" selectMultiple="true" />
            </td>
        </tr>
    </table>
    <br/>

    <%-- 定义一系列工具栏 --%>
    <div id="toolbar" class="btn-group">
        <button id="btn_add" @click="myAddClick()" type="button" class="btn btn-default">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
        </button>
        <button id="btn_edit" @click="update()" type="button" class="btn btn-default" disabled>
            <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
        </button>
        <button id="btn_delete" @click="del()" type="button" class="btn btn-default" disabled>
            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
        </button>

        <%--<a id="btn_add" class="btn btn-primary" @click="myAddClick()"><i class="fa fa-plus"></i>&nbsp;新增</a>--%>
        <%--<a id="btn_edit" class="btn btn-primary" @click="update()"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</a>--%>
        <%--<a id="btn_delete" class="btn btn-primary" @click="del()"><i class="fa fa-trash-o"></i>&nbsp;删除</a>--%>
    </div>

    <table id="table" data-mobile-responsive="true"></table>
    <div class="form-actions">
        <shiro:hasPermission name="project:invoice:edit">
        <input id="btnSubmit" class="btn btn-primary" type="button" value="提交申请" @click="btn1()" />&nbsp;
        <c:if test="${not empty projectInvoice.id}">
            <input id="btnSubmit2" class="btn btn-inverse" type="button" value="销毁申请" @click="btn2()" />&nbsp;
        </c:if>
        </shiro:hasPermission>

        <shiro:hasPermission name="apply:external:projectApplyExternal:super">
            <input id="btnSubmit" class="btn btn-primary" type="button" value="保存并结束流程" @click="btn3()" data-toggle="tooltip" title="小心操作！"/>&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="button" value="只保存表单数据" @click="btn4()" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
        </shiro:hasPermission>
        &nbsp;&nbsp;
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
    </div>

    <c:if test="${not empty projectInvoice.procInsId}">
        <act:histoicFlow procInsId="${projectInvoice.procInsId}" />
    </c:if>
</form:form>
</div> <%-- end v-cloak --%>

<script>
// 执行代码
$(function () {
    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();
}); // 执行代码结束

/**
 * index: 父表当前行的行索引
 * row: 父表当前行的Json数据对象
 * $detail: 当前行下面创建的新行里面的td对象
 * 第三个参数尤其重要，因为生成的子表的table是装载在$detail对象里面的。
 * bootstrap table为我们生成了$detail这个对象，然后我们只需要往它里面填充我们想要的table即可。
 *
 */
function InitSubTable(index, row, $detail) {
    // 得到合同号id，去查所有版本
    var contract_id = row.contract.id;
    console.log(JSON.stringify(row));
    var cur_table = $detail.html('<table></table>').find('table');
    $(cur_table).bootstrapTable({
        url: '${ctx}/project/invoice/findVerList',
        method: 'get',
        queryParams: { contractId: contract_id },
        ajaxOptions: { contractId: contract_id },
        clickToSelect: true,
        detailView: false,//父子表
        uniqueId: "ID",
        pageSize: 10,
        pageList: [10, 25],
        columns: [{
            field: 'apply.id',
            title: '项目编号',
            titleTooltip: "tips",
            align: 'center',
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
            field: 'clientName',
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
            field: 'settlement',
            title: '结算周期'
        }, {
            field: 'invoiceNo',
            title: '发票号'
        }, {
            field: 'remarks',
            title: '备注'
        }],
        //无线循环取子表，直到子表里面没有记录
        onExpandRow: function (index, row, $Subdetail) {
            // oInit.InitSubTable(index, row, $Subdetail);
        }
    });
}

// 定义部分
// 定义一个对象
var TableInit = function () {
    var oTableInit = new Object();

    // 定义对象的方法，初始化Table
    oTableInit.Init = function () {
        // 先销毁表格
        $('#table').bootstrapTable('destroy').bootstrapTable({
            toolbar: '#toolbar',
            contentType:'application/x-www-form-urlencoded; charset=UTF-8',
            <%--url: '${ctx }/project/invoice/table',--%>
            data: ${fns:toJson(projectInvoice.invoiceItemList)},
            selectItemName: 'selectItemName',  // radio or checkbox 的字段名
            method: 'post',                      //请求方式（*）
            queryParams : oTableInit.queryParams,  //传递参数（*）
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
            rowStyle: function (row, index) { //设置行的特殊样式
                var strclass = "";
                if (index == 1) {
                    strclass = "danger";
                }
                return { classes: strclass };
//				return strclass;
            },
            columns: [{
                checkbox: true,
                visible: true                  //是否显示复选框
            }, {
                field: 'apply.id',
                title: '项目编号',
                titleTooltip: "tips",
                align: 'center',
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
                cellStyle: function(value, row, index) {
                    var classes = ['active', 'success', 'info', 'warning', 'danger'];
                    if (index % 2 === 0 && index / 2 < classes.length) {
                        return {
                            //classes: classes[index / 2]
                            css: {
//                                "background-color": "red",
                            }
                        };
                    }
                    return {};
                },
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
                field: 'settlement',
                title: '结算周期'
            }, {
                field: 'invoiceNo',
                title: '发票号'
            }, {
                field: 'remarks',
                title: '备注'
            }, {
                field: 'id',
                title: '操作',
                visible: false,
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
            responseHandler: function (res) { // 在ajax获取到数据，渲染表格之前，修改数据源
//                if(res.total > 0) {
//                    return {
//                        "rows": res.rows,
//                        "total": res.total
//                    }
//                } else {
//                    return {
//                        "rows": [],
//                        "total": 0
//                    }
//                }
                return res;
            },
            onLoadSuccess: function () { // 数据加载成功！
            },
            onLoadError: function () { // 数据加载失败！
            },
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
                InitSubTable(index, row, $detail);
            },
            onClickRow: function (row, $element, field) {
//              window.location.href = "/qStock/qProInfo/" + row.ProductId;
            }
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

    //初始化子表格(无线循环)
    oInit.InitSubTable = function (index, row, $detail) {
        var contract_id = row.contract_id;
        var cur_table = $detail.html('<table></table>').find('table');
        $(cur_table).bootstrapTable({
            url: '${ctx}/project/invoice/findVerList',
            method: 'get',
            queryParams: { contractId: contract_id },
            ajaxOptions: { contractId: contract_id },
            clickToSelect: true,
            detailView: false,//父子表
            uniqueId: "ID",
            pageSize: 10,
            pageList: [10, 25],
            columns: [{
                field: 'apply.id',
                title: '项目编号',
                titleTooltip: "tips",
                align: 'center',
                visible: false
            }, {
                field: 'apply.projectName',
                title: '项目名称',
                resizable: true,
                sortable: true
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
                field: 'clientName',
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
                field: 'settlement',
                title: '结算周期'
            }, {
                field: 'invoiceNo',
                title: '发票号'
            }, {
                field: 'remarks',
                title: '备注'
            }],
            //无线循环取子表，直到子表里面没有记录
            onExpandRow: function (index, row, $Subdetail) {
                // oInit.InitSubTable(index, row, $Subdetail);
            }
        });
    };

    oInit.Init = function () {
        // 监听table的事件，设置btn的状态
        $("#table").on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
            $("#btn_delete").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
            $("#btn_edit").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
            // $("#btn_resign").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
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

//        $("#btnSubmit").click(function () {
//            var oTable = new TableInit();
//            oTable.Init();
////            $("#table").bootstrapTable('refresh', oTable.queryParams);
//        });
    };
    return oInit;
}; // end var ButtonInit

// 全局函数-编辑业务逻辑
function editById(id) {
    alertx("暂未开放");
}

// 全局函数
function getSelectedIndexes() {
    var index = [];
    $('input[name="selectItemName"]:checked').each(function () {
        index.push($(this).data('index'));
    });
    return index;
//    alert('Checked row index: ' + index.join(', '));
}

// 给table绑定事件 - aka.onClickRow
/**
 * row: the record corresponding to the clicked row
 * $element: the tr element
 * field: the field name corresponding to the clicked cell
 */
$('#table').on('click-row.bs.table', function (row, $element, field) {
    // console.log("row=" + row);
//    $('.success').removeClass('success'); // 去除之前选中的行的选中样式
//    $(element).addClass('success'); // 添加当前选中的success样式用于区别
});
// 全局函数
function search() {
    // 正确的使用方法
    var opt = {
        url: 'doDynamicsList',
        silent: true,
        query:{
            'sd.userInfo.userName':searchForm.userName.value,
        }
    };
    // 需要先摧毁table
    $('#table').bootstrapTable('destroy').bootstrapTable('refresh',opt);
}

function submitG() {
    var jform = $("#inputForm");
    jform.validate();
    if (!jform.valid()) {
        jeesnsDialog.tips("请修改。");
        return;
    }
    var json = form2js($("#inputForm")[0]);
    var data = $("#table").bootstrapTable('getData');
    if (data === undefined || data.length == 0) {
        jeesnsDialog.tips("开票项不能为空。");
        return;
    }
    json.invoiceItemList = data;
    // console.log("form.json=" + JSON.stringify(json));
    jeesns.jeesnsAjax('${ctx}/project/invoice/saveAjax', 'POST', json);
    <%--jeesns.jeesnsAjax('${ctx}/project/invoice/test2', 'POST', json);--%>
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
            jeesnsDialog.open('${ctx}/project/invoice/addItemView',
                '增加开票项', '600px', '650px', function(data) {
                $('#table').bootstrapTable('append', data);
            });
        },

        query: function () {
            vm.reload();
        },
        add: function() {
            vm.title = "新增";
            vm.roleList = {};

            $('#table').bootstrapTable('append', vm.newItem);
            vm.newItem = {};
            // 方法内 'this' 指向 vm
//            this.message = 'hello';
        },
        getDept: function() {
            //加载部门树
            $.get(baseURL + "sys/dept/list", function(r){
                ztree = $.fn.zTree.init($("#deptTree"), setting, r);
                var node = ztree.getNodeByParam("deptId", vm.user.deptId);
                if(node != null){
                    ztree.selectNode(node);
                    vm.user.deptName = node.name;
                }
            })
        },
        update: function() {
            // 全局变量，用于给iframe的dialog传值，修改时用
            var indexes = getSelectedIndexes();
            if (!isArraySingle(indexes)) {
                jeesnsDialog.tips("只能选择一条数据");
                return;
            }
            row = getSelectedRow();
            rowIndex = indexes[0];

            jeesnsDialog.open('${ctx}/project/invoice/addItemView?id=' + row.id,
                '增加开票项', '600px', '650px', function(data) {
                    $('#table').bootstrapTable('updateRow', {index: rowIndex, row: data});
                });
        },
        del: function () {
            <%--var userIds = getSelectedRows('table');--%>
            <%--confirm('确定要删除选中的记录？', function(){--%>
                <%--jeesns.jeesnsAjax('${ctx}/sys/user/delete', 'POST', userIds, function(r){--%>
                    <%--vm.reload();--%>
                <%--});--%>
            <%--});--%>
        },
        saveOrUpdate: function () {
            var url = vm.user.userId == null ? "sys/user/save" : "sys/user/update";
            jeesns.jeesnsAjax(url, 'POST', vm.user, function(r){
                vm.reload();
            });
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级部门
                    vm.user.deptId = node[0].deptId;
                    vm.user.deptName = node[0].name;
                    layer.close(index);
                }
            });
        }
    }  // method end
});  // end vm

</script>
</body>
</html>