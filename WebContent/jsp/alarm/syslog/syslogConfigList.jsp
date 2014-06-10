<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat,java.util.Date;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String startDate = df.format(new Date());
	String endDate = df.format(new Date());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/jquery.validate.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/jquery.metadata.js" type="text/javascript"></script>
<script src="<%=basePath%>js/messages_cn.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="js/syslogConfigList.js"></script>
<script src="<%=basePath%>jsp/js/typeAndSubType.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>

<style type="text/css">
html,body {
	font-size: 12px;
	height: 100%;
}

.l-table-edit {
	
}

.l-table-edit-td {
	padding: 4px;
}

.l-button-submit {
	width: 60px;
	float: left;
	margin-left: 10px;
	padding-bottom: 2px;
}

.l-verify-tip {
	left: 230px;
	top: 120px;
}
</style>

</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="syslogConfigGrid"></div>
</body>