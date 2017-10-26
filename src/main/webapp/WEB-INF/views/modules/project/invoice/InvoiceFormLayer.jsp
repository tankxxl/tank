<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            // 初始化全局变量，修改表单使用
		    treeGetParam = "?prjId=${projectInvoice.apply.id}";

			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					var formData = $(form).serializeJsonObject();
					console.log("formData=" + JSON.stringify(formData));
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

        // 选择项目后触发事件
        function changeProject(projectId, idx) {
            // JavaScript全局变量，用于传递参数，新建表单使用。
		    treeGetParam = "?prjId=" + projectId;

            // 向后台获取项目信息，并将相关信息回显
            $.post('${ctx}/apply/external/projectApplyExternal/getAsJson',
                {id: projectId},
                function (apply) {

                $("#project_code").text(apply.projectCode);
                $("#customer_name").text(apply.customer.customerName);
                $("#customer_contact_name").text(apply.customerContact.contactName);
                $("#customer_contact_phone").text(apply.customerContact.phone);

                //清除合同相关的值
                $("#contract_amount").text("");
                $("#contract_gross_margin").text("");
                $("#contractItemId").val("");
                $("#contractItemName").val("");

                $("#contractId").val("");
            });
        }

        // 选择合同后触发事件
        function changedContract(itemId, idx) {
            $.post('${ctx}/project/contract/projectContract/getItemAsJson',
                {id: itemId}, function (item) {
                if (item) {
                    $('#contract_amount').text(item.contractAmount);
                    $('#contract_gross_margin').text(item.grossProfitMargin);
                    $('#contractId').val(item.contract.id);
                } else {
                    $('#contract_amount').text("");
                    $('#contract_gross_margin').text("");
                    $('#contractId').val("");
                }
            });
        }
	</script>

    <script src="${ctxStatic}/bootstrap-table/dist/extensions/mobile/bootstrap-table-mobile.js"></script>
    <script src="${ctxStatic}/bootstrap-table/dist/extensions/resizable/bootstrap-table-resizable.js"></script>
    <script src="${ctxStatic}/bootstrap-table/dist/extensions/editable/bootstrap-table-editable.js"></script>
    <script src="${ctxStatic}/vue/vue.min.js"></script>
    <script src="${ctxStatic}/modules/project/common.js"></script>
</head>
<body>
<div id="rrapp" v-cloak>
<ul class="nav nav-tabs">
    <c:if test="${ empty projectInvoice.act.taskId}">
        <li><a href="${ctx}/project/invoice/">开票列表</a></li>
    </c:if>
    <li class="active"><a href="${ctx}/project/invoice/form?id=${projectInvoice.id}">开票
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
    <form:hidden id="contractId" path="contract.id" />
    <sys:message content="${message}"/>
    <table class="table-form">
            <caption>项目信息</caption>
        <tr>
            <td  class="tit" >申请人</td>
            <td colspan="2">
                张三
            </td>

            <td  class="tit" >部门</td>
            <td colspan="2">
                人力资源部
            </td>
        </tr>

        <tr>
            <td class="tit">备注</td>
            <td colspan="5">
                <div style="white-space:nowrap;">
                    <form:textarea path="remarks" style="width:98%"  maxlength="255"
                                   placeholder=""/>
                </div>
            </td>
        </tr>

        <tr>
            <td class="tit" >文件附件</td>
            <td colspan="5">
                <form:hidden id="attachment" path="attachment" maxlength="20000"  />
                <sys:ckfinder input="attachment" type="files" uploadPath="/project/purchase" selectMultiple="true" />
            </td>
        </tr>
    </table>
    <br/><br/>

    <%-- 定义一系列工具栏 --%>
    <div id="toolbar" class="btn-group">
        <a href="${ctx}/project/invoice/addItem?id=${projectInvoice.id}" func="func()"
           width="800px" height="600px" target="_jeesnsOpen" title="添加Layer">
            <label class="btn btn-default">添加auto</label> </a>
        <button id="btn_add" type="button" class="btn btn-default">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
        </button>
        <button id="btn_edit" type="button" class="btn btn-default">
            <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
        </button>
        <button id="btn_delete" type="button" class="btn btn-default" disabled>
            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
        </button>

        <a class="btn btn-primary" @click="myAddClick()"><i class="fa fa-plus"></i>&nbsp;新增</a>
        <a class="btn btn-primary" @click="update()"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</a>
        <a class="btn btn-primary" @click="del()"><i class="fa fa-trash-o"></i>&nbsp;删除</a>
    </div>

    <table id="table" data-mobile-responsive="true"></table>
    <div class="form-actions">
        <shiro:hasPermission name="project:invoice:edit">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请" onclick="$('#flag').val('yes')"/>&nbsp;
        <c:if test="${not empty projectInvoice.id}">
            <input id="btnSubmit2" class="btn btn-inverse" type="submit" value="销毁申请" onclick="$('#flag').val('no')"/>&nbsp;
        </c:if>
        </shiro:hasPermission>

        <shiro:hasPermission name="apply:external:projectApplyExternal:super">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存并结束流程" onclick="$('#flag').val('saveFinishProcess')" data-toggle="tooltip" title="小心操作！"/>&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="只保存表单数据" onclick="$('#flag').val('saveOnly')" data-toggle="tooltip" title="管理员才能操作！"/>&nbsp;
        </shiro:hasPermission>

        <input id="btnSubmit" class="btn btn-primary" type="button"
               value="ajax保存" @click="submitx()" />&nbsp;

        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
    </div>

    <c:if test="${not empty projectInvoice.procInsId}">
        <act:histoicFlow procInsId="${projectInvoice.procInsId}" />
    </c:if>
</form:form>
</div> <%-- end v-cloak --%>

<%--<script src="${ctxStatic}/modules/project/invoice/InvoiceForm.js"></script>--%>

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

function func(data) {
    alertx("来自dialog=" + data);
}

// 定义部分
// 定义一个对象
var TableInit = function () {
    var oTableInit = new Object();

    // 定义对象的方法，初始化Table
    oTableInit.Init = function () {
        // 先销毁表格
        $('#table').bootstrapTable('destroy');

        $('#table').bootstrapTable({
//			resizable: true,
            toolbar: '#toolbar',
            contentType:'application/x-www-form-urlencoded; charset=UTF-8',
            <%--url: '${ctx }/project/invoice/table',--%>
            data: ${fns:toJson(projectInvoice.invoiceItemList)},
            method: 'post',                      //请求方式（*）

            queryParams : oTableInit.queryParams,  //传递参数（*）
            dataField : "list", //很重要，这是后端返回的实体数据！表示后端传递的对象数据，名字要与对象的名字相同。默认值：rows
            totalField: "total", // 很重要，跟后端返回的数据相关。默认值：total
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
            // singleSelect: true,				//复选框只能选择一条记录
            clickToSelect: false,                //是否启用点击选中行
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
                checkbox: true,
                visible: true                  //是否显示复选框
            }, {
                field: 'projectCode',
                title: '项目编号',
                titleTooltip: "tips",
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) {
                    if (value) {
                        return '<a href="${ctx}/apply/external/projectApplyExternal/form?id=' + row.id + '">' + value + '</a>';
                    } else {
                        return value;
                    }
                }
                // width: '180'
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
                                "background-color": "red",
                            }
                        };
                    }
                    return {};
                },
                formatter: function (value, row, index) { // 可以在此合成字段返回：row.field1 + row.field2
                    if (row.projectCode) {
                        return value;
                    } else {
                        return '<a href="${ctx}/apply/external/projectApplyExternal/form?id=' + row.id + '">' + value + '</a>';
                    }
                }
            }, {
                field: 'apply.projectCode',
                title: '客户名称'
            }, {
                field: 'goodsName',
                title: '开票内容'
            }, {
                field: 'category',
                title: '规格型号',
                formatter: function (value, row, index) {
                    return getDictLabel(${fns:toJson(fns:getDictList('pro_category'))}, value);
                }

            }, {
                field: 'updateDate',
                title: '数量',
                formatter: function (value, row, index) {
//                    return new Date(value).Format("yyyy-MM-dd");
                    return value;
                }

            }, {
                field: 'customer.customerName',
                title: '单位'
            }, {
                field: 'customer.customerName',
                title: '单价'
            }, {
                field: 'customer.customerName',
                title: '金额'
            }, {
                field: 'customer.customerName',
                title: '利润点'
            }, {
                field: 'customer.customerName',
                title: '结算周期'
            }, {
                field: 'customer.customerName',
                title: '合同号'
            }, {
                field: 'customer.customerName',
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
                    // return btnExport + btnView + btnTrace + btnDelete + btnEdit;
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
            onLoadSuccess: function () {
//            alert("数据加载成功！");
            },
            onLoadError: function () {
//                alert("数据加载失败！");
            },
            onDblClickRow: function (row, $element) {
                var id = row.id;
//			EditViewById(id, 'view');
            },
            onClickRow: function (row, $element) {
                console.log(row.projectName);
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

    oInit.Init = function () {
        //$("#btn_add").click(function () {
        //    $("#myModalLabel").text("新增");
        //    $("#myModal").find(".form-control").val("");
        //    $('#myModal').modal()

        //    postdata.DEPARTMENT_ID = "";
        //});

        //$("#btn_edit").click(function () {
        //    var arrselections = $("#tb_departments").bootstrapTable('getSelections');
        //    if (arrselections.length > 1) {
        //        toastr.warning('只能选择一行进行编辑');

        //        return;
        //    }
        //    if (arrselections.length <= 0) {
        //        toastr.warning('请选择有效数据');

        //        return;
        //    }
        //    $("#myModalLabel").text("编辑");
        //    $("#txt_departmentname").val(arrselections[0].DEPARTMENT_NAME);
        //    $("#txt_parentdepartment").val(arrselections[0].PARENT_ID);
        //    $("#txt_departmentlevel").val(arrselections[0].DEPARTMENT_LEVEL);
        //    $("#txt_statu").val(arrselections[0].STATUS);

        //    postdata.DEPARTMENT_ID = arrselections[0].DEPARTMENT_ID;
        //    $('#myModal').modal();
        //});

        // 监听table的事件，设置btn的状态
        $("#table").on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
            $("#btn_delete").prop('disabled', !$("#table").bootstrapTable('getSelections').length);
        });

        $("#btn_delete").click(function () {
//		    var arrselections = $("#table").bootstrapTable('getSelections'); // 取出row数组[row1, row2]
//            var ids = getIdSelections();  // 'id1,id2,id3'
//		    if (arrselections.length <= 0) {
//		        toastr.warning('请选择有效数据');
//		        return;
//		    }

            var ids = $.map($("#table").bootstrapTable('getSelections'), function(row) {
                return row.id;
            });
            // 前台删除
            $('#table').bootstrapTable('remove', {
                field: 'id',
                values: ids
            });
            // 设置btn_del的状态
            $("#btn_delete").prop('disabled', true);
//
//		    Ewin.confirm({ message: "确认要删除选择的数据吗？" }).on(function (e) {
//		        if (!e) {
//		            return;
//		        }
//		        $.ajax({
//		            type: "post",
//		            url: "/Home/Delete",
//		            data: { "": JSON.stringify(arrselections) },
//		            success: function (data, status) {
//		                if (status == "success") {
//		                    toastr.success('提交数据成功');
//		                    $("#tb_departments").bootstrapTable('refresh');
//		                }
//		            },
//		            error: function () {
//		                toastr.error('Error');
//		            },
//		            complete: function () {
//		            }
//		        });
//		    });
        });

        //$("#btn_submit").click(function () {
        //    postdata.DEPARTMENT_NAME = $("#txt_departmentname").val();
        //    postdata.PARENT_ID = $("#txt_parentdepartment").val();
        //    $.ajax({
        //        type: "post",
        //        url: "/Home/GetEdit",
        //        data: { "": JSON.stringify(postdata) },
        //        success: function (data, status) {
        //            if (status == "success") {
        //                toastr.success('提交数据成功');
        //                $("#tb_departments").bootstrapTable('refresh');
        //            }
        //        }
        //    });
        //});

        //$("#btn_query").click(function () {
        //    $("#tb_departments").bootstrapTable('refresh');
        //});

        $("#btnSubmit").click(function () {
            var oTable = new TableInit();
            oTable.Init();
//            $("#table").bootstrapTable('refresh', oTable.queryParams);
        });

    };
    return oInit;
}; // end var ButtonInit


// 全局函数，Email字段(column)格式化
function emailFormatter(value, row, index) {
    return "<a href='mailto:" + value + "' title='单击打开连接'>" + value + "</a>";
}
// 全局函数
function getTableSelect() {
    var rows = $('#table').bootstrapTable('getSelections');
    if (rows.length > 0) {
        ID = rows[0].id;
    }
    alertx(rows);
}
// 全局函数
function getIdSelections() {
    return $.map( $('#table').bootstrapTable('getSelections'), function (row) {
        return row.id
    });
}
// 全局函数
function Delete() {
    var ids = ""; // 得到用户选择的数据的ID
    var rows = $("#table").bootstrapTable('getSelections');
    for (var i = 0; i < rows.length; i++) {
        ids += rows[i].id + ',';
    }
    ids = ids.substring(0, ids.length - 1);
//		DeleteByIds(ids);
}
// 给table绑定事件 - aka.onClickRow
/**
 * row: the record corresponding to the clicked row
 * $element: the tr element
 * field: the field name corresponding to the clicked cell
 */
$('#table').on('click-row.bs.table', function (row, $element, field) {
    console.log("row=" + row);
    console.log("$element=" + $element);
    console.log("field=" + field);
    $('.success').removeClass('success'); // 去除之前选中的行的选中样式
//    $(element).addClass('success'); // 添加当前选中的success样式用于区别
});
// 全局函数
function getSelectedRow() {
    var index = $('#table').find('tr.success').data('index'); // 获得选中的行
    return $('#table').bootstrapTable('getData')[index]; // 返回选中行所有数据
}
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

// 全局变量，定义vm变量，
// Vue三部分：el、data、methods
var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            username: null
        },
        showList: true,
        title:null,
        roleList:{},
        newItem: {},
        user:{
            status:1,
            deptId:null,
            deptName:null,
            roleIdList:[]
        }
    }, // data end
    methods: {
        submitx: function () {
            $('#flag').val('saveOnly');
            var json = $("#inputForm").serializeJsonObject();
            var seri = $("#inputForm").serialize();
            var data = $("#table").bootstrapTable('getData');
            var myData = [];
            for (var i = 0; i < data.length; i++) {
                myData.push({'apply.id': data[i].apply.id,
                             'apply.projectName': data[i].apply.projectName});
            }
//            json.invoiceItemList = data;
            json.invoiceItemList = myData;
            console.log("form.json=" + JSON.stringify(json));
            console.log("form.seri=" + JSON.stringify(seri));
            console.log("table.data.json=" + JSON.stringify(data));
            console.log("table.myData.json=" + JSON.stringify(myData));
//            $("#inputForm").submit();
            jeesns.jeesnsAjax('${ctx}/project/invoice/save', 'POST', json);
        },
        myAddClick: function(){
            jeesnsDialog.open('${ctx}/project/invoice/addItem?id=${projectInvoice.id}',
                '增加开票项', '600px', '600px', function(data) {
                $('#table').bootstrapTable('append', data);
                alert(JSON.stringify(data));
            });

        },


        query: function () {
            vm.reload();
        },
        add: function() {
            vm.showList = false;
            vm.title = "新增";
            vm.roleList = {};
            vm.user = {deptName:null, deptId:null, status:1, roleIdList:[]};

            $('#table').bootstrapTable('append', vm.newItem);
            vm.newItem = {};
            //获取角色信息
//            this.getRoleList();

//            vm.getDept();
        },
        getDept: function(){
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
        update: function () {
            var userId = getSelectedRow();
            if(userId == null){
                return ;
            }

            vm.showList = false;
            vm.title = "修改";

            vm.getUser(userId);
            //获取角色信息
            this.getRoleList();
        },
        del: function () {
            var userIds = getSelectedRows();
            if(userIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/delete",
                    contentType: "application/json",
                    data: JSON.stringify(userIds),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function () {
            var url = vm.user.userId == null ? "sys/user/save" : "sys/user/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.user),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        getUser: function(userId){
            $.get(baseURL + "sys/user/info/"+userId, function(r){
                vm.user = r.user;
                vm.user.password = null;

                vm.getDept();
            });
        },
        getRoleList: function(){
            $.get(baseURL + "sys/role/select", function(r){
                vm.roleList = r.list;
            });
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
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
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'username': vm.q.username},
                page:page
            }).trigger("reloadGrid");
        }
    }  // method end
});  // end vm


</script>
</body>
</html>