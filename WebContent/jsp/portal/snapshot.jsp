<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>设备快照</title>
<link rel="stylesheet" type="text/css" href="<%=basePath%>ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="css/data-view.css" />
<script type="text/javascript" src="<%=basePath%>ext/ext-base.js"></script>
<script type="text/javascript" src="<%=basePath%>ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>ext/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/PagingMemoryProxy.js"></script>
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="js/snapshot.js"></script>
<style type="text/css">
html,body {
	height: 100%;
}

.x-panel-tl {
	border: 0px dotted white;
}

.x-panel-ml,.x-panel-mr,.x-panel-br,.x-panel-bl {
	padding: 0px;
}

.x-panel-nofooter .x-panel-bc {
	height: 0px;
}

.x-toolbar td,.x-toolbar span,.x-toolbar input,.x-toolbar div,.x-toolbar select,.x-toolbar label
	{
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 12px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="snapshot" style="height: 100%"></div>
</body>
</html>