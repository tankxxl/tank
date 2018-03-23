// rgz init 
$(function () {

    // 验证值小数位数不能超过两位
    jQuery.validator.addMethod("decimal", function (value, element) {
        var decimal = /^-?\d+(\.\d{1,2})?$/;
        return this.optional(element) || (decimal.test(value));
    }, $.validator.format("小数位数不能超过两位!"));

    //自定义函数处理queryParams的批量增加 - 自动将form表单封装成json
    // 如：input控件的name='act.id'，此函数生成的json为:'act.id': '123'，不能满足ajax请求，RequestBody接收复杂对象的需求。
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
    
}); // init end

//jqGrid的配置信息
// $.jgrid.defaults.width = 1000;
// $.jgrid.defaults.responsive = true;
// $.jgrid.defaults.styleUI = 'Bootstrap';

//工具集合Tools
window.T = {};

// 获取请求参数
// 使用示例
// location.href = http://localhost/index.html?id=123
// T.p('id') --> 123;
var url = function(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null)return  unescape(r[2]); return null;
};
T.p = url;

//请求前缀
var baseURL = "/renren-fastplus/";

//登录token
var token = localStorage.getItem("token");
if(token == 'null'){
    parent.location.href = baseURL + 'login.html';
}

//jquery全局配置
/*
$.ajaxSetup({
	dataType: "json",
	cache: false,
    headers: {
        "token": token
    },
    // xhrFields: {
    //     withCredentials: true
    // },
    complete: function(xhr) {
        //token过期，则跳转到登录页面
        if(xhr.responseJSON.code == 401){
            parent.location.href = baseURL + 'login.html';
        }
    }
});
*/

//jqgrid全局配置
/*
$.extend($.jgrid.defaults, {
    ajaxGridOptions : {
        headers: {
            "token": token
        }
    }
});
*/

//权限判断
function hasPermission(permission) {
    if (window.parent.permissions.indexOf(permission) > -1) {
        return true;
    } else {
        return false;
    }
}

//重写alert
/*window.alert = function(msg, callback){
	parent.layer.alert(msg, function(index){
		parent.layer.close(index);
		if(typeof(callback) === "function"){
			callback("ok");
		}
	});
}

//重写confirm式样框
window.confirm = function(msg, callback){
	parent.layer.confirm(msg, {btn: ['确定','取消']},
	function(){//确定事件
		if(typeof(callback) === "function"){
			callback("ok");
		}
	});
}*/

//选择一条记录
// function getSelectedRow() {
//     // 元素id必须为table
//     var grid = $("#table");
//     var rowKey = grid.getGridParam("selrow");
//     if(!rowKey){
//     	alert("请选择一条记录");
//     	return ;
//     }
//
//     var selectedIDs = grid.getGridParam("selarrrow");
//     if(selectedIDs.length > 1){
//     	alert("只能选择一条记录");
//     	return ;
//     }
//
//     return selectedIDs[0];
// }
//
// //选择多条记录
// function getSelectedRows() {
//     var grid = $("#table");
//     var rowKey = grid.getGridParam("selrow");
//     if(!rowKey){
//     	alert("请选择一条记录");
//     	return ;
//     }
//
//     return grid.getGridParam("selarrrow");
// }

//JavaScript 构建一个 form
function MakeForm() {
    // 创建一个 form
    var form1 = document.createElement("form");

    form1.id = "form1";
    form1.name = "form1";

    // 添加到 body 中
    document.body.appendChild(form1);

    // 创建一个输入
    var input = document.createElement("input");

    // 设置相应参数
    input.type = "text";
    input.name = "value1";
    input.value = "1234567";

    // 将该输入框插入到 form 中
    form1.appendChild(input);

    // form 的提交方式
    form1.method = "POST";

    // form 提交路径
    form1.action = "/servlet/info";

    // 对该 form 执行提交
    form1.submit();

    // 提交完成后，再删除该 form
    document.body.removeChild(form1);
}

function post(url, params) {
    var tempForm = document.createElement("form");
    tempForm.action = url;
    tempForm.method = "post";
    tempForm.style.display = "none";
    for (var x in params) {
        var opt = document.createElement("textarea");
        opt.name = x;
        opt.value = params[x];
        // alert(opt.name)
        tempForm.appendChild(opt);
    }
    // 创建->提交->删除
    document.body.appendChild(tempForm);
    tempForm.submit();
    document.body.removeChild(tempForm);
}

// JQuery动态创建表单并提交
$('#btnRGZZZ').click(function(){
    // 取得要提交的参数
    var my_val = $.trim($('#ipt').val());
    // 取得要提交页面的URL
    var action = $(this).attr('href');
    // 创建Form
    var form = $('<form></form>');
    // 设置属性
    form.attr('action', action);
    form.attr('method', 'post');
    // form的target属性决定form在哪个页面提交
    // _self -> 当前页面 _blank -> 新页面
    form.attr('target', '_self');
    // 创建Input
    var my_input = $('<input type="text" name="my_name" />');
    my_input.attr('value', my_val);
    // 附加到Form
    form.append(my_input);
    // 提交表单
    form.submit();
    // 注意return false取消链接的默认动作
    return false;
});