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
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerComboBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerButton.js" type="text/javascript"></script>
<script src="js/webTelnet.js" type="text/javascript"></script>
<title>webTelnet</title>
<style type="text/css">
#parentDiv {
	width: 600px;
}

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
	width: 100%;
	float: left;
	margin: 10px 0 0 10px;
}

.commandContanner {
	width: 100%;
	float: left;
	margin: 10px 0 0 5px;
	background-color: #6CF;
}

.command {
	float: left;
	text-align: left;
}

.command input {
	width: 450px;
	height: 30px;
	line-height: 30px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input name="tcHashCode" type="hidden" id="tcHashCode" value="001">
	<div id="parentDiv">
		<div class="op">
			<div>
				<div class="title" style="width: 30px">类型:</div>
				<div class="inputDiv">
					<input type="text" id="ipaddress" value="<%=ip%>" />
				</div>
			</div>
			<div>
				<div class="title" style="width: 35px">端口:</div>
				<div class="inputDiv">
					<input type="text" id="port" value="23" />
				</div>
			</div>
			<div>
				<div class="title" style="width: 35px">终端:</div>
				<div class="inputDiv">
					<input type="text" id="terminalType" />
				</div>
			</div>
			<div style="float: left; margin-left: 10px;">
				<div id="login"></div>
			</div>

		</div>

		<div class="shower">
			<textarea cols="100" rows="15" class="l-textarea" style="width: 550px" readonly="readonly" id="printArea"></textarea>
		</div>
		<div class="commandContanner">
			<div class="command">
				Command: <input type="text" name="commandText" id="commandText" />
			</div>
		</div>
	</div>
</body>
</html>