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
<title>设备详细信息</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="js/mysqlSystem.js" type="text/javascript"></script>

<style type="text/css">
body {
	font-size: 12px;
}

.l-table-edit {
	
}

.l-table-edit-td {
	height: 30px;
	width: 300px;
	line-height: 30px;
	text-align: right;
}

.l-table-value-td {
	height: 30px;
	width: 500px;
	line-height: 30px;
	text-align: left;
}

.l-verify-tip {
	left: 230px;
	top: 120px;
}

#lineOne {
	width: 100%;
	float: left;
}

#lineTwo {
	width: 100%;
	float: left;
	margin-top: 5px;
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
}

#flexDiv {
	float: left;
	margin: 2px 0 0 2px;
}

.flex {
	float: left;
	width: 50%
}
</style>

</head>

<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />
	<input id="id" type="hidden" value="<%=id%>" />
	<div id="lineOne">
		<div id="configDiv">
			<table cellpadding="0" cellspacing="0" class="l-table-edit" id="configTable">
				<tbody>
					<tr>
						<td class="l-table-edit-td">数据库别名:</td>
						<td class="l-table-value-td" id="alias" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">库名称:</td>
						<td class="l-table-value-td" id="dbname" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td" valign="top">类型:</td>
						<td class="l-table-value-td" id="dbtype" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">IP地址:</td>
						<td class="l-table-value-td" id="ipaddress" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">端口:</td>
						<td class="l-table-value-td" id="port" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">管理状态:</td>
						<td class="l-table-value-td" id="managed" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">当前状态:</td>
						<td class="l-table-value-td" style="padding-left: 15px;" id="status" colspan="3"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">数据库版本号:</td>
						<td class="l-table-value-td" id="version" style="padding-left: 15px;" colspan="3"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">服务器操作系统:</td>
						<td class="l-table-value-td" id="hostOS" style="padding-left: 15px;" colspan="3"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">基本路径:</td>
						<td class="l-table-value-td" id="basePaths" style="padding-left: 15px;" colspan="3"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">数据路径:</td>
						<td class="l-table-value-td" id="dataPath" style="padding-left: 15px;" colspan="3"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">错误日志路径:</td>
						<td class="l-table-value-td" id="logerrorPath" style="padding-left: 15px;" colspan="3"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="picDiv">
			<div>
				<div id="pingTodayPanel" style="float: left;">
					<div id="pingTodayPic"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="lineTwo">
		<div id="flexDiv">
			<table style="width: 100%">
				<tbody>
					<tr>
						<td><div id="pingLine" class="flex"></div></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>