<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部立项申请管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#contentTable").find("input[export]").each(function(){
				$(this).click(function(){
					var proId =$(this).attr("proId");
					top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/apply/external/projectApplyExternal/export?id="+proId);
							$("#searchForm").submit();
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			});
			
		});

		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>

	<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/mobile/bootstrap-table-mobile.js"></script>
	<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/resizable/bootstrap-table-resizable.js"></script>
	<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/editable/bootstrap-table-editable.js"></script>

</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/apply/external/projectApplyExternal/">外部立项申请列表</a></li>
	<shiro:hasPermission name="apply:external:projectApplyExternal:edit">
		<li><a href="${ctx}/apply/external/projectApplyExternal/form">外部立项申请添加</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="projectApplyExternal" action="${ctx}/apply/external/projectApplyExternal/"
		   htmlEscape="false" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<ul class="ul-form">
		<li><label>项目编码：</label>
			<form:input path="projectCode" maxlength="64" class="input-medium"/>
		</li>
		<li><label>项目名称：</label>
			<form:input path="projectName" maxlength="64" class="input-medium"/>
		</li>
		<li><label>销售人员：</label>
			<sys:treeselect id="saler" name="saler.id"
				value="${projectApplyExternal.saler.id}"
				labelName="saler.name" labelValue="${projectApplyExternal.saler.name}"
				title="用户" url="/sys/office/treeData?type=3" cssClass="input-small"
				allowClear="true" notAllowSelectParent="true"/>
		</li>
		<li><label>客户：</label>
			<sys:treeselect id="customer" name="customer.id" value="${projectApplyExternal.customer.id}"
				labelName="customer.customerName" labelValue="${projectApplyExternal.customer.customerName}"
				title="客户" url="/customer/customer/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
		</li>
		<li><label>项目类别：</label>
			<form:select path="category" class="input-medium">
				<form:option value="" label=""/>
				<form:options items="${fns:getDictList('pro_category')}" itemLabel="label" itemValue="value"/>
			</form:select>
		</li>
		<li><label>审批状态：</label>
			<form:select path="procStatus" class="input-medium">
				<form:option value="" label=""/>
				<form:options items="${fns:getDictList('AuditStatus')}" itemLabel="label" itemValue="value"/>
			</form:select>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/></li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>

<%-- 定义一系列工具栏 --%>

<div id="toolbar" class="btn-group">
	<button id="btn_add" type="button" class="btn btn-default">
		<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
	</button>
	<button id="btn_edit" type="button" class="btn btn-default">
		<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
	</button>
	<button id="btn_delete" type="button" class="btn btn-default">
		<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
	</button>
</div>
<table id="table" data-mobile-responsive="true"></table>
<script>
// 执行代码
$(function () {
    //自定义函数处理queryParams的批量增加
    $.fn.serializeJsonObject = function () {
        var json = {};
        var form = this.serializeArray();
        $.each(form, function () {
            if (json[this.name]) {
                if (!json[this.name].push) {
                    json[this.name] = [json[this.name]];
                }
                json[this.name].push();
            } else {
                json[this.name] = this.value || '';
            }
        });
        return json;
    }

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

	// 初始化Table
	oTableInit.Init = function () {
	    // 先销毁表格
		$('#table').bootstrapTable('destroy');

		$('#table').bootstrapTable({
//			resizable: true,
			toolbar: '#toolbar',
			url: '${ctx }/apply/external/projectApplyExternal/table',
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
//            singleSelect: true,				//复选框只能选择一条记录
			clickToSelect: true,                //是否启用点击选中行
			uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
			showToggle: true,                   //是否显示详细视图和列表视图的切换按钮
			cardView: false,                    //是否显示详细视图
			detailView: false,                  //是否显示父子表
			rowStyle: function (row, index) { //设置行的特殊样式
				//这里有5个取值颜色['active', 'success', 'info', 'warning', 'danger'];
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
				title: '销售人员'
			}, {
				field: 'customer.customerName',
				title: '客户'
			}, {
				field: 'category',
				title: '项目类别',
				formatter: function (value, row, index) {
					// 通过formatter可以自定义列显示的内容
					// value: 当前field的值，即id
					// row: 当前行的数据
                    <%--${fns:getDictLabel(projectApplyExternal.procStatus, 'AuditStatus', '')};--%>
                    return getDictLabel(${fns:toJson(fns:getDictList('pro_category'))}, value);
//					var a = '<a href="/home/edit?id=' + value + '">' + value + '</a>';
//					return a;
				}

			}, {
                field: 'updateDate',
                title: '更新时间',
                formatter: function (value, row, index) {
                    // 通过formatter可以自定义列显示的内容
                    // value: 当前field的值，即id
                    // row: 当前行的数据
					return new Date(value).Format("yyyy-MM-dd");
                }

            }, {
                field: 'procStatus',
                title: '审批状态',
                formatter: function (value, row, index) {
                    <%--${fns:getDictLabel(projectApplyExternal.procStatus, 'AuditStatus', '')};--%>
                    return getDictLabel(${fns:toJson(fns:getDictList('AuditStatus'))}, value);
                },
                cellStyle: function (value, row, index) {
					return "text-warning";
                }

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
				alert("数据加载失败！");
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
		//    postdata.DEPARTMENT_LEVEL = $("#txt_departmentlevel").val();
		//    postdata.STATUS = $("#txt_statu").val();
		//    $.ajax({
		//        type: "post",
		//        url: "/Home/GetEdit",
		//        data: { "": JSON.stringify(postdata) },
		//        success: function (data, status) {
		//            if (status == "success") {
		//                toastr.success('提交数据成功');
		//                $("#tb_departments").bootstrapTable('refresh');
		//            }
		//        },
		//        error: function () {
		//            toastr.error('Error');
		//        },
		//        complete: function () {

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
</script>

</body>
</html>