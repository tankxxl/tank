/**
 * Created by rgz on 11/07/2017.
 */
var commonJs = {
    viewForm:function(){
        //查看已办状态时(status=finish):所有输入框 切换为 span 状态，操作对象隐藏
        //办理待办状态时（status=todo）:操作对象 data-edit属性 与 当前环节名称相同时可操作，否则不可操作。
        //备注：data-edit 为 可编辑环节， data-edit为空时也可编辑。
        var activityName=$("input[name='act.taskName']").val(); // 当前所在环节
        var status=$("input[name='act.status']").val(); // 当前处理形式
        //如果当前环节为空，则代表是第一个录入环节，直接跳过处理。
        if(activityName=='' && status!='finish'){
            return;
        }
        //select2，[attribute]取拥有attribute属性的元素
        // [attribute=value]、[attribute!=value]
        // 取attribute属性值等于value或不等于value的元素
        // [attribute ^= value]、[attribute $= value]和[attribute *= value]
        // attribute属性值以value开始、以value结束、或包含value值
        $("select[class*=select2-offscreen]").each(function(){
            var ifView=commonJs.ifView(this,activityName,status);
            // ifView为true为不可编辑，我们只处理不可编辑的控件
            if(ifView){
                var name=$(this).attr("name");
                var value=$(this).find("option:selected").val();
                var text=$(this).find("option:selected").text();
                // 获得匹配选择器的第一个祖先元素，从当前元素开始沿DOM树向上。
                var colBox=$(this).closest("div[class*='col-']");
                commonJs.setView(colBox,name,value,text);
            }
        });
        // Input 输入框
        // [selector1][selector2], 复合型属性过滤器，同时满足多个条件
        $("input[type=text][name],textarea").each(function(){
            // 判断此控件是只读还是可编辑
            var ifView=commonJs.ifView(this,activityName,status);
            if(ifView){
                var text=$(this).val();
                var colBox=$(this).closest("div[class*='col-']");
                commonJs.setView(colBox,$(this).attr("name"),$(this).val(),null);
            }
        });
        //按钮
        $("a[class=btn],input[type=button],input[type=submit],button").each(function(){
            var ifView=commonJs.ifView(this,activityName,status);
            if(ifView){
                $(this).remove();
            }
        });
        //其他，所有带[data-edit]元素的DOM
        $("*[data-edit]").each(function(){
            var ifView=commonJs.ifView(this,activityName,status);
            if(ifView){
                $(this).remove();
            }
        });
    },
    //此方法用于判断单个控件是否可被隐藏, 判断前提是：activityName不为空
    ifView:function(node,activityName,status){
        var dataEdit=$(node).attr("data-edit");
        if(typeof(dataEdit) != "undefined"
            && (
                dataEdit=="*"  //data-edit=* ,所有环节 都可操作
                || (dataEdit=="|" && status=="todo" ) //data-edit=|,待办可操作，已办不可操作
                || ($.inArray(activityName, dataEdit.split(','))>=0 && (status=="todo" || status=="edit") ) //data-edit=处理环节名，指定环节名在待办时可操作
            )
        ){
            return false;//可编辑
        }else{
            return true;//不可编辑
        }
    },
    //此方法用于设置隐藏，并移动隐藏域的位置
    setView:function(colBox,name,value,text){
        //判断colBox下是否有隐藏文本域，如果有则移动
        var hiddenList=colBox.find("input[type=hidden][name]");
        colBox.addClass("form-label").children().remove();
        hiddenList.each(function(){
            // 在循环内部，this代表hiddenList中的某一个元素
            colBox.append($(this));
        });
        colBox.append("<input type='hidden' name='"+name+"' value='"+value+"' /><span>"+(text==null?value==null?'':value:text)+"</span>");
    }
}
$(document).ready(function() {
        commonJs.viewForm();
    }
);