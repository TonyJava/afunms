<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTab.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDrag.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="js/topNPanel.js" type="text/javascript"></script>
<style type="text/css">
html,body {
	width: 100%;
	height: 100%;
}

#parent {
	width: 100%;
	height: 100%;
	overflow: hidden;
}

#navtab {
	width: 100%;
	height: 100%;
	overflow: hidden;
	border: 1px solid #A3C0E8;
}

.tabC {
	width: 100%;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="parent">
		<div id="navtab">
			<div title="实时" class="tabC" lselected="true" toHref="topNShowDetail.jsp?dateType=now">
				<iframe frameborder="0" src="" id="nowIframe"></iframe>
			</div>
			<div title="日" class="tabC" toHref="topNShowDetail.jsp?dateType=day">
				<iframe frameborder="0" src="" id="nowIframe"></iframe>
			</div>
			<div title="周" class="tabC" toHref="topNShowDetail.jsp?dateType=week">
				<iframe frameborder="0" src="" id="nowIframe"></iframe>
			</div>
			<div title="月" class="tabC" toHref="topNShowDetail.jsp?dateType=month">
				<iframe frameborder="0" src=""></iframe>
			</div>
		</div>
	</div>
</body>
</html>