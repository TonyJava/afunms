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
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/alarmTree.js" type="text/javascript"></script>
<script src="js/alarm.js" type="text/javascript"></script>

<style type="text/css">
body,html {
	height: 100%;
}

body {
	padding: 0px;
	margin: 0;
	overflow: hidden;
}

#alarmLayout {
	margin: 0;
	padding: 0;
}

#alFrame {
	width: 100%;
	height: 100%;
}

.l-link {
	display: block;
	height: 26px;
	line-height: 26px;
	padding-left: 10px;
	text-decoration: underline;
	color: #333;
}

.l-link2 {
	text-decoration: underline;
	color: white;
	margin-left: 2px;
	margin-right: 2px;
}

.l-layout-top {
	background: #102A49;
	color: White;
}

.l-layout-bottom {
	background: #E5EDEF;
	text-align: center;
}

.l-link {
	display: block;
	line-height: 22px;
	height: 22px;
	padding-left: 16px;
	border: 1px solid white;
	margin: 4px;
}

.l-link-over {
	background: #FFEEAC;
	border: 1px solid #DB9F00;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="alarmLayout" style="width: 100%; margin: 0 auto; margin-left: 2px;">
		<div position="left" title="功能菜单" id="alAccordion">
			<div title="配置选项" class="l-scroll">
				<ul id="alTree" style="margin-top: 3px;"></ul>
			</div>
		</div>
		<div position="center" id="alFramecenter">
			<div tabid="告警列表" title="告警列表" style="height: 300px">
				<iframe frameborder=0 src="<%=basePath%>jsp/alarm/alarmList.jsp" name="alFrame" id="alFrame"></iframe>
			</div>
		</div>
	</div>
</body>
</html>
