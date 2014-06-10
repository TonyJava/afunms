<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat,java.util.Date;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String startDate = df.format(new Date());
	String endDate = df.format(new Date());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="js/syslogDetailList.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>

<style type="text/css">
html,body {
	font-size: 12px;
	height: 100%;
}

.l-table-edit {
	
}

.l-table-edit-td {
	padding: 4px;
}

.l-button-submit {
	width: 60px;
	float: left;
	margin-left: 10px;
	padding-bottom: 2px;
}

.l-verify-tip {
	left: 230px;
	top: 120px;
}
</style>

</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<form>
		<table cellpadding="0" cellspacing="0" class="l-table-edit">
			<tr>
				<td align="right" class="l-table-edit-td">开始日期:</td>
				<td align="left" class="l-table-edit-td"><input name="startDate" type="text" id="startDate" value="<%=startDate%>" ltype="date" /></td>
				<td align="left"></td>
				<td align="right" class="l-table-edit-td">结束日期:</td>
				<td align="left" class="l-table-edit-td"><input name="endDate" type="text" id="endDate" value="<%=endDate%>" ltype="date" /></td>
				<td align="left"></td>
				<td class="l-table-label-td">类别:</td>
				<td class="l-table-edit-td"><select name="type" id="type" ltype="select">
				</select></td>
				<td align="right" class="l-table-edit-td">ip地址:</td>
				<td align="left" class="l-table-edit-td"><input name="ipaddress" type="text" id="ipaddress" value="" ltype="text" /></td>
				<td><input type="button" value="确定" id="bt" class="l-button l-button-submit" /></td>
			</tr>
		</table>
	</form>
	<div id="syslogDetailGrid"></div>
</body>