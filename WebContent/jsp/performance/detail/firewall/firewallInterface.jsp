<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String nodeId = request.getParameter("nodeId");
	String alias = request.getParameter("alias");
	alias = new String(alias.getBytes("ISO-8859-1"), "UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-
transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerMenu.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerMenuBar.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerResizable.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/firewallInterface.js" type="text/javascript"></script>
<style type="">
html {
	height: 100%;
}

body {
	height: 100%;
	padding: 0px;
	margin: 0;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="nodeId" type="hidden" value="<%=nodeId%>" />
	<input id="alias" type="hidden" value="<%=alias%>" />
	<div id="firewallInterfaceGrid"></div>

</body>
</html>
