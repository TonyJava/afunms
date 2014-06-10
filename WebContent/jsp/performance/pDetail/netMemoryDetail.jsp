<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String alias = new String(request.getParameter("alias").getBytes("ISO-8859-1"),"UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网络设备内存详细信息</title>
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script type="text/javascript">
$(function() {
	var ip=getUrlParam("ip");
	var id=getUrlParam("id");
	$("#title").text("<%=alias%>("+ip+")");
	var so = new SWFObject("<%=basePath%>flex/Net_Memory_Month.swf?ipadress=" + ip + "&id=" + id, "Net_Memory_Month", "800", "400", "8", "#ffffff");
		so.write("flashcontent");

	});
</script>
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
</style>
</head>
<body>
	<div class="groupTitle">
		<label id="title"></label>
	</div>
	<div align="center" style="float: left; width: 100%;">
		<table cellpadding="0" cellspacing="0" width=98%>
			<tr>
				<td width="100%" align="center">
					<div id="flashcontent">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>