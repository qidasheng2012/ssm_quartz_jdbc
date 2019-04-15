<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>编辑任务</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <style type="text/css">
        input{
            width: 250px;
            height: 30px;
        }

        tr{
            height: 45px;
        }
    </style>
</head>

<body style="background: beige; margin: 100px 300px 200px;">
<form id="form" method="post">
    <input type="hidden" name="oldjobName" value="${pd.jobName}">
    <input type="hidden" name="oldjobGroup" value="${pd.jobGroup}">
    <input type="hidden" name="oldtriggerName" value="${pd.triggerName}">
    <input type="hidden" name="oldtriggerGroup" value="${pd.triggerGroupName}">

    <h3>编辑Trigger</h3>
    <hr/>

    <table id="table_report" class="table table-striped table-bordered table-hover">
        <tr>
            <td>cron</td>
            <td><input type="text" name="cron" value="${pd.cron}"/></td>
        </tr>
        <tr>
            <td>clazz</td>
            <td><input type="text" name="clazz" value="${pd.clazz}"/></td>
        </tr>
        <tr>
            <td>jobName</td>
            <td><input type="text" name="jobName" value="${pd.jobName}"/></td>
        </tr>
        <tr>
            <td>jobGroup</td>
            <td><input type="text" name="jobGroupName" value="${pd.jobGroup}"/></td>
        </tr>
        <tr>
            <td>triggerName</td>
            <td><input type="text" name="triggerName" value="${pd.triggerName}"/></td>
        </tr>
        <tr>
            <td>triggerGroupName</td>
            <td><input type="text" name="triggerGroupName" value="${pd.triggerGroupName}"/></td>
        </tr>
        <tr>
            <td></td>
            <td>
                <input type="button"  onclick="commit()"  style="width:45px;border: 0;background-color: #7ED321;" value="提交"/>
                <input type="button"  onclick="cancel()" style="width:45px;border: 0;background-color: #fff;" value="返回"/>
            </td>
        </tr>
    </table>
</form>
</body>
</html>

<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script>
    var url = "${pageContext.request.contextPath}";

    // 返回
    function cancel() {
        window.location.href = url + "/quartz/listJob";
    }

    // 提交
    function commit() {
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "${pageContext.request.contextPath }/quartz/edit" ,
            data: $('#form').serialize(),
            success: function (data) {
                if (data.status = 'success') {
                    alert("修改成功");
                    window.location.href = url + "/quartz/listJob";
                } else {
                    alert("修改失败");
                }
            },
            error : function() {
                alert("异常！");
            }
        });
    }
</script>
