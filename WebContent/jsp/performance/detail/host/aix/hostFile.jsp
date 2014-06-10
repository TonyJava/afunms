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
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/hostFile.js" type="text/javascript"></script>

<style type="text/css">
#outContainer {
	margin: 2px 0 0 2px;
}

.divHead {
	width: 100%;
	background: url('<%=basePath%>css/img/pTool/divHead.gif');
	height: 25px;
	border-bottom: 1px solid green;
	font-weight: bold;
	padding: 5px 0 0 10px;
}

.partDivParent {
	width: 100%;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />

	<div class="divHead">文件系统利用率</div>
	<div id="fileSystem" style="text-align: center;">
		<strong>You need to upgrade your Flash Player</strong>
	</div>
	<div id="fileLineGrid"></div>
	<div class="divHead">磁盘增长率</div>
	<div id="diskAddGrid"></div>
	<div class="divHead">磁盘性能</div>
	<div id="diskperGrid"></div>
	<div class="divHead">页面性能</div>
	<div id="pageGrid"></div>


</body>
</html>