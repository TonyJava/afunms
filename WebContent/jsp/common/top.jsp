<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>top</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.7.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerToolBar.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerLayout.js" type="text/javascript"></script>
<script src="<%=basePath%>js/jquery.jclock.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>

<script type="text/javascript">
	var basePath = null;
	function itemclick(item) {
		if(item.text=="拓扑"){
			openFullWindow("<%=basePath%>topology/network/index.jsp","topo");
		}else{
			var frame = window.parent.document.getElementById("hFrame");
			frame.src = basePath + "jsp/" + item.url;
		}
	}
	$(function() {
		basePath = $("#basePath").attr("value");
		$('#timer').jclock({
			withDate : true,
			withWeek : true
		});
		$("#toptoolbar").ligerToolBar({
			items : [ {
				text : '首页',
				click : function(item) {
					window.location.href = "index.jsp";
				},
				icon : 'home'
			}, {
				line : true
			}, {
				text : '拓扑',
				click : itemclick,
				url : '../topology/network/index.jsp',
				icon : 'topo'
			}, {
				line : true
			}, {
				text : '资源配置',
				click : itemclick,
				url : 'resourceConfig/resourceConfig.jsp',
				icon : 'config'
			}, {
				line : true
			}, {
				text : '性能',
				click : itemclick,
				url : 'performance/performance.jsp',
				icon : 'process'
			}, {
				line : true
			}, {
				text : '告警',
				click : itemclick,
				url : 'alarm/alarm.jsp',
				icon : 'alarm'
			}, {
				line : true
			}, {
				text : '报表',
				click : itemclick,
				url : 'report/report.jsp',
				icon : 'report'
			}, {
				line : true
			}, {
				text : '系统',
				click : itemclick,
				url : 'systemConfig/systemConfig.jsp',
				icon : 'settings'
			} ]
		});
		$("#topLayout").ligerLayout({
			topHeight : 100,
			height : 100,
			allowTopResize : false
		});

		$("#logout").bind("click", function() {
			$.ajax({
				url : basePath + 'user.do?action=logout',
				type : 'post',
				success : function(msg) {
					window.location.href = basePath + 'login.jsp';
				}
			});

		});
	});
</script>
<style type="text/css">
body {
	margin: 0;
	padding: 0px;
	overflow: hidden;
}

#topLayout {
	width: 100%;
	margin: 0;
	padding: 0;
	background-color: #995600;
}

#logoContainer {
	height: 70px;
	width: 100%;
	background: url('<%=basePath%>css/img/menu/topBackground.jpg') repeat-x;
	width: 100%;
}

#toplogo {
	margin: 0;
	width: 500px;
	float: left;
}

#toptoolbar {
	margin-top: 1px;
}

#right {
	float: right;
	height: 70px;
	width: 200px;
}

#timer {
	color: white;
	height: 40px;
	text-align: right;
	margin: 5px 5px 0 0;
}

/**
底部边框去掉
**/
.l-layout-top {
	border: none;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="topLayout">
		<div position="top">
			<div id="logoContainer">
				<div id="toplogo">
					<img alt="logo" src="<%=basePath%>css/img/menu/logo.jpg">
				</div>
				<div id="right">
					<div id="timer"></div>
					<div style="text-align: right; padding-right: 5px">
						<img alt="logout" id="logout" src="<%=basePath%>css/img/menu/logout.png">
					</div>
				</div>
			</div>

			<div id="toptoolbar"></div>
		</div>
	</div>
</body>
</html>