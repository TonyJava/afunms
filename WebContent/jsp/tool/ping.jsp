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
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script type='text/javascript' src='<%=basePath%>/dwr/interface/DWRUtil.js'></script>
<script type='text/javascript' src='<%=basePath%>/dwr/engine.js'></script>
<script type='text/javascript' src='<%=basePath%>/dwr/util.js'></script>
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerComboBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerButton.js" type="text/javascript"></script>
<script src="<%=basePath%>js/tool.js" type="text/javascript"></script>
<script src="js/ping.js" type="text/javascript"></script>
<title>ping工具</title>
<style type="text/css">
.op {
	margin: 5px 0 0 5px;
	border-bottom: 1px black dotted;
	height: 32px;
}

.opContainer {
	margin: 5px 0 0 10px;
	float: left;
	width: 250px;
}

.title {
	float: left;
	text-align: right;
	padding: 0 5px 0 0;
	height: 20px;
	line-height: 20px;
}

.inputDiv {
	float: left;
}

.shower {
	float: left;
	margin: 10px 0 0 10px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div>
		<div class="op">
			<div>
				<div class="title" style="width: 30px">类型:</div>
				<div class="inputDiv">
					<input type="text" id="ipaddress" value="<%=ip%>" />
				</div>
			</div>
			<div>
				<div class="title" style="width: 80px">包大小(Byte):</div>
				<div class="inputDiv">
					<input type="text" id="packagelength" />
				</div>
			</div>
			<div>
				<div class="title" style="width: 30px">次数:</div>
				<div class="inputDiv">
					<input type="text" id="executenumber" />
				</div>
			</div>
			<div style="float: left; margin-left: 10px;">
				<div id="execute"></div>
			</div>

		</div>

		<div class="shower">
			<textarea cols="100" rows="15" class="l-textarea" style="width: 550px" readonly="readonly" id="resultofping"></textarea>
		</div>
	</div>
</body>
</html>