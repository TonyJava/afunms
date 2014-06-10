<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat,java.util.Date;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	
	String tomcatIds = request.getParameter("tomcatIds");
	String iisIds = request.getParameter("iisIds");
	String weblogicIds = request.getParameter("weblogicIds");
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
<script src="<%=basePath%>js/jquery.validate.min.js" type="text/javascript"></script>
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
	<input id="tomcatIds" type="hidden" value="<%=tomcatIds%>" />
	<input id="iisIds" type="hidden" value="<%=iisIds%>" />
	<input id="weblogicIds" type="hidden" value="<%=weblogicIds%>" />
	<form>
		<table cellpadding="0" cellspacing="0" class="l-table-edit">
			<tr>
				<td align="right" class="l-table-edit-td">开始时间:</td>
				<td align="left" class="l-table-edit-td"><input name="startDate" type="text" id="startDate" value="<%=startDate %>" readonly="readonly" /></td>
				<td align="left"></td>
				<td align="right" class="l-table-edit-td">结束时间:</td>
				<td align="left" class="l-table-edit-td"><input name="endDate" type="text" id="endDate" value="<%=startDate %>" readonly="readonly" /></td>
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
	</div>
</body>
</html>