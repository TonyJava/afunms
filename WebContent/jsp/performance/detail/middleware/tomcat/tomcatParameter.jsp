<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
	String nodeId = (String) request.getParameter("nodeId");
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
<script src="js/tomcatParameter.js" type="text/javascript"></script>

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
						<td colspan="6" class="group" style="padding-left: 15px;">VM参数</td>
					</tr>
					<tr>
						<td colspan="6" class="l-table-value-td" id="InputArguments" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">类路径</td>
					</tr>
					<tr>
						<td colspan="6" class="l-table-value-td" id="classPath" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">库路径</td>
					</tr>
					<tr>
						<td colspan="6" class="l-table-value-td" id="libraryPath" style="padding-left: 15px;"></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>