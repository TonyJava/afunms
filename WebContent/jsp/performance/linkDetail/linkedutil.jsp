<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String line = request.getParameter("line");
	String type = request.getParameter("type");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网元添加</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerForm.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerComboBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerCheckBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerButton.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTextBox.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/typeAndSubType.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/linkUtil.js" type="text/javascript"></script>
<style type="text/css">
.l-table-edit-td {
	padding: 4px;
}

.l-table-label-td {
	width: 90px;
	text-align: left;
}

.groupTitle {
	font-size: 12px;
	font-weight: bold;
	margin: 4px;
	padding-left: 20px;
	float: left;
	width: 95%;
	height: 28px;
	line-height: 28px;
	border-bottom: 1px solid #D6D6D6;
	background: url('<%=basePath%>css/icons/communication.gif') no-repeat;
	background-position: 0% 33.33333%;
}

.btDiv {
	text-align: center;
	margin: 10px 0 0 0;
	border-top: 2px solid black;
	padding: 5px 0 0 0;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="line" type="hidden" value="<%=line%>" />
	<input id="type" type="hidden" value="<%=type%>" />
	<form name="linkDetail" method="post" id="linkDetail">
		<div>
			<div class="groupTitle">基础信息</div>
			<div>
				<table cellpadding="0" cellspacing="0" width=100% align=center>
					<tr>
						<td class="l-table-label-td" align="left" nowrap>&nbsp;</td>
						<td class="l-table-label-td" align="left" nowrap>&nbsp;起点设备:</td>
						<td class="l-table-label-td" align="left" nowrap>&nbsp;终点设备:</td>
					</tr>
					<tr bgcolor="#F1F1F1">
						<td class="l-table-label-td" align="left" nowrap>&nbsp;设备名称:</td>
						<td class="l-table-label-td" id="startAlias"></td>
						<td class="l-table-label-td" id="endAlias"></td>
					</tr>
					<tr>
						<td class="l-table-label-td" align="left">&nbsp;管理IP地址:</td>
						<td class="l-table-label-td" id="startIp">&nbsp;</td>
						<td class="l-table-label-td" id="endIp">&nbsp;</td>
					</tr>
					<tr bgcolor="#F1F1F1">
						<td class="l-table-label-td" align=left nowrap>&nbsp;接口IP地址:</td>
						<td class="l-table-label-td" id="startLinkIp">&nbsp;</td>
						<td class="l-table-label-td" id="endLinkIp">&nbsp;</td>
					</tr>
					<tr>
						<td class="l-table-label-td" align="left">&nbsp;接口索引/状态:</td>
						<td class="l-table-label-td" id="startIndex"></td>
						<td class="l-table-label-td" id="endIndex"></td>
					</tr>
					<tr bgcolor="#F1F1F1">
						<td class="l-table-label-td" align=left nowrap>&nbsp;接口描述:</td>
						<td class="l-table-label-td" id="startDescr"></td>
						<td class="l-table-label-td" id="endDescr"></td>
					</tr>
				</table>
			</div>
		</div>
		<div>
			<div class="groupTitle">曲线图</div>
			<div id="linePic"></div>
		</div>
	</form>
</html>