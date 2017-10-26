$(function () { // 执行代码

    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();
}); // init end


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
                title: '客户名称'
            }, {
                field: 'customer.customerName',
                title: '开票内容'
            }, {
                field: 'category',
                title: '规格型号',
                formatter: function (value, row, index) {
                    // 通过formatter可以自定义列显示的内容
                    // value: 当前field的值，即id
                    // row: 当前行的数据
                    return value;
                // <%--${fns:getDictLabel(projectApplyExternal.procStatus, 'AuditStatus', '')};--%>
                //     return getDictLabel(${fns:toJson(fns:getDictList('pro_category'))}, value);
//					var a = '<a href="/home/edit?id=' + value + '">' + value + '</a>';
//					return a;
                }

            }, {
                field: 'updateDate',
                title: '数量',
                formatter: function (value, row, index) {
                    // 通过formatter可以自定义列显示的内容
                    // value: 当前field的值，即id
                    // row: 当前行的数据
                    return new Date(value).Format("yyyy-MM-dd");
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
                field: 'customer.customerName',
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





var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;

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
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.roleList = {};
            vm.user = {deptName:null, deptId:null, status:1, roleIdList:[]};

            //获取角色信息
            this.getRoleList();

            vm.getDept();
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