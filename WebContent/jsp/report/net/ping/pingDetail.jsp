<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ids = request.getParameter("ids");
	String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/jquery.validate.min.js"
	type="text/javascript"></script>
<script src="<%=basePath%>js/jquery.metadata.js" type="text/javascript"></script>
<script src="<%=basePath%>js/messages_cn.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="js/pingDetail.js"></script>
<script src="<%=basePath%>jsp/js/typeAndSubType.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
</head>
<style type="text/css">
body {
	font-size: 12px;
}
</style>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ids" type="hidden" value="<%=ids%>" />
	<form>
		<table cellpadding="0" cellspacing="0" class="l-table-edit">
			<tr>
				<td align="right" class="l-table-edit-td">开始时间:</td>
				<td align="left" class="l-table-edit-td"><input name="startDate"
					type="text" id="startDate" value="<%=startDate + " 00:00:00" %>" readonly="readonly"/></td>
				<td align="left"></td>
				<td align="right" class="l-table-edit-td">结束时间:</td>
				<td align="left" class="l-table-edit-td"><input name="endDate"
					type="text" id="endDate" value="<%=endDate + " 23:59:59" %>" readonly="readonly" /></td>
				<td align="left"></td>
			</tr>
		</table>
	</form>
	<div id="pingdetailGrid"></div>
	<div style="height: 260px; margin-top: 3px;">
		<div id="responseTimeLinePanel" style="float: left">
			<div id="pingChartDiv">
				<strong>You need to upgrade your Flash Player</strong>
			</div>
		</div>
		<div id="responseTimePiePanel"
			style="float: right; margin-left: 10px;">
			<div id="responsetimeChartDiv"></div>
		</div>
	</div>
</body>
</html>