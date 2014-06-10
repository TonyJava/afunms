<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
	String nodeId = (String) request.getParameter("nodeId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tomcat应用</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="js/weblogicServlet.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<style type="text/css">
#searchbar,#weblogicServletGrid,#titleDiv,#textDiv {
	float: left;
}

#searchbar {
	margin: 2px 0 2px 5px;
}

#titleDiv {
	width: 50px;
	height: 25px;
	line-height: 22px;
}
</style>

</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />
	<input id="nodeId" type="hidden" value="<%=nodeId%>" />
	<div style="width: 100%; margin-top: 2px">
		<div id="searchbar">
			<div id="titleDiv">关键字:</div>
			<div id="textDiv">
				<input id="key" type="text" />
			</div>
		</div>
		<div id="weblogicServletGrid"></div>
	</div>
</body>