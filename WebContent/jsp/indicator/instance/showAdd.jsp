<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
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
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/indicatorShowAdd.js" type="text/javascript"></script>

<style type="text/css">
.middle input {
	display: block;
	width: 30px;
	margin: 2px;
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
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div>
		<div class="groupTitle">指标参数</div>
		<div style="margin: 4px; float: left;">
			<div id="waitAddIndicators"></div>
		</div>
		<div style="margin: 4px; float: left;" class="middle">
			<input type="button" onclick="moveIndicatorsToLeft()" value="&lt;" /> <input type="button" onclick="moveIndicatorsToRight()" value="&gt;" /> <input type="button"
				onclick="moveAllIndicatorsToLeft()" value="&lt;&lt;" /> <input type="button" onclick="moveAllIndicatorsToRight()" value="&gt;&gt;" />
		</div>
		<div style="margin: 4px; float: left;">
			<div id="hasAddIndicators"></div>
		</div>
	</div>
	<div>
		<div class="groupTitle">网元列表</div>
		<div style="margin: 4px; float: left;">
			<div id="waitAddNodes"></div>
		</div>
		<div style="margin: 4px; float: left;" class="middle">
			<input type="button" onclick="moveNodesToLeft()" value="&lt;" /> <input type="button" onclick="moveNodesToRight()" value="&gt;" /> <input type="button" onclick="moveAllNodesToLeft()"
				value="&lt;&lt;" /> <input type="button" onclick="moveAllNodesToRight()" value="&gt;&gt;" />
		</div>
		<div style="margin: 4px; float: left;">
			<div id="hasAddNodes"></div>
		</div>
	</div>
	<div style="float: left; margin-left: 40%; margin-top: 10px;">
		<div id="submit"></div>
		<div id="close"></div>
	</div>

</body>
</html>