<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat,java.util.Date;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String content = java.net.URLDecoder.decode(request.getParameter("content"), "UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Syslog详细</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="js/contentDetail.js"></script>


<style type="text/css">
.l-form-buttons {
	border-top: 1px dotted black;
}

.l-form-buttons li {
	float: left;
	position: relative;
	left: 180px;
}

#detailDiv {
	width: 495px;
	margin: 5px;
}

#sysDescr {
	width: 100%;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<form id="form">
		<div id="detailDiv">
			<textarea cols="50" rows="8" class="l-textarea" id="sysDescr"><%=content%></textarea>
		</div>
	</form>
</body>
</html>