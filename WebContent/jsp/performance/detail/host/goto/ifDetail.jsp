<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String alias = new String(request.getParameter("alias").getBytes("ISO-8859-1"),"UTF-8");
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/ifDetail.js" type="text/javascript"></script>
<title>接口详细</title>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="alias" type="hidden" value="<%=alias%>" />
	<div id="flashcontent">
		<strong>You need to upgrade your Flash Player</strong>
	</div>
</body>
</html>