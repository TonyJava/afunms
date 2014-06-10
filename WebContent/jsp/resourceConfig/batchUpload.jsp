<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=basePath%>css/uploadify.css" rel="stylesheet" />
<script src="<%=basePath%>js/uploadify/jquery-1.7.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/uploadify/jquery.uploadify.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="js/batchUpload.js" type="text/javascript"></script>
<title>选择文件</title>
<style type="text/css">
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

#uploadDiv {
	float: left;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div>
		<div class="groupTitle">文件路径</div>
		<div id="uploadDiv">
			<input type="file" id="uploadify" name="uploadify" />
		</div>
	</div>

</body>
</html>