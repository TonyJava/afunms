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
<style type="text/css">
#bt {
	height: 32px;
	line-height: 32px;
	text-align: center;
	margin-top: 2px;
	background: url('<%=basePath%>css/img/common/bg.png');
}
</style>
</head>
<body>
	<div id="bt">Copyright © 2014 六盘水公安局运维平台</div>
</body>
</html>