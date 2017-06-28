<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>工时填报</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
            setDateTd(new Date('${currentDate}'));
        });

        // 设置Date TD标签
        function setDateTd(date) {
            var currentDay = date.getDay();
            var weekStartDate = addDate(date, 0 - currentDay);
            for (var i = 0; i < 7; ++i) {
                var tempDate = addDate(weekStartDate, i);
                $($('#d' + i)[0]).text(tempDate.Format('yyyy-MM-dd'));
            }
        }

        // 获取前一周日期
        function preWeek() {
            var currentDate = new Date($('#weekSelect').val());
            var newDate = addDate(currentDate, -7);
            $('#paramDate').val(newDate.Format('yyyy-MM-dd'));
            $('#changeDateForm').submit();
        }
        // 获取后一周日期
        function nextWeek() {
            var currentDate = new Date($('#weekSelect').val());
            var newDate = addDate(currentDate, 7);
            $('#paramDate').val(newDate.Format('yyyy-MM-dd'));
            $('#changeDateForm').submit();
        }
        // 日期选择
        function pickedFunc(dp) {
            $('#paramDate').val(dp.cal.getDateStr());
            $('#changeDateForm').submit();
        }
    </script>

    <style type="text/css">
        .td-h {
            font-weight: bold;
        }
        .input-small {
            width: 50px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/project/tech/manhour/">工时列表</a></li>
    <li class="active"><a href="${ctx}/project/tech/manhour/form4FillInManhours">工时填报</a></li>
</ul>
<br/>

<%--
    用于改变时间后载入新数据并刷新
    在此先使用页面刷新方式；建议后期改为Ajax方式局部更新数据显示
--%>
<form:form id="changeDateForm" modelAttribute="currentDate" action="${ctx}/project/tech/manhour/regetform4FillInManhours" method="post">
    <input type="hidden" id="paramDate" name="paramDate" value="${currentDate}"/>
</form:form>

<form:form id="inputForm" modelAttribute="workorderManhourArray" action="${ctx}/project/tech/manhour/save" method="post">
    <sys:message content="${message}"/>
    <table class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <td colspan="4">
                选择周：
                <button onclick="preWeek()" type="button" style="margin-top: 10px;margin-bottom: 10px;">上一周</button>
                <input id="weekSelect" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                       style="margin-top: 10px;" value="${currentDate}"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false, onpicked:pickedFunc});"/>
                <button onclick="nextWeek()" type="button" style="margin-top: 10px;margin-bottom: 10px;">下一周</button>
            </td>
            <td id="d0"></td>
            <td id="d1"></td>
            <td id="d2"></td>
            <td id="d3"></td>
            <td id="d4"></td>
            <td id="d5"></td>
            <td id="d6"></td>
        </tr>
        <tr>
            <th class="td-h">
                工单编号
            </th>
            <th class="td-h">
                项目名称
            </th>
            <th class="td-h">
                工作内容
            </th>
            <th class="td-h">
                预计起止时间
            </th>
            <th class="td-h">
                周日
            </th>
            <th class="td-h">
                周一
            </th>
            <th class="td-h">
                周二
            </th>
            <th class="td-h">
                周三
            </th>
            <th class="td-h">
                周四
            </th>
            <th class="td-h">
                周五
            </th>
            <th class="td-h">
                周六
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${workorderManhourArray.workorderManhourArray}" var="manhourArray" varStatus="outterStatus">
            <tr>
                <td class="tit">
                        ${workorderList[outterStatus.index].id}
                </td>
                <td class="tit">
                        ${workorderList[outterStatus.index].assigning.techapply.project.projectName}
                </td>
                <td class="tit">
                        ${workorderList[outterStatus.index].descContent}
                </td>
                <td class="tit" style="text-align: center;">
                    <fmt:formatDate value="${workorderList[outterStatus.index].descTimeBegin}" pattern="yyyy-MM-dd"/><br/>
                    -<br/>
                    <fmt:formatDate value="${workorderList[outterStatus.index].descTimeEnd}" pattern="yyyy-MM-dd"/><br/>
                </td>
                <c:forEach items="${manhourArray}" var="manhour" varStatus="innerStatus">
                    <td>
                        <c:choose>
                            <c:when test="${not empty (workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].workorder.id}">
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].workorder.id"
                                       value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].workorder.id}"/>
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].engineer.id"
                                       value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].engineer.id}"/>
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].manhourDate"
                                       value="<fmt:formatDate value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].manhourDate}"/>"
                                        pattern="yyyy-MM-dd"/>
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].auditState"
                                       value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].auditState}"/>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].workorder.id"
                                       value="${workorderList[outterStatus.index].id}"/>
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].engineer.id"
                                       value="${workorderList[outterStatus.index].user.id}"/>
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].manhourDate"
                                       value="<fmt:formatDate value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].manhourDate}"/>"
                                       pattern="yyyy-MM-dd"/>
                                <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].auditState"
                                       value="${fns:getDictValue('未审批', 'audit_status', '0')}"/>
                            </c:otherwise>
                        </c:choose>
                        <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].id"
                               value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].id}"/>
                        <input type="hidden" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].auditOpinion"
                               value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].auditOpinion}"/>
                        <c:set var="auditState" value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].auditState}"/>
                        <c:choose>
                            <c:when test="${auditState eq fns:getDictValue('审批通过', 'audit_state', '1')}">
                                <input type="text" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].manhour" class="input-small "
                                       value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].manhour}" readonly style="color: green;"/>
                            </c:when>
                            <c:when test="${auditState eq fns:getDictValue('审批未通过', 'audit_state', '2')}">
                                <input type="text" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].manhour" class="input-small "
                                       value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].manhour}" style="color: red;"/>
                            </c:when>
                            <c:otherwise>
                                <input type="text" name="workorderManhourArray[${outterStatus.index}][${innerStatus.index}].manhour" class="input-small "
                                       value="${(workorderManhourArray.workorderManhourArray)[outterStatus.index][innerStatus.index].manhour}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="form-actions">
        <shiro:hasPermission name="project:tech:manhour:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>