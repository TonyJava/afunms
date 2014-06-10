<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String subtype = request.getParameter("subtype");
	String type = request.getParameter("type");
	String nodeId = request.getParameter("nodeId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerCheckBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerButton.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerListBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTextBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/indicatorBatchEdit.js" type="text/javascript"></script>

<style type="text/css">
#searchbar {
	margin: 5px 0 5px 10px;
	width: 90%;
	float: left;
}

#titleDiv {
	float: left;
	width: 20px;
}

#textDiv {
	float: left;
}

#submit,#close {
	float: left;
	margin-left: 10px;
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

#btDiv {
	margin-left: auto;
	margin-right: auto;
	width: 160px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="subtype" type="hidden" value="<%=subtype%>" />
	<input id="type" type="hidden" value="<%=type%>" />
	<input id="nodeId" type="hidden" value="<%=nodeId%>" />
	<input id="nodeindicatorsIds" type="hidden" />
	<div>
		<div id="searchbar">
			<div id="titleDiv">IP:</div>
			<div id="textDiv">
				<input id="ipKey" type="text" />
			</div>
		</div>
		<div class="groupTitle">网元列表</div>
		<div style="margin: 4px; float: left;">
			<div id="hostGrid"></div>
		</div>
	</div>
	<div id="btDiv">
		<div id="submit" class="bt"></div>
		<div id="close" class="bt"></div>
	</div>
</body>
</html>