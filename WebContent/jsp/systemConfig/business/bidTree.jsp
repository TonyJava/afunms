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
<title>业务列表</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTree.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerMenu.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="js/bidTree.js" type="text/javascript"></script>

</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div>
		<div>
			<ul id="bidTree">
			</ul>
		</div>
		<div id="showNodeDiv" style="display: none; algin: center">
			<div>
				<input name="type" id="type" type="hidden">
			</div>
			<div>
				名称：<input name="name" id="name" type="text" ltype="text" />
			</div>
			<div>
				描述：<input name="descr" id="descr" type="text" ltype="text" />
			</div>
			<br>
			<div>
				<input id="save" type="button" value="保  存">
			</div>
		</div>
	</div>

</body>
</html>