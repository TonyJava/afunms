<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String nodeId = (String) request.getParameter("nodeId");
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
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="js/weblogicConfig.js" type="text/javascript"></script>

<style type="text/css">
body {
	font-size: 12px;
}

.l-table-edit {
	
}

.l-table-edit-td {
	height: 30px;
	line-height: 30px;
	text-align: right;
	width: 120px;
}

.l-table-value-td {
	height: 30px;
	line-height: 30px;
	text-align: left;
	border-right: 1px dotted green;
}

#lineOne {
	width: 100%;
	float: left;
}

#configDiv {
	float: left;
	margin: 2px 0 0 2px;
}

#configDiv table {
	width: 100%;
}

#picDiv {
	float: left;
	margin: 2px 0 0 5px;
	width: 420px;
}

.group {
	height: 30px;
	line-height: 30px;
	border-right: 1px solid green;
	color: white;
	background: url('<%=basePath%>css/img/common/blue-sides.gif');
	font-weight: bold;
	color: white
}
</style>

</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />
	<input id="nodeId" type="hidden" value="<%=nodeId%>" />
	<div id="lineOne">
		<div id="configDiv">
			<table cellpadding="0" cellspacing="0" class="l-table-edit" id="configTable">
				<tbody>
					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">服务信息</td>
					</tr>
					<tr>
						<td class="l-table-edit-td">服务名称:</td>
						<td class="l-table-value-td" id="serverRuntimeName" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">服务监听地址:</td>
						<td class="l-table-value-td" id="serverRuntimeListenAddress" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">服务监听端口:</td>
						<td class="l-table-value-td" id="serverRuntimeListenPort" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td" valign="top">服务Socket数:</td>
						<td class="l-table-value-td" id="RunOpenSocketsCurCount" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">服务状态:</td>
						<td colspan="3" class="l-table-value-td" id="serverRuntimeState" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">域信息</td>
					</tr>
					<tr>
						<td class="l-table-edit-td">域名:</td>
						<td class="l-table-value-td" id="domainName" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">域端口:</td>
						<td class="l-table-value-td" id="domainAdministrationPort" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">域版本:</td>
						<td class="l-table-value-td" id="domainConfigurationVersion" style="padding-left: 15px;"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="picDiv">
			<div>
				<div id="pingTodayPanel" style="float: left;">
					<div id="pingTodayPie"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>