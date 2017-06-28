<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>合同执行</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
    //
    function addRow(list, idx, tpl, row){
        $(list).append(Mustache.render(tpl, {
            idx: idx, delBtn: true, row: row
        }));
        $(list+idx).find("select").each(function(){
            $(this).val($(this).attr("data-value"));
        });
        $(list+idx).find("input[type='checkbox'], input[type='radio']").each(function(){
            var ss = $(this).attr("data-value").split(',');
            for (var i=0; i<ss.length; i++){
                if($(this).val() == ss[i]){
                    $(this).attr("checked","checked");
                }
            }
        });
    }
</script>
</head>
<body>

<div class="container" style="background-color:#ff00ff">
    <div class="row">
        <div class="col-xs-12 col-sm-6">
            <div class="row">
                <div class="col-xs-4 col-sm-4">
                    &nbsp;
                </div>
                <div class="col-xs-4 col-sm-4">
                    <img src="#" class="img-responsive">
                </div>
                <div class="col-xs-4 col-sm-4">
                    &nbsp;
                </div>
                <div class="col-xs-4 col-sm-4">
                    <span style="font-size:10px;color:#cccccc;">Alpha testing</span>
                </div>
            </div>
        </div>
        <div class="col-xs-12 col-sm-6">
            <div class="row">
                <div class="col-xs-4 col-sm-4">
                    <span>Another tesing text</span>
                </div>
                <div class="col-xs-4 col-sm-4">
                    <span style="color:#ffffcc;"> - </span>
                </div>
                <div class="col-xs-4 col-sm-4">
                    <span style="font-size:11px;color:#ffffff;">Random text</span>
                </div>
            </div>
        </div>
    </div>
</div>


<div class="container">
    <!--Row with two equal columns-->
    <div class="row">
        <div class="col-sm-6">
            <div class="demo-content">.col-sm-6</div>
        </div>
        <div class="col-sm-6">
            <div class="demo-content bg-alt">.col-sm-6</div>
        </div>
    </div>
    <hr>
    <!--Row with two columns divided in 1:2 ratio-->
    <div class="row">
        <div class="col-sm-4">
            <div class="demo-content">.col-sm-4</div>
        </div>
        <div class="col-sm-8">
            <div class="demo-content bg-alt">.col-sm-8</div>
        </div>
    </div>
    <hr>
    <!--Row with two columns divided in 1:3 ratio-->
    <div class="row">
        <div class="col-sm-3">
            <div class="demo-content">.col-sm-3</div>
        </div>
        <div class="col-sm-9">
            <div class="demo-content bg-alt">.col-sm-9</div>
        </div>
    </div>
</div>


    <div class="container">
        <div class="row">
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
            <div class="col-md-1">.col-md-1</div>
        </div>
        <div class="row">
            <div class="col-md-8">.col-md-8</div>
            <div class="col-md-4">.col-md-4</div>
        </div>
        <div class="row">
            <div class="col-md-4">.col-md-4</div>
            <div class="col-md-4">.col-md-4</div>
            <div class="col-md-4">.col-md-4</div>
        </div>
        <div class="row">
            <div class="col-md-6">.col-md-6</div>
            <div class="col-md-6">.col-md-6</div>
        </div>
    </div>
<div class="container-fluid">
    <div class="row">
        <div class="span2">
            项目名称
        </div>
        <div class="span2">
                ${projectExecution.apply.projectName}
        </div>
        <div class="span2">
            项目编码
        </div>
        <div class="span2">
                ${projectExecution.apply.projectCode }
        </div>
        <div class="span2">
            项目类型
        </div>
        <div class="span2">
            ${fns:getDictLabel(projectExecution.apply.category , 'pro_category', '')}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2">
            客户名称
        </div>
        <div class="span2">
                ${projectExecution.apply.client.customerName }
        </div>
        <div class="span2">
            客户联系人
        </div>
        <div class="span2">
                ${projectExecution.apply.customerContact.contactName }
        </div>
        <div class="span2">
            客户电话
        </div>
        <div class="span2">
                ${projectExecution.apply.customerContact.phone }
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2">
            合同号
        </div>
        <div class="span2">
                ${projectExecution.contractItem.contractCode}
        </div>
        <div class="span2">
            合同金额
        </div>
        <div class="span2">
                ${projectExecution.contractItem.contractAmount}
        </div>
        <div class="span2">
            合同毛利率
        </div>
        <div class="span2">
                ${projectExecution.contractItem.grossProfitMargin}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2">
            付款条件
        </div>
        <div class="span10">
                ${projectExecution.paymentTerm}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2">
            合同执行依据
        </div>
        <div class="span10">
                ${fns:getDictLabels(projectExecution.executionBasis, 'jic_execution_basis', '')}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2">
            交货地址
        </div>
        <div class="span2">
                ${projectExecution.deliveryAddress}
        </div>
        <div class="span2">
            交货联系人
        </div>
        <div class="span2">
                ${projectExecution.deliveryPerson}
        </div>
        <div class="span2">
            联系电话
        </div>
        <div class="span2">
                ${projectExecution.deliveryPhone}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2">
            文件附件
        </div>
        <div class="span10">
            <sys:ckfinder input="attachment" type="files" uploadPath="/project/execution" selectMultiple="true" readonly="true"/>
        </div>
    </div>

</div>
<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover">
        <caption>产品配置清单</caption>
    <thead>
    <tr>
        <th>品牌</th>
        <th>采购金额</th>
        <th>交货日期</th>
        <th>保修条款</th>
        <th>供应商</th>
        <th>供应商渠道</th>
        <th>联系人</th>
        <th>电话</th>
        <th>付款条件</th>
    </tr>
    </thead>
    <tbody id="itemList"></tbody>
</table>

<script type="text/template" id="itemTpl">//<!--
<tr id="executionItemList{{idx}}">
    <input id="executionItemList{{idx}}_id" name="executionItemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
    <input id="executionItemList{{idx}}_delFlag" name="executionItemList[{{idx}}].delFlag" type="hidden" value="0"/>
    <td>{{row.brand}}</td>
    <td>{{row.amount}}</td>
    <td style="width:95px" class="input-medium">{{row.deliveryDate}}</td>
    <td>{{row.warranty}}</td>
    <td>{{row.supplier}}</td>
    <td>{{row.supplierOrigin4Export}}</td>

    <td>{{row.contactPerson}}</td>
    <td>{{row.contactPhone}}</td>
    <td>{{row.paymentTerm}}</td>
</tr>//-->
</script>
<script type="text/javascript">
    var itemRowIdx = 0, itemTpl = $("#itemTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
    $(document).ready(function() {
        var data = ${fns:toJson(projectExecution.executionItemList)};
        for (var i=0; i<data.length; i++){
            addRow('#itemList', itemRowIdx, itemTpl, data[i]);
            itemRowIdx = itemRowIdx + 1;
        }
    }); // end ready
</script>

<br/><br/>

<c:if test="${not empty projectExecution.procInsId}">
    <act:histoicFlow procInsId="${projectExecution.procInsId}" />
</c:if>

</body>
</html>