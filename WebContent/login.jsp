<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="<%=basePath%>js/login.js" type="text/javascript"></script>
<title>管理登录</title>
<style type="text/css">
<!--
body,html {
	font-family: "微软雅黑", Arial, Helvetica, sans-serif;
	margin: 0;
	padding: 0;
	background: #CCC url(<%=basePath%>resource/image/login/bg.jpg);
}

#outContainer {
	position: absolute; /*绝对定位*/
	top: 50%; /* 距顶部50%*/
	left: 50%; /* 距左边50%*/
	height: 400px;
	margin-top: -200px; /*margin-top为height一半的负值*/
	width: 550px;
	margin-left: -275px; /*margin-left为width一半的负值*/
}

#loginPanel {
	width: 376px;
	height: 314px;
	background: url(<%=basePath%>resource/image/login/loginBg.png) no-repeat;
	margin-left: 90px;
	margin-top: 5px;
	position: absolute;
}

#logo {
	background: url(<%=basePath%>resource/image/login/logo.png);
	height: 80px;
}

#loginTitle {
	font-size: 16px;
	width: 98%;
	margin-top: 50px;
	height: 50px;
	line-height: 50px;
	text-align: center;
	color: #FFF;
	background-color: #06F;
}

#inputContainer {
	margin-top: 40px;
}

.userDiv {
	margin-top: 5px;
	margin-left: auto;
	margin-right: auto;
	height: 30px;
	width: 300px;
	background: url(<%=basePath%>resource/image/login/user.png) no-repeat;
	border: 1px #000000 dotted;
}

.passwordDiv {
	margin-top: 5px;
	margin-left: auto;
	margin-right: auto;
	height: 30px;
	width: 300px;
	background: url(<%=basePath%>resource/image/login/password.png) no-repeat;
	border: 1px #000000 dotted;
}

#userid,#password {
	width: 260px;
	height: 30px;
	line-height:20px;
	font-size: 16px;
	margin-left: 35px;
	border: none;
	background-color: transparent;
	padding-left:5px;
}

#submitDiv {
	margin: 20px 20px 0 0;
	text-align: right;
}

#submit {
	color: #FFF;
	font-family: "微软雅黑", Arial, Helvetica, sans-serif;
	font-size: 16px;
	border: none;
	width: 90px;
	height: 40px;
	background: url(<%=basePath%>resource/image/login/bg.png) repeat-x;
}

#submit:hover {
	color: #000;
}

#submit:visited {
	border: none;
}

input {
	outline: none
}
-->
</style>
</head>

<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<form>
		<div id="outContainer">
			<div id="logo"></div>
			<div id="loginPanel">
				<div id="loginTitle">请登录</div>
				<div id="inputContainer">
					<div class="userDiv">
						<input type="text" id="userid" />
					</div>
					<div class="passwordDiv">
						<input type="password" id="password" />
					</div>
				</div>
				<div id="submitDiv">
					<input type="button" id="submit" onclick="login();" value="登录" />
				</div>
			</div>
		</div>
	</form>
</body>
</html>