<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String nodeId = request.getParameter("nodeId");
	String type = request.getParameter("type");
	String subType = request.getParameter("subType");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerAccordion.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTree.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/pDbTool.js" type="text/javascript"></script>
<style type="text/css">
body,html {
	height: 100%;
}

body {
	padding: 0px;
	margin: 0;
	overflow: hidden;
}

#accordion {
	margin: 3px 0px 0 3px;
	width: 165px;
	overflow: hidden;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="nodeId" type="hidden" value="<%=nodeId%>" />
	<input id="type" type="hidden" value="<%=type%>" />
	<input id="subType" type="hidden" value="<%=subType%>" />
	<div id="accordion">
		<div title="工具">
			<ul>
				<li bi="cancle.gif" fn="cancleMonitor()">取消监控</li>
			</ul>
		</div>
		<div title="指标阈值">
			<ul>
				<li bi="zb.gif" fn="indicatorDetail()">采集指标</li>
				<li bi="zb.gif" fn="thresholdDetail()">告警阈值</li>
			</ul>
		</div>
	</div>
</body>
</html>