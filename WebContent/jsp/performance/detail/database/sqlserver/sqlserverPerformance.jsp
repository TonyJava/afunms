<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
	String id = (String) request.getParameter("id");
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
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerResizable.js" type="text/javascript"></script>
<script src="js/sqlserverPerformance.js" type="text/javascript"></script>

<style type="text/css">
.ContentDiv {
	height: 450px;
	width:100%;
	display: none;
}

.flexDiv {
	float: left;
	width: 50%；
	height: 400px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="id" type="hidden" value="<%=id%>" />
	<div class="lineContainer">
	<div class="titleDiv"><div class="ti"><img src="<%=basePath%>css/img/right.png" /></div><div class="tt">命中率 | 日志文件使用率</div></div>
		<div class="ContentDiv">
			<div class="flexDiv">
				<div id="hitRateFlex"></div>
			</div>
			<div class="flexDiv">
				<div id="logFileFlex" style="margin-left: 50px;"></div>
			</div>
		</div>
	</div>

	<div class="lineContainer">
	<div class="titleDiv"><div class="ti"><img src="<%=basePath%>css/img/right.png" /></div><div class="tt">表空间</div></div>
		<div class="ContentDiv">
			<div class="flexDiv">
				<div id="tableSpaceGrid"></div>
			</div>
			<div class="flexDiv">
				<div id="tableSpaceFlex" style="margin-left: 50px;"></div>
			</div>
		</div>
	</div>

</body>
</html>