<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
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
<script type="text/javascript">
	$(function() {
		$("#accordion").ligerAccordion({
			height : 300
		});
	});
</script>
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
	margin: 1px 2px 0 3px;
	width: 160px;
	overflow: hidden;
}
</style>
</head>
<body>
	<div id="accordion">
		<div title="工具">
			<ul>
				<li>列表一</li>
				<li>列表二</li>
				<li>列表三</li>
				<li>列表四</li>
				<li>列表五</li>
			</ul>
		</div>
		<div title="指标阈值">
			<ul>
				<li>列表一</li>
				<li>列表二</li>
				<li>列表三</li>
				<li>列表四</li>
				<li>列表五</li>
			</ul>
		</div>
		<div title="其他" style="padding: 10px">其他内容</div>
	</div>
</body>
</html>