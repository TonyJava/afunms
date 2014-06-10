<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	String portalFlag = request.getParameter("portalFlag");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>运维平台系统</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.7.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerToolBar.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerLayout.js" type="text/javascript"></script>

<style type="text/css">
html,body {
	height: 100%;
	margin: 0;
	padding: 0;
}

#topDiv {
	height: 100px;
}

#bottomDiv {
	height: 32px;
}

#iframeDiv {
	width: 100%;
}

#hFrame {
	width: 100%;
	height: 100%;
}
</style>

<script type="text/javascript">
	var portalFlag = null;
	$(function() {
		portalFlag = $("#portalFlag").val();
		redirect();
		layoutResize();
		autoHeight();
	});
	function layoutResize() {
		var bodyerH = $(window).height() - 132;
		$("#iframeDiv").height(bodyerH);
		$("#iframeDiv").width($(window).width());

	}
	function autoHeight() {
		$(window).resize(function() {
			layoutResize();
		});
	}

	function redirect() {
		if (portalFlag) {
			if (portalFlag == "alarm") {
				top.hFrame.location.href = "/afunms/jsp/alarm/alarm.jsp";
			} else if (portalFlag == "snapshot") {
				top.hFrame.location.href = "/afunms/jsp/performance/performance.jsp";
			}
		}
	}
</script>
</head>
<body>
	<input id="portalFlag" type="hidden" value="<%=portalFlag%>" />
	<div id="container">
		<div id="topDiv">
			<jsp:include page="jsp/common/top.jsp"></jsp:include>
		</div>
		<div id="iframeDiv">
			<iframe frameborder=0 scrolling=no src="<%=basePath%>jsp/home/homeContent.jsp" id="hFrame" name="hFrame"></iframe>
		</div>
		<div id="bottomDiv"><jsp:include page="jsp/common/bottom.jsp"></jsp:include></div>
	</div>
</body>
</html>