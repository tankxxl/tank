/**
 * Created by zchuanzhao on 2016/10/13.
 */
$(function () {
    // 没有传form参数，所以是绑定事件=配置界面事件
    jeesns.submitForm();
    // 配置界面事件，根据界面控件的参数
    jeesns.jeesnsLink();
});

//
var jeesns = {
    reg_rule : {
        'selected'   :    /.+/,
        'require'    :    /.+/,
        'email'      :    /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
        'url'        :    /^http|https:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/,
        'currency'   :    /^\d+(\.\d+)?$/,
        'number'     :    /^\d+$/,
        'zip'        :    /^\d{6}$/,
        'integer'    :    /^[-\+]?\d+$/,
        'double'     :    /^[-\+]?\d+(\.\d+)?$/,
        'letter'     :    /^[A-Za-z]+$/
    },
    reg_rule_msg : {
        'selected'   :    "必须选择",
        'require'    :    "不能为空",
        'email'      :    "格式不正确",
        'url'        :    "格式不正确",
        'currency'   :    /^\d+(\.\d+)?$/,
        'number'     :    /^\d+$/,
        'zip'        :    /^\d{6}$/,
        'integer'    :    "必须为整数",
        'double'     :    "必须为数字",
        'letter'     :    "必须为字母"
    },

    // 生成请求参数
    getOptions : function(){
        var index;
        // 定义options对象
        var options = {
            dataType : 'json',
            timeout : 20000,
            beforeSubmit : function (){
                // 选择type="submit"的<input>和<button>元素
                $(":submit").attr("disabled","disabled");
                // form.find('.jeesns-submit').attr("disabled","disabled");
                index = jeesnsDialog.loading();
            },
            error:function(res){
                jeesnsDialog.close(index);
                $(":submit").removeAttr("disabled");
                // form.find('.jeesns-submit').removeAttr("disabled");
                jeesnsDialog.tips('请求失败 ！');
            },
            success:function(res){
                jeesnsDialog.close(index);
                if(res.code==0){
                    $(":submit").removeAttr("disabled");
                    jeesnsDialog.successTips(res.message);
                }else if(res.code==1){
                    jeesnsDialog.loading();
                    jeesnsDialog.successTips(res.message);
                    // 刷新？
                    setTimeout(function(){
                        window.location.href=window.location.href;
                    },3000);
                }else if(res.code==2){
                    jeesnsDialog.loading();
                    jeesnsDialog.successTips(res.message);
                    // 跳转到指定的url
                    setTimeout(function(){
                        window.location.href=res.url;
                    },3000);
                }else if(res.code==3){
                    parent.window.location.href=parent.window.location.href;
                }else if(res.code==-1){
                    $(":submit").removeAttr("disabled");
                    jeesnsDialog.errorTips(res.message);
                }else{
                    $(":submit").removeAttr("disabled");
                    jeesnsDialog.tips(res.message);
                }
                // $(":submit").removeAttr("disabled");
                // form.find('.jeesns-submit').removeAttr("disabled");
            }
        };
        // 返回options对象
        return options;
    },
    // 提交form
    submitForm : function(form){
        if(arguments[0]){//如果传入了form，马上对form进行ajax提交
            var form = typeof(form)=='object' ? $(form) : $('#'+form);
            if(jeesns.checkForm(form)==false) return false;
            var options = getOptions();
            form.ajaxSubmit(options);
        }else{//否则，对标志有class="jeesns_form"的表单进行ajax提交的绑定操作
            $('.jeesns_form').bind('submit',function(){
                var form = $(this);
                if(jeesns.checkForm(form)==false) return false;
                var options = jeesns.getOptions();
                form.ajaxSubmit(options);
                return false;
            });
        }
    },
    // 检查UI
    checkForm : function(form){
        var check = true;
        form.find("input,textarea,select,redio,checkbox").each(function(){
            var val = $.trim($(this).val());
            var rule = $(this).attr('data-rule');
            var type = $(this).attr('data-type');
            if(type != "" && type != undefined){
                var alt = $(this).attr('alt');
                if(alt == "" || alt == undefined){
                    alt = $(this).attr("placeholder");
                }
                if(alt == undefined){
                    alt = "";
                }
                if(rule != "" && rule != undefined){
                    if(rule.indexOf("equal") != -1){
                        var equalid = rule.substring(rule.indexOf("[")+1,rule.indexOf("]"));
                        var equalValue = $("#"+equalid).val();
                        if(val != equalValue){
                            jeesnsDialog.errorTips(alt);
                            $(this).focus();
                            check = false;
                            return false;
                        }
                    }
                }

                if(type.indexOf(",") != -1){
                    var types = type.split(",");
                    for (var i=0;i<types.length;i++){
                        type = types[i];
                        if(!jeesns.reg_rule[type].test(val)){
                            jeesnsDialog.errorTips(alt+jeesns.reg_rule_msg[type]);
                            $(this).focus();
                            check = false;
                            return false;
                        }
                    }
                }else{
                    if(!jeesns.reg_rule[type].test(val)){
                        jeesnsDialog.errorTips(alt+jeesns.reg_rule_msg[type]);
                        $(this).focus();
                        check = false;
                        return false;
                    }
                }
            }
        });
        return check;
    },

    // 绑定UI事件
    jeesnsLink : function (){
        // 对超连接的click进行绑定，绑定的函数在将来执行，在等事件来了才执行
        $('a[target="_jeesnsLink"]').on('click',function() {
            var url = $(this).attr('href');
            var title = $(this).attr('confirm');
            if (title) {
                jeesnsDialog.confirm(title, function () {
                    jeesns.jeesnsAjax(url,"GET",null);
                });
            }else {
                jeesns.jeesnsAjax(url,"GET",null);
            }
            return false;
        });

        $('a[target="_jeesnsOpen"]').on('click',function() {
            var url = $(this).attr('href');
            var title = $(this).attr('title');
            var width = $(this).attr('width');
            var height = $(this).attr('height');
            var callback = $(this).attr('callback');
            // console.log("callback=" + callback);
            if(width == undefined || width == ""){
                width = "500px";
            }
            if(height == undefined || height == ""){
                height = "300px";
            }
            jeesnsDialog.open(url, title, width, height, callback);
            return false;
        });
    },
    // 执行ajax请求
    jeesnsAjax : function(url,type, data, callback){
        var index;
        $.ajax({
            url: url,
            type: type,
            data: JSON.stringify(data),
            cache: false,
            contentType: 'application/json;charset=UTF-8', // json格式提交，默认是form格式
            // contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            dataType: 'json', // 表示返回值类型，非必须
            timeout: 20000,
            beforeSend: function() { //
                index = jeesnsDialog.loading();
            },
            error: function() { // 非业务
                jeesnsDialog.close(index);
                jeesnsDialog.errorTips("请求失败");
            },
            success: function(res) { // busi here
                jeesnsDialog.close(index);
                if (res.code == 0) {
                    jeesnsDialog.successTips(res.message);
                } else if(res.code == -1) {
                    jeesnsDialog.errorTips(res.message);
                } else if(res.code==1) {
                    // jeesnsDialog.loading();
                    jeesnsDialog.successTips(res.message);
                    setTimeout(function() {
                        window.location.href = window.location.href;
                    }, 10);
                } else if(res.code==2) {
                    // jeesnsDialog.loading();
                    jeesnsDialog.successTips(res.message);
                    setTimeout(function() {
                        window.location.href = res.url;
                    },10);
                } else if(res.code==3) {
                    parent.window.location.href = parent.window.location.href;
                } else if (res.code == 4) { // 直接打开新窗口
                    jeesnsDialog.successTips(res.message);
                    window.open(res.url);
                } else {
                    jeesnsDialog.tips(res.message);
                }
                // 最后把返回数据回调出去
                if (callback != undefined) {
                    callback(res);
                }
            }
        });
    }
};
// 全局变量jeesnsDialog对象
var jeesnsDialog = {
    loading : function () {
        //加载层
        return parent.layer.load(0);
    },

    close : function (index) {
        parent.layer.close(index);
    },

    closeAll : function () {
        parent.layer.closeAll();
    },

    alert : function(msg) {
        parent.layer.alert(msg);
    },

    confirm : function(msg,confirmFun) {
        parent.layer.confirm(msg, function(){
            confirmFun();
        }, function(){

        });
    },

    tips : function(msg){
        parent.layer.msg(msg);
    },

    tips : function(msg,type){
        if(type == "error"){
            parent.layer.msg(msg, {icon: 5});
        }else if(type == "success"){
            parent.layer.msg(msg, {icon: 6});
        }else{
            parent.layer.msg(msg);
        }
    },

    errorTips : function(msg) {
        jeesnsDialog.tips(msg,"error");
    },

    successTips : function(msg) {
        jeesnsDialog.tips(msg, "success");
    },

    openView: function (url, title, width, height, callback) {
        if(width == undefined || width == ""){
            width = "600px";
        }
        if(height == undefined || height == ""){
            height = "650px";
        }
        jeesnsDialog.open(url, title, width, height, true, callback);
    },
    openEdit: function (url, title, width, height, callback) {
        if(width == undefined || width == ""){
            width = "600px";
        }
        if(height == undefined || height == ""){
            height = "650px";
        }
        jeesnsDialog.open(url, title, width, height, false, callback);
    },
    // inner
    open : function (url, title, width, height, viewFlag, callback) {
        var btns = ['确认', '取消'];
        if (viewFlag) {
            btns = ['关闭'];
        }
        layer.open({
            title: title,
            type: 2,  // 0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            area: [width,height],
            fix: true,
            maxmin: true,
            content: url, // 弹出框内容
            scrollbar: false,
            btn: btns, // 按钮1和按钮2的回调分别是yes(btn1)和btn2，而从按钮3开始，则回调为btn3: function(){}，以此类推
            yes: function (index, layero) {  // 按钮【确认】的回调
                // 如果只有一个按钮，就直接关闭dlg即可，相当于关闭按钮
                if (viewFlag) {
                    layer.close(index);
                    return;
                }
                // 获取弹出层页面的变量
                // layero.find("iframe"); 找到iframe的jquery对象
                // layero.find("iframe")[0]; 将jquery对象转化为dom对象
                // contentWindow 获取当前iframe的内容window对象(dom对象)
                var data = $(layero).find("iframe")[0].contentWindow.formData();
                // or
                // var ifname = "layui-layer-iframe" + index; // 获得layer层的名字
                // var Ifame = window.frames[ifname]; // 得到框架
                // var FormID = eval(Ifame.document.getElementById("form1")); // 将字符转成框架中form的对象
                // Ifame.checkForm(FormID); // 访问框架内的提交函数，并进行提交前的检查




                //当点击‘确定’按钮的时候，获取弹出层返回的值
                // var res = window["layui-layer-iframe" + index].callbackdata();
                // console.log(data);
                // console.log(!data);
                // 如果form验证不通过，返回data为空，此处data为空时对话框不关闭
                if (!data) {
                    return;
                }
                // var body = layer.getChildFrame('body', index);  //巧妙的地方在这里哦
                // var jform = $(body).find("#inputForm");

                if (data && callback) {
                    callback(data);
                }
                layer.close(index);
            },
            btn2: function(index) { // 点击取消按钮
                layer.close(index);
            },
            cancel: function(index){ // 点击右上角x号按钮的回调
                // window.location.href = window.location.href;
            }
        });
    } // end open function
};


