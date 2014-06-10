<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<link href="facebox/facebox.css" media="screen" rel="stylesheet" type="text/css" />
<script src="facebox/facebox.js" type="text/javascript"></script>

<link href="downloadr/downloadr.css" media="screen" rel="stylesheet" type="text/css" />
<script src="downloadr/jqbrowser.js" type="text/javascript"></script>
<script src="downloadr/downloadr.js" type="text/javascript"></script>
<script src="js/batchDownload.js" type="text/javascript"></script>
<title>Insert title here</title>
<style type="text/css">
body {
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 12px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<a href="#" rel="downloadr" title="设备列表">Download</a>
</body>
</html>