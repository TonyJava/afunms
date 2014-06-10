<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
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
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="js/tomcatPerf.js" type="text/javascript"></script>
</head>

<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />
	<div>
		<div id="pingPanel" style="float: left; margin: 2px 0 0 5px;">
			<div id="pingLine"></div>
		</div>
	</div>
	<div>
		<div id="jvmPanel" style="float: left; margin: 2px 0 0 5px;">
			<div id="jvmLine"></div>
		</div>
	</div>
</body>
</html>