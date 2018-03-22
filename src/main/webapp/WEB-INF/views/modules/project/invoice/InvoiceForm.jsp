<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            // 验证值小数位数不能超过两位
            jQuery.validator.addMethod("decimal", function (value, element) {
                var decimal = /^-?\d+(\.\d{1,2})?$/;
                return this.optional(element) || (decimal.test(value));
            }, $.validator.format("小数位数不能超过两位!"));

            // 初始化全局变量，修改表单使用
		    treeGetParam = "?prjId=${projectInvoice.apply.id}";

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
        <a href="${ctx}/project/invoice/add" callback="func()"
           width="800px" height="600px" target="_jeesnsOpen" title="添加">
            <label class="btn btn-default">添加</label> </a>
        <%--<button id="btn_add" type="button" class="btn btn-default">--%>
            <%--<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增--%>
        <%--</button>--%>
        <button id="btn_edit" type="button" class="btn btn-default">
            <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
        </button>
        <button id="btn_delete" type="button" class="btn btn-default">
            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
        </button>

        <!-- Button trigger modal -->
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal3">
            Launch modal
        </button>

        <a class="btn btn-primary" @click="add()"><i class="fa fa-plus"></i>&nbsp;新增</a>
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

        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.back()"/>
    </div>

    <c:if test="${not empty projectInvoice.procInsId}">
        <act:histoicFlow procInsId="${projectInvoice.procInsId}" />
    </c:if>
</form:form>

<!-- Modal -->
<div class="modal fade" id="exampleModal3" tabindex="-1"
     role="dialog" aria-labelledby="exampleModal3Label" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModal3Label">Modal title</h5>
                <%--<button type="button" class="close" data-dismiss="modal" aria-label="Close">--%>
                    <%--<span aria-hidden="true">×</span>--%>
                <%--</button>--%>
            </div>
            <div class="modal-body">
                <%--修改的时候如何把值写上去，要在方法里用jquery一个一个写上去？？--%>
                <form role="form">
                    <div class="form-group">
                        <label for="apply">项目：</label>
                        <sys:treeselect id="apply" name="apply.id"
                                        value="${projectInvoice.apply.id}"
                                        labelName="apply.projectName"
                                        labelValue="${projectInvoice.apply.projectName}"
                                        title="项目名称"
                                        url="/apply/external/projectApplyExternal/treeData4LargerMainStage?proMainStage=11"
                                        cssClass="required"  allowClear="true" notAllowSelectParent="true"
                                        customClick="changeProject"/>


                        <%--<input type="text" id="username" class="form-control" placeholder="请输入用户名" v-model="newItem.projectName" />--%>
                    </div>
                    <div class="form-group">
                        <label for="age">合同：</label>
                        <input type="text" id="age" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>

                    <div class="form-group">
                        <label for="age">客户名称：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>

                    <div class="form-group">
                        <label for="age">开票内容：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">规格型号：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">数量：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">单位：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">单价：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">金额：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">利润点：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">结算周期：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">发票号：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <div class="form-group">
                        <label for="age">备注：</label>
                        <input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />
                    </div>
                    <%--<div class="form-group">--%>
                        <%--<label for="age">年龄：</label>--%>
                        <%--<input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />--%>
                    <%--</div>--%>
                    <%--<div class="form-group">--%>
                        <%--<label for="age">年龄：</label>--%>
                        <%--<input type="text" class="form-control" placeholder="请输入年龄" v-model="newItem.projectCode" />--%>
                    <%--</div>--%>

                    <!-- <div class="form-group">
                      <input type="button" value="添加" class="btn btn-primary" v-on:click="add()" />
                      <input type="reset" value="重置" class="btn btn-danger" />
                    </div> -->
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" @click="add()" class="btn btn-primary" data-dismiss="modal">Save changes</button>
            </div>
        </div>
    </div>
</div> <%-- end modal dialog --%>
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
            url: '${ctx }/project/invoice/table',
            method: 'post',                      //请求方式（*）
            contentType: "application/x-www-form-urlencoded",
            queryParams : oTableInit.queryParams,  //传递参数（*）
            dataField : "list", //很重要，这是后端返回的实体数据！表示后端传递的对象数据，名字要与对象的名字相同。
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
                field: 'projectName',
                title: '项目名称',
                resizable: true,
                sortable: true,
                formatter: function (value, row, index) {
                    if (row.projectCode) {
                        return value;
                    } else {
                        return '<a href="${ctx}/apply/external/projectApplyExternal/form?id=' + row.id + '">' + value + '</a>';
                    }
                }
            }, {
                field: 'saler.name',
                title: '客户名称'
            }, {
                field: 'customer.customerName',
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
                    // 通过formatter可以自定义列显示的内容
                    // value: 当前field的值，即id
                    // row: 当前行的数据
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

                    <shiro:hasPermission name="apply:external:projectApplyExternal:edit">
                    if (row.procStatus == '2') {
                        btnDelete = '<a href="${ctx}/apply/external/projectApplyExternal/delete?id=' + row.id + '" onclick="return confirmx("确认要删除该外部立项申请吗？", this.href)">删除</a>&nbsp';
                    } else {
                        btnTrace = '<a class="trace" target="_blank" procInsId="' + row.procInsId + '" href="${ctx}/act/task/trace1?procInsId=' + row.procInsId + '">跟踪</a>&nbsp';
                    }
                    </shiro:hasPermission>

                    <shiro:hasPermission name="apply:external:projectApplyExternal:modify">
                    btnEdit = '<a href="${ctx}/apply/external/projectApplyExternal/modify?id=' + row.id + '">修改</a>&nbsp';
                    </shiro:hasPermission>

                    // return btnExport + btnView + btnTrace + btnDelete + btnEdit;
                    return btnExport + btnTrace + btnDelete + btnEdit;
                }
            } ],
            responseHandler: function (res) {
                // 在ajax获取到数据，渲染表格之前，修改数据源
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
//                window.location.href = "/qStock/qProInfo/" + row.ProductId;
            }
        });
    }; // end Init()

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        var temp = $("#searchForm").serializeJsonObject();
        temp["pageSize"] = params.limit;                        //页面大小
        temp["pageNo"] = (params.offset / params.limit) + 1;  //页码
//        temp["sort"] = params.sort;                         //排序列名
        temp["orderBy"] = params.sort;                         //排序列名
        temp["sortOrder"] = params.order;                   //排位命令（desc，asc）
        //特殊格式的条件处理
//        temp["WHC_Age"] = $("#WHC_Age").val() + "~" + $("#WHC_Age2").val();
        return temp;

//		var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
//            pageNo: (params.offset / params.limit) + 1,   //页码，传到后台分页
//            pageSize: params.limit,                       //页面大小
//            sort: params.sort,      //排序列名，传到后台排序
//            sortOrder: params.order //排位命令（desc，asc），传到后台排序
//		};
//		return temp;
    }; // end queryParams()
    return oTableInit;
}; // end var TableInit


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

        $("#btn_delete").click(function () {
//		    var arrselections = $("#table").bootstrapTable('getSelections'); // 取出row数组[row1, row2]
            var ids = getIdSelections();  // 'id1,id2,id3'

//		    if (arrselections.length <= 0) {
//		        toastr.warning('请选择有效数据');
//		        return;
//		    }
            // 前台删除
            $('#table').bootstrapTable('remove', {
                field: 'id',
                values: ids
            });
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
//
//		            }
//
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


//Email字段(column)格式化
function emailFormatter(value, row, index) {
    return "<a href='mailto:" + value + "' title='单击打开连接'>" + value + "</a>";
}

function getTableSelect() {
    var rows = $('#table').bootstrapTable('getSelections');
    if (rows.length > 0) {
        ID = rows[0].id;
    }
    alertx(ID);
}

function getIdSelections() {
    return $.map( $('#table').bootstrapTable('getSelections'), function (row) {
        return row.id
    });
}

function Delete() {
    var ids = ""; // 得到用户选择的数据的ID
    var rows = $("#table").bootstrapTable('getSelections');
    for (var i = 0; i < rows.length; i++) {
        ids += rows[i].id + ',';
    }
    ids = ids.substring(0, ids.length - 1);
//		DeleteByIds(ids);
}

$('#table').on('click-row.bs.table', function (e, row, element) {
    $('.success').removeClass('success'); // 去除之前选中的行的选中样式
    $(element).addClass('success'); // 添加当前选中的success样式用于区别
});

function getSelectedRow() {
    var index = $('#table').find('tr.success').data('index'); // 获得选中的行
    return $('#table').bootstrapTable('getData')[index]; // 返回选中行所有数据
}

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
        query: function () {
            vm.reload();
        },
        add: function() {
            vm.showList = false;
            vm.title = "新增";
            vm.roleList = {};
            vm.user = {deptName:null, deptId:null, status:1, roleIdList:[]};

//            vm.newItem = {
//                projectCode:"rgz" + r,
//                projectName: "18rgz" + r,
//                remarks: "rgz " + r
//            };
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
});
</script>
</body>
</html>