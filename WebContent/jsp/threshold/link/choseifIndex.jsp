<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String flag = request.getParameter("flag"); 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>选择主机</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTextBox.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js" type="text/javascript"></script>
<script src="js/choseifIndex.js" type="text/javascript"></script>
<style type="text/css">
body {
	padding: 1px 2px 0 1px;
	margin: 0;
}

#searchbar,#ifIndexGrid {
	float: left;
}

#searchbar {
	margin: 2px 0 2px 5px;
}

#titleDiv,#textDiv {
	float: left;
}

#titleDiv {
	width: 50px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="flag" type="hidden" value="<%=flag %>" />
	<div style="width: 100%; margin-top: 2px">
		<div id="searchbar">
			<div id="titleDiv">关键字:</div>
			<div id="textDiv">
				<input id="ifIndexKey" type="text" />
			</div>
		</div>
		<div id="ifIndexGrid"></div>
	</div>
</body>
</html>