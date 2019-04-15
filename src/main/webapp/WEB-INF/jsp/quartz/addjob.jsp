<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>新增任务</title>

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
    <h3>新增Trigger</h3>
    <hr/>

    <table id="table_report" class="table table-striped table-bordered table-hover">
        <tr>
            <td>cron</td>
            <td><input type="text" name="cron" value=""/></td>
        </tr>
        <tr>
            <td>clazz</td>
            <td><input type="text" name="clazz" value=""/></td>
        </tr>
        <tr>
            <td>jobName</td>
            <td><input type="text" name="jobName" value=""/></td>
        </tr>
        <tr>
            <td>jobGroupName</td>
            <td><input type="text" name="jobGroupName" value=""/></td>
        </tr>
        <tr>
            <td>triggerName</td>
            <td><input type="text" name="triggerName" value=""/></td>
        </tr>
        <tr>
            <td>triggerGroupName</td>
            <td><input type="text" name="triggerGroupName" value=""/></td>
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
            url: "${pageContext.request.contextPath }/quartz/add" ,
            data: $('#form').serialize(),
            success: function (data) {
                if (data.status = 'success') {
                    alert("新增成功");
                    window.location.href = url + "/quartz/listJob";
                } else {
                    alert("新增失败");
                }
            },
            error : function() {
                alert("异常！");
            }
        });
    }

</script>


