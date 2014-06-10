<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.afunms.polling.base.Node" %>
<%@ page import="com.afunms.polling.PollingEngine"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	Node node = (Node) PollingEngine.getInstance().getNodeByIp((String)request.getParameter("ip"));
	String alias=node.getAlias();
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>内存利用率</title>
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script type="text/javascript">
$(function() {
	var ip=getUrlParam("ip");
	var id=getUrlParam("id");
	$("#title").text("<%=alias%>("+ip+")");
	var so = new SWFObject("<%=basePath%>flex/Area_Memory_month.swf?ipadress=" + ip + "&id=" + id, "Area_Memory_month", "800", "400", "8", "#ffffff");
		so.write("flashcontent");

	});
</script>
<style type="text/css">
body {
	font-family: "微软雅黑", "宋体", Arial, sans-serif; font-size: 12px;
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