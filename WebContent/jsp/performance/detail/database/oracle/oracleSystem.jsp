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
<title>详细信息</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerForm.js" type="text/javascript"></script>
<script src="js/oracleSystem.js" type="text/javascript"></script>

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

#lineThree {
	width: 100%;
	float: left;
	margin-top: 5px;
}

#lineThree {
	width: 100%;
	float: left;
	margin-top: 5px;
}

#lineFour {
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
	<input id="id" type="hidden" value="<%=id%>" />
	<div class="partDivParent">
		<div class="divHead">基础信息</div>
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
							<td class="l-table-value-td" id="status" style="padding-left: 15px;"></td>
							<td class="l-table-edit-td" valign="top">监听状态:</td>
							<td class="l-table-value-td" id="lstrnStatu" style="padding-left: 15px;"></td>
						</tr>
						<tr>
							<td class="l-table-edit-td" valign="top">主机名称:</td>
							<td class="l-table-value-td" id="hostname" style="padding-left: 15px;"></td>
							<td class="l-table-edit-td">DB名称:</td>
							<td class="l-table-value-td" id="DBname" style="padding-left: 15px;"></td>
						</tr>
						<tr>
							<td class="l-table-edit-td">DB版本:</td>
							<td class="l-table-value-td" id="version" style="padding-left: 15px;"></td>
							<td class="l-table-edit-td">例程名:</td>
							<td class="l-table-value-td" id="instancename" style="padding-left: 15px;"></td>
						</tr>
						<tr>
							<td class="l-table-edit-td">例程状态:</td>
							<td class="l-table-value-td" id="instancestatus" style="padding-left: 15px;"></td>
							<td class="l-table-edit-td" valign="top">例程开始时间:</td>
							<td class="l-table-value-td" id="startup_time" style="padding-left: 15px;"></td>
						</tr>
						<tr>
							<td class="l-table-edit-td" valign="top">归档模式:</td>
							<td class="l-table-value-td" id="archiver" style="padding-left: 15px;"></td>
							<td class="l-table-edit-td">数据库创建日期:</td>
							<td class="l-table-value-td" id="created" style="padding-left: 15px;"></td>
						</tr>
						<tr>
							<td class="l-table-edit-td" colspan="3">最浪费内存的前10个语句占全部内存读取量的比例:</td>
							<td class="l-table-value-td" id="memsql" style="padding-left: 15px;" colspan="1"></td>
						</tr>
						<tr>
							<td class="l-table-edit-td">打开游标数:</td>
							<td class="l-table-value-td" id="opencurstr" style="padding-left: 15px;"></td>
							<td class="l-table-edit-td">当前会话数:</td>
							<td class="l-table-value-td" id="curconnectstr" style="padding-left: 15px;"></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="picDiv">
				<div>
					<div id="pingLinePanel" style="float: left;">
						<div id="pingLine"></div>
					</div>
					<div id="pingTodayPanel" style="float: left;">
						<div id="pingTodayPic"></div>
					</div>
				</div>

			</div>
		</div>
	</div>
	<div id="lineTwo">
		<table style="width: 100%">
			<tbody>
				<tr>
					<td><div id="dbdataPanel">
							<div id="dbdataHistogram" class="flex"></div>
						</div></td>
					<td><div id="pgaPanel">
							<div id="pgaHistogram" class="flex"></div>
						</div></td>
					<td><div id="sgaPanel">
							<div id="sgaHistogram" class="flex"></div>
						</div></td>
					<td><div id="spacePanel">
							<div id="spaceHistogram" class="flex"></div>
						</div></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="lineThree">
		<table style="width: 100%">
			<tbody>
				<tr>
					<td><div id="contrPanel">
							<div id="contrGrid"></div>
						</div></td>
					<td><div id="logPanel">
							<div id="logGrid"></div>
						</div></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="lineFour">
		<div id="keepobjPanel">
			<div id="keepobjGrid"></div>
		</div>
	</div>
</body>
</html>