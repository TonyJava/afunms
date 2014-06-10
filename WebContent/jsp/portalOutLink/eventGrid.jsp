<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.jasig.cas.client.validation.Assertion"%>
<%@page import="com.afunms.system.dao.UserDao"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="org.apache.axis.client.Call"%>
<%@page import="org.apache.axis.client.Service"%>
<%@page import="net.sf.json.JSONObject"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>事件列表</title>
<link rel="stylesheet" type="text/css" href="<%=basePath%>ext/resources/css/ext-all.css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="<%=basePath%>ext/ext-base.js"></script>
<script type="text/javascript" src="<%=basePath%>ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>ext/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=basePath%>jsp/portalOutLink/js/eventGrid.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>

<style type="text/css">
body,.x-grid3-col,.x-grid3-cell,.x-grid3-td-2,.x-grid3-cell-inner,.x-grid3-col-2,.x-grid3-hd-row td
	{
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 12px;
}

.x-toolbar td,.x-toolbar span,.x-toolbar input,.x-toolbar div,.x-toolbar select,.x-toolbar label
	{
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 12px;
}

body,html,#eventList {
	width: 100%;
	height: 100%;
}

a:link {
	text-decoration: none;
	color: black;
}

a:visited {
	text-decoration: none;
	color: black;
}

a:hover {
	text-decoration: underline;
	color: Green;
}

a:active {
	text-decoration: none;
	color: black;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="eventList" style="height: 100%"></div>
</body>
</html>