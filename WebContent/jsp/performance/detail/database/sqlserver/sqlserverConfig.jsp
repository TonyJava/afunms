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
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="js/sqlserverConfig.js" type="text/javascript"></script>
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
}

.l-table-value-td {
	height: 30px;
	line-height: 30px;
	text-align: left;
}

#lineOne {
	width: 100%;
	float: left;
}

#systemDiv {
	float: left;
	margin: 2px 0 0 2px;
}

#systemDiv table {
	width: 100%;
}

#pingLinePanel {
	float: left;
	margin: 2px 0 0 2px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="id" type="hidden" value="<%=id%>" />
	<div id="lineOne">
		<div id="systemDiv">
			<table cellpadding="0" cellspacing="0" class="l-table-edit" id="systemTable">
				<tbody>
					<tr>
						<td class="l-table-edit-td">网元IP:</td>
						<td class="l-table-value-td" id="nodeIp" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td" valign="top">网元别名:</td>
						<td class="l-table-value-td" id="nodeAlias" style="padding-left: 15px;"></td>
					</tr>

					<tr>
						<td class="l-table-edit-td" valign="top">管理状态:</td>
						<td class="l-table-value-td" id="isM" style="padding-left: 15px;"></td>
					</tr>

					<tr>
						<td class="l-table-edit-td">当前状态:</td>
						<td class="l-table-value-td" id="status" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">主机名称:</td>
						<td class="l-table-value-td" id="hostName" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">补丁包:</td>
						<td class="l-table-value-td" id="servicePackage" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">进程ID:</td>
						<td class="l-table-value-td" id="processId" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">用户模式:</td>
						<td class="l-table-value-td" id="userMode" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">安全性模式:</td>
						<td class="l-table-value-td" id="securityMode" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">群集实例:</td>
						<td class="l-table-value-td" id="integratedInstance" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">版本:</td>
						<td class="l-table-value-td" style="padding-left: 15px;"><textarea cols="100" rows="4" class="l-textarea" style="width: 400px" id="version"></textarea></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="pingLinePanel">
			<div id="pingLine"></div>
		</div>
	</div>
</body>
</html>