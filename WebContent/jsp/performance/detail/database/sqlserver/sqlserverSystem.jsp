<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
	String id = (String) request.getParameter("id");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerResizable.js" type="text/javascript"></script>
<script src="js/sqlserverSystem.js" type="text/javascript"></script>

</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="id" type="hidden" value="<%=id%>" />
	<div class="lineContainer">
		<div class="titleDiv">
			<div class="ti">
				<img src="<%=basePath%>css/img/right.png" />
			</div>
			<div class="tt">Page | Memory</div>
		</div>
		<div class="ContentDiv">
			<div class="gridDiv">
				<div id="pageGrid"></div>
			</div>
			<div class="gridDiv">
				<div id="memoryGrid" style="margin-left: 50px;"></div>
			</div>
		</div>
	</div>

	<div class="lineContainer">
		<div class="titleDiv">
			<div class="ti">
				<img src="<%=basePath%>css/img/right.png" />
			</div>
			<div class="tt">平均等待锁 | 进行等待锁</div>
		</div>
		<div class="ContentDiv">
			<div class="gridDiv">
				<div id="avgWaitGrid"></div>
			</div>
			<div class="gridDiv">
				<div id="processWaitGrid" style="margin-left: 50px;"></div>
			</div>
		</div>
	</div>

	<div class="lineContainer">
		<div class="titleDiv">
			<div class="ti">
				<img src="<%=basePath%>css/img/right.png" />
			</div>
			<div class="tt">启动等待锁 | 累计等待锁</div>
		</div>
		<div class="ContentDiv">
			<div class="gridDiv">
				<div id="startUpWaitGrid"></div>
			</div>
			<div class="gridDiv">
				<div id="countWaitGrid" style="margin-left: 50px;"></div>
			</div>
		</div>
	</div>
</body>
</html>