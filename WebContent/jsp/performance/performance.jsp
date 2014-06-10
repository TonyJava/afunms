<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String portalFlag ="-1";
	if(null!=request.getParameter("portalFlag")){
		 portalFlag = new String(request.getParameter("portalFlag").getBytes("ISO-8859-1"),"UTF-8");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-
transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/performance.js" type="text/javascript"></script>

<script src="<%=basePath%>jsp/js/pTree.js" type="text/javascript"></script>

<style type="text/css">
body,html {
	height: 100%;
}

body {
	padding: 0px;
	margin: 0;
	overflow: hidden;
}

#performanceLayout {
	margin: 0;
	padding: 0;
}

#pFrameDiv {
	width: 100%;
	height: 100%;
}

#pFrame {
	width: 100%;
	height: 100%;
}

.l-link {
	display: block;
	height: 26px;
	line-height: 26px;
	padding-left: 10px;
	text-decoration: underline;
	color: #333;
}

.l-link2 {
	text-decoration: underline;
	color: white;
	margin-left: 2px;
	margin-right: 2px;
}

.l-layout-top {
	background: #102A49;
	color: White;
}

.l-layout-bottom {
	background: #E5EDEF;
	text-align: center;
}

.l-link {
	display: block;
	line-height: 22px;
	height: 22px;
	padding-left: 16px;
	border: 1px solid white;
	margin: 4px;
}

.l-link-over {
	background: #FFEEAC;
	border: 1px solid #DB9F00;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="portalFlag" type="hidden" value="<%=portalFlag%>" />
	<div id="performanceLayout" style="width: 100%; margin: 0 auto; margin-left: 2px;">
		<div position="left" title="主要菜单" id="pAccordion">
			<div title="网元列表" class="l-scroll">
				<!-- 性能树 -->
				<ul id="pTree" style="margin-top: 3px;"></ul>
			</div>
			<div title="链路">
				<div style="height: 7px;"></div>
				<a class="l-link" href="javascript:f_addTab('链路列表','链路列表','linkPerformanceList.jsp')">链路列表</a>
			</div>
		</div>
		<div position="center" id="pFramecenter">
			<div tabid="交换机" title="交换机">
				<!-- 右侧性能列表 -->
				<iframe frameborder=0 src="<%=basePath%>jsp/performance/netPerformanceList.jsp?type=switch" name="pFrame" id="pFrame"></iframe>
			</div>
		</div>
	</div>
</body>
</html>
