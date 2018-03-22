<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开票管理</title>
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
	</script>

	<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/mobile/bootstrap-table-mobile.js"></script>
	<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/resizable/bootstrap-table-resizable.js"></script>
	<script src="${ctxStatic}/bootstrap-table-1.11.1/dist/extensions/editable/bootstrap-table-editable.js"></script>

</head>
<%-- 发票列表 --%>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/project/invoiceItem/">开票列表</a></li>
		<%--<shiro:hasPermission name="project:invoice:edit"><li><a href="${ctx}/project/invoice/form">开票添加</a></li></shiro:hasPermission>--%>
	</ul>
<form:form id="searchForm"
		   modelAttribute="projectInvoiceItem"
		   action="${ctx}/project/invoiceItem/"
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
<div id="toolbar" class="btn-group">
	<button id="btn_resign" type="button" class="btn btn-default">
		<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>重开
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
			$('#table').bootstrapTable('destroy').bootstrapTable({
//			resizable: true,
			toolbar: '#toolbar',
			url: '${ctx }/project/invoiceItem/table',
			method: 'post',                      //请求方式（*）
			contentType: "application/x-www-form-urlencoded",
			queryParams : oTableInit.queryParams,  //传递参数（*）
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
			showColumns: true,                  //是否显示所有的列（选择显示的列）
			showRefresh: true,                  //是否显示刷新按钮
            showFullscreen: true,
//            singleSelect: true,				//复选框只能选择一条记录
			clickToSelect: true,                //是否启用点击选中行
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
                field: 'contract.clientName',
                title: '客户名称'
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
                field: 'invoiceNo',
                title: '发票号',
                formatter: function (value, row, index) {
                    return '<a href="${ctx}/project/contract/projectContract/form?id=' + row.contract.id + '">' + value + '</a>';
				}
            }, {
                field: 'returnAmount',
                title: '回款金额'
            }, {
				field: 'saler.name',
				title: '销售人员'
			}, {
				field: 'updateDate',
				title: '更新时间',
				formatter: function (value, row, index) {
					return new Date(value).Format("yyyy-MM-dd");
				}
			}, {
				field: 'procStatus',
				title: '审批状态',
				formatter: function (value, row, index) {
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
					return btnDelete + btnEdit;
				}
			} ],
			responseHandler: function (res) { // 在ajax获取到数据，渲染表格之前，修改数据源
				return res;
			},
				onLoadSuccess: function() {

                },


			onLoadError: function () {
				// alert("数据加载失败！");
			},
			onDblClickRow: function (row, $element) {
				var id = row.id;
//			EditViewById(id, 'view');
			},
			onClickRow: function (row, $element) {
				console.log(row);
//                window.location.href = "/qStock/qProInfo/" + row.ProductId;
			}
		}); // 定义table
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
			//});
			//$("#btn_edit").click(function () {
			//    var arrselections = $("#tb_departments").bootstrapTable('getSelections');
			//    if (arrselections.length > 1) {
			//        toastr.warning('只能选择一行进行编辑');
			//        return;
			//    }
			//    $("#myModalLabel").text("编辑");
			//    $("#txt_departmentname").val(arrselections[0].DEPARTMENT_NAME);
			//    postdata.DEPARTMENT_ID = arrselections[0].DEPARTMENT_ID;
			//    $('#myModal').modal();
			//});
			$("#btn_delete").click(function () {
//		    var arrselections = $("#table").bootstrapTable('getSelections'); // 取出row数组[row1, row2]
				var ids = getIdSelections();  // 'id1,id2,id3'
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
//		            error: function () { },
//		            complete: function () { }
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
			//        error: function () { },
			//        complete: function () { }
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

            $('#table').on('post-body.bs.table', function (e, row, element) {
                // alert("post-body.bs.table");
                <%--include('qq', '${ctxStatic}/common/', ['jeesns.js']);--%>
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