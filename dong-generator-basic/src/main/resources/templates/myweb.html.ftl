<!DOCTYPE html>
<html>
<head>
    <title>测试模板生成</title>
</head>
<body>
<h1>
    欢迎来到测试站
</h1>
<ul>
    <#list menuItems as item>
        <li><a href="${item.url}">${item.label}</a> </li>
    </#list>
</ul>
<footer>
    ${currentYear} ceshi data
</footer>
</body>
</html>