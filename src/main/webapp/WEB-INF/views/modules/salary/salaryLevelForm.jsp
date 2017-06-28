<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>薪资等级管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//// =================
				$("#payMonthly,#nssf").change(function(){
				var payMonthlyValue;
				var nssfValue;
				var tempValue =21.75*8;//月付转化成小时成本待除的数
				//工资小时、五险一金小时 
				var salaryHourlyValue;
				var insuranceAndHousingFundHourlyValue;
				
				/* if(this.value>10000000){
					var errorInputLabel = this.id=="payMonthly"?"月薪资 ":"社保基数 ";
					if($(this).parent().has("label").length == 0){
						$(this).parent().append("<label  class='error'>"+errorInputLabel+"设置的值请小于1千万"+"</label>")
					}
					return false;
				}else{
					$(this).next().remove();
				} */
				if(isNaN($(this).val())){
					return false;
				}
				
				if(this.id =="payMonthly"){
					payMonthlyValue =this.value;
					insuranceAndHousingFundHourlyValue=$("#insuranceAndHousingFundHourly").val();//用于最后判断是不是要生产人力成本小时
					
					
					
					//var salaryHourlyInputNode =trNode.lastChild;//得到 工资消失的input节点
					salaryHourlyValue =(parseFloat(payMonthlyValue)/tempValue).toFixed(2);
					$("#salaryHourly").val(salaryHourlyValue);
					
				} else{
					nssfValue= this.value;
					salaryHourlyValue =$("#salaryHourly").val();//用于最后判断是不是要生产人力成本小时
					
					//具体的五险一金值
					var pensionValue=parseFloat(nssfValue)*0.2;
					var unemploymentValue =parseFloat(nssfValue)*0.01;
					var occupationalInjuryValue =parseFloat(nssfValue)*0.01*0.5;
					var birthValue =parseFloat(nssfValue)*0.01*0.8;
					var medicalValue =parseFloat(nssfValue)*0.1;
					var providentFundValue =parseFloat(nssfValue)*0.12;
					//具体的五险一金小时
					var pensionHourlyValue =(pensionValue/tempValue).toFixed(2);
					var unemploymentHourlyValue =(unemploymentValue/tempValue).toFixed(2);
					var occupationalInjuryHourlyValue =(occupationalInjuryValue/tempValue).toFixed(2);
					var birthHourlyValue =(birthValue/tempValue).toFixed(2);
					var medicalHourlyValue =(medicalValue/tempValue).toFixed(2);
					var providentFundHourlyValue =(providentFundValue/tempValue).toFixed(2);
					//五险一金小时成本
					insuranceAndHousingFundHourlyValue =(parseFloat(pensionHourlyValue)+parseFloat(unemploymentHourlyValue)+parseFloat(occupationalInjuryHourlyValue)+parseFloat(birthHourlyValue)+parseFloat(medicalHourlyValue)+parseFloat(providentFundHourlyValue)).toFixed(2);
					
					
					$("#pension").val((pensionValue).toFixed(2));
					$("#pensionHourly").val(pensionHourlyValue);
					$("#unemployment").val((unemploymentValue).toFixed(2));
					$("#unemploymentHourly").val(unemploymentHourlyValue);
					$("#occupationalInjury").val((occupationalInjuryValue).toFixed(2));
					$("#occupationalInjuryHourly").val(occupationalInjuryHourlyValue);
					$("#birth").val((birthValue).toFixed(2));
					$("#birthHourly").val(birthHourlyValue);
					$("#medical").val((medicalValue).toFixed(2));
					$("#medicalHourly").val(medicalHourlyValue);
					$("#providentFund").val((providentFundValue).toFixed(2));
					$("#providentFundHourly").val(providentFundHourlyValue);
					$("#insuranceAndHousingFundHourly").val(insuranceAndHousingFundHourlyValue);
				}
				
				if(salaryHourlyValue!=""&&insuranceAndHousingFundHourlyValue!=""){
					labelHourlyValue =(parseFloat(salaryHourlyValue)+parseFloat(insuranceAndHousingFundHourlyValue)).toFixed(2);
					$("#laborHourly").val(labelHourlyValue);
					
				}
				
				
			});
			//// =================
			
			
			
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
/* 					if($("#payMonthly").val()>10000000||$("#nssf").val()>10000000){
						return false;
					} */
					
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/salary/salaryLevel/">薪资等级成功列表</a></li>
		<li class="active"><a href="${ctx}/salary/salaryLevel/form?id=${salaryLevel.id}">薪资等级成功<shiro:hasPermission name="salary:salaryLevel:edit">${not empty salaryLevel.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="salary:salaryLevel:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="salaryLevel" action="${ctx}/salary/salaryLevel/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">工程师职级：</label>
			<div class="controls">
				<form:radiobuttons path="grade" items="${fns:getDictList('profession_grade')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">工种：</label>
			<div class="controls">
				<form:radiobuttons path="profession" items="${fns:getDictList('profession')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">月薪资：</label>
			<div class="controls">
				<form:input path="payMonthly" htmlEscape="false" class="input-xlarge required " number="true" maxlength="10"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">社保基数：</label>
			<div class="controls">
				<form:input path="nssf" htmlEscape="false" class="input-xlarge required" number="true" maxlength="10"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">养老：</label>
			<div class="controls">
				<form:input path="pension" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">养老小时成本：</label>
			<div class="controls">
				<form:input path="pensionHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">失业：</label>
			<div class="controls">
				<form:input path="unemployment" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">失业小时成本：</label>
			<div class="controls">
				<form:input path="unemploymentHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">工伤：</label>
			<div class="controls">
				<form:input path="occupationalInjury" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">工伤小时成本：</label>
			<div class="controls">
				<form:input path="occupationalInjuryHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">生育：</label>
			<div class="controls">
				<form:input path="birth" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">生育小时成本：</label>
			<div class="controls">
				<form:input path="birthHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">医疗：</label>
			<div class="controls">
				<form:input path="medical" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">医疗小时成本：</label>
			<div class="controls">
				<form:input path="medicalHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">公积金：</label>
			<div class="controls">
				<form:input path="providentFund" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">公积金小时成本：</label>
			<div class="controls">
				<form:input path="providentFundHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">五险一金小时成本合计：</label>
			<div class="controls">
				<form:input path="insuranceAndHousingFundHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">工资小时成本：</label>
			<div class="controls">
				<form:input path="salaryHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">人工小时总成本：</label>
			<div class="controls">
				<form:input path="laborHourly" htmlEscape="false" class="input-xlarge "  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="salary:salaryLevel:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>