<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
	String alias = new String(request.getParameter("alias").getBytes("ISO-8859-1"), "UTF-8");
	alias = java.net.URLEncoder.encode(alias,"UTF-8");
	String nodeId = request.getParameter("nodeId");
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
<script src="js/windowsPerformanceDetail.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<style type="text/css">
html,body {
	margin: 0px 0px;
	width: 100%;
	height: 100%;
}

#parent {
	margin: 0;
	overflow: hidden;
	height: 100%;
}

#toolTabDiv {
	width: 100%;
	overflow: hidden;
	height: 20px;
}

#toolTab {
	margin: 2px 2px 0 0;
	width: 20px;
	float: right;
	width: 20px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="parent">
		<div id="toolTabDiv">
			<div id="toolTab">
				<img alt="工具栏" src='<%=basePath%>css/img/pTool/config.gif' id="toolImg">
			</div>
		</div>
		<div id="navtab" style="width: 100%; height: 100%; overflow: hidden; border: 1px solid #A3C0E8;">
			<div title="配置信息" style="height: 100%" class="tabC" lselected="true" toHref="host/windows/hostConfig.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src="host/windows/hostConfig.jsp?ip=<%=ip%>"></iframe>
			</div>
			<div title="性能" style="height: 100%" class="tabC" toHref="host/windows/hostPerf.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="接口" style="height: 100%" class="tabC" toHref="host/windows/hostInterface.jsp?ip=<%=ip%>&alias=<%=alias%>&nodeId=<%=nodeId%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="进程" style="height: 100%" class="tabC" toHref="host/windows/hostProcess.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="服务" style="height: 100%" class="tabC" toHref="host/windows/hostService.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="软件" style="height: 100%" class="tabC" toHref="host/windows/hostSoftware.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="设备" style="height: 100%" class="tabC" toHref="host/windows/hostDevice.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="存储" style="height: 100%" class="tabC" toHref="host/windows/hostStorage.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="Arp" style="height: 100%" class="tabC" toHref="host/windows/hostArp.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="SysLog" style="height: 100%" class="tabC" toHref="host/windows/hostSysLog.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
			<div title="告警" style="height: 100%" class="tabC" toHref="host/windows/hostEvent.jsp?ip=<%=ip%>">
				<iframe frameborder="0" src=""></iframe>
			</div>
		</div>
	</div>
</body>
</html>