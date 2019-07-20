<%--
  Created by IntelliJ IDEA.
  User: ymbcxb
  Date: 2019/6/22
  Time: 19:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
SpringMVC上传文件
<form action="/manage/product/upload/" method="post" enctype="multipart/form-data">
    <input type="file" name="file">
    <input type="submit" value="普通文件上传文件">
</form>

富文本上传文件
<form action="/manage/product/richtext_img_upload" method="post" enctype="multipart/form-data">
    <input type="file" name="file">
    <input type="submit" value="富文本图片上传文件">
</form>
</body>
</html>
