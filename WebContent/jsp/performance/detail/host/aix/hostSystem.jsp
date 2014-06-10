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
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="js/hostSystem.js" type="text/javascript"></script>

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
	width: 420px;
}

#flexDiv {
	float: left;
	margin: 2px 0 0 2px;
}

#flexDiv table td {
	padding: 2px 2px 2px 2px;
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
	<div id="lineOne">
		<div id="configDiv">
			<table cellpadding="0" cellspacing="0" class="l-table-edit" id="configTable">
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
						<td class="l-table-edit-td" valign="top">系统名:</td>
						<td class="l-table-value-td" id="nodeSysName" style="padding-left: 15px;"></td>
					</tr>

					<tr>
						<td class="l-table-edit-td">类型:</td>
						<td class="l-table-value-td" id="type" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">子类型:</td>
						<td class="l-table-value-td" id="subType" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">启动时间:</td>
						<td class="l-table-value-td" id="sysUpTime" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">系统描述:</td>
						<td class="l-table-value-td" style="padding-left: 15px;"><textarea cols="100" rows="4" class="l-textarea" style="width: 300px" id="sysDescr"></textarea></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">物理内存:</td>
						<td class="l-table-value-td" id="physicalMemory" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">虚拟内存:</td>
						<td class="l-table-value-td" id="virtualMemory" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">数据采集时间:</td>
						<td class="l-table-value-td" id="ctTime" style="padding-left: 15px;"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="picDiv">
			<div>
				<div id="pingTodayPanel" style="float: left;">
					<div id="pingTodayPic"></div>
				</div>
				<div id="responseTodayPanel" style="float: left; margin-left: 2px;">
					<div id="responseTodayPic" style="padding-top: 40px; text-align: center"></div>
				</div>
			</div>
			<div>
				<div id="cpuTodayPanel" style="float: left; margin-top: 2px;">
					<div id="cpuTodayPic"></div>
				</div>
				<div id="memoryTodayPanel" style="float: left; margin-top: 2px; margin-left: 2px;">
					<div id="memoryTodayPic"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="lineTwo">
		<div id="flexDiv">
			<table style="width: 100%">
				<tbody>
					<tr>
						<td><div id="cpuLastWeek" class="flex"></div></td>
						<td><div id="memoryHistogram" class="flex"></div></td>
						<td><div id="diskHistogram" class="flex"></div></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>