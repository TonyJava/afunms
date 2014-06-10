<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
	String id = (String) request.getParameter("id");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-
transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerMenu.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerMenuBar.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerResizable.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/oracleTopsql.js" type="text/javascript"></script>
<style type="">
html {
	height: 100%;
}

body {
	height: 100%;
	padding: 0px;
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
	<input id="ip" type="hidden" value="<%=ip%>" />
	<input id="id" type="hidden" value="<%=id%>" />
	<div id="container">
		<div class="partDivParent">
			<div class="divHead">内存占用</div>
			<div id="oracleMemsqlGrid" style="display: none"></div>
		</div>
		<div class="partDivParent">
			<div class="divHead">磁盘读取</div>
			<div id="oracleDisksqlGrid" style="display: none"></div>
		</div>
		<div class="partDivParent">
			<div class="divHead">排序</div>
			<div id="oracleSortsqlGrid" style="display: none"></div>
		</div>
	</div>
</body>
</html>
