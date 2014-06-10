<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.afunms.application.dao.TomcatDao"%>
<%@ page import="com.afunms.application.model.Tomcat"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String tomcatId = (String) request.getParameter("tomcatId");
	TomcatDao dao = new TomcatDao();
	Tomcat vo = new Tomcat();
	try {
		if (null != tomcatId) {
			vo = (Tomcat) dao.findByID(tomcatId);
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(null!=dao){
			dao.close();
		}
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script type='text/javascript' src='<%=basePath%>/dwr/interface/AvailabilityCheckUtil.js'></script>
<script type='text/javascript' src='<%=basePath%>/dwr/engine.js'></script>
<script type='text/javascript' src='<%=basePath%>/dwr/util.js'></script>
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerComboBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerButton.js" type="text/javascript"></script>
<script src="js/tomcatCheck.js" type="text/javascript"></script>
<title>Insert title here</title>
<style type="text/css">
#parentDiv {
	width: 380px;
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

#linkInfo {
	float: left;
	margin-left: 10px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div id="parentDiv">
		<div class="op">
			<div>
				<div class="title" style="width: 50px">网元IP:</div>
				<div class="inputDiv">
					<input type="text" id="ip" value="<%=vo.getIpAddress()%>" />
				</div>
			</div>
			<div>
				<div class="title" style="width: 35px">端口:</div>
				<div class="inputDiv">
					<input type="text" id="port" value="<%=vo.getPort()%>" />
				</div>
			</div>
		</div>

		<div class="op">
			<div>
				<div class="title" style="width: 50px">用户:</div>
				<div class="inputDiv">
					<input type="text" id="user" value="<%=vo.getUser()%>" />
				</div>
			</div>
			<div>
				<div class="title" style="width: 35px">密码:</div>
				<div class="inputDiv">
					<input type="password" id="password" value="<%=vo.getPassword()%>" />
				</div>
			</div>
		</div>

		<div style="margin-top: 10px;">
			<div style="float: left; margin-left: 10px;">
				<div id="login"></div>
			</div>
			<div id="linkInfo"></div>
		</div>
	</div>
</body>
</html>