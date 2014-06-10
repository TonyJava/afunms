<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String thresHoldNameString = request.getParameter("thresHoldNameString");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网元阈值添加</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerCheckBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerButton.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerListBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/nodeThresHoldShowAdd.js" type="text/javascript"></script>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="thresHoldNameString" type="hidden" value="<%=thresHoldNameString%>" />
	<div id="nodeThresHoldGrid"></div>
</body>
</html>