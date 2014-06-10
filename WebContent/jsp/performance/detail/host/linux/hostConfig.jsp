<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-
transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/hostConfig.js" type="text/javascript"></script>
<style type="">
html {
	height: 100%;
}

body {
	height: 100%;
	padding: 0;
	margin: 0;
}

#container {
	margin: 0;
}

.divHead {
	width: 100%;
	background: url('<%=basePath%>css/img/pTool/divHead.gif');
	height: 25px;
	border-bottom: 1px solid green;
	font-weight: bold;
	padding: 5px 0 0 10px;
}

.partDivParent {
	width: 100%;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="container">
		<div class="partDivParent">
			<div class="divHead">CPU配置</div>
			<div id="cpuConfigGrid"></div>
		</div>
		<div class="partDivParent">
			<div class="divHead">内存配置</div>
			<div id="memoryConfigGrid" style="display: none"></div>
		</div>
		<div class="partDivParent">
			<div class="divHead">用户信息</div>
			<div id="userConfigGrid" style="display: none"></div>
		</div>
	</div>
</body>
</html>
