<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.afunms.common.util.SessionConstant,com.afunms.system.model.User,com.afunms.realtime.BandwidthControler;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	
	User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	String ip = request.getParameter("ip");
	String ifindex = request.getParameter("ifIndex");
	String fileName = user.getUserid() + "_" + ip + "_" + ifindex+ "_bandwidth.xml";   
	String nodeId = request.getParameter("id");
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
<script type='text/javascript' src='<%=basePath%>/dwr/interface/BandwidthControler.js'></script>
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/MKDateTime.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/ifRealTimeBandWidth.js" type="text/javascript"></script>
<title>实时带宽</title>
<style type="text/css">
.l-table-edit-td {
	padding: 4px;
}

.l-table-label-td {
	width: 90px;
	text-align: right;
}

#parentContainer {
	float: left;
	margin: 1px;
	width: 99%;
	border: 1px dotted black;
}

#btDiv {
	width: 100%;
	border-bottom: 1px solid blue;
	float: left;
	padding: 3px 0 3px 0;
}

#btDiv input {
	margin-left: 10px;
}

#textInfoDiv {
	width: 100%;
	float: left;
	margin-left: 5px;
}

#flashcontent {
	width: 100%;
	float: left;
	text-align: center;
	padding: 5px 0 5px 0;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="nodeId" type="hidden" value="<%=nodeId%>" />
	<input id="fileName" type="hidden" value="<%=fileName%>" />
	<input id="ifindex" type="hidden" value="<%=ifindex%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />
	<form id="form">
		<div id="parentContainer">
			<div id="btDiv">
				<table>
					<tr>
						<td><input type="button" value="暂 停" name="suspend" onclick="suspendBtn();" class="l-button" /></td>
						<td><input type="button" value="继 续" name="continue" onclick="continueBtn();" class="l-button" /></td>
						<td><input type="button" value="导 出" name="excel" onclick="excelBtn();" class="l-button" /></td>
						<td><input type="button" value="打 印" name="print" onclick="printBtn()" class="l-button" /></td>
						<td><input type="button" value="退 出" name="exit" onclick="exitBtn()" class="l-button" /></td>
					</tr>
				</table>
			</div>
			<div id="textInfoDiv">
				<table cellpadding="0" cellspacing="0" class="l-table-edit">
					<tr>
						<td class="l-table-label-td">IP地址:</td>
						<td class="l-table-edit-td"><input type="text" id="ip" name="ip" ltype="text" value="<%=ip%>" /></td>
						<td class="l-table-label-td">端口索引:</td>
						<td class="l-table-edit-td"><input type="text" id="ifindex" name="ifindex" ltype="text" value="<%=ifindex%>" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">开始时间:</td>
						<td class="l-table-edit-td"><input type="text" id="startTime" name="startTime" ltype="text" value="" /></td>
						<td class="l-table-label-td">暂停时间:</td>
						<td class="l-table-edit-td"><input type="text" id="suspendTime" name="suspendTime" ltype="text" value="" /></td>
					</tr>
				</table>
			</div>
			<div id="flashcontent"></div>
		</div>
	</form>
</body>
</html>