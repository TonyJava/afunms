<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String alarmWayId = request.getParameter("alarmWayId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>告警方式编辑</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerForm.js" type="text/javascript"></script>
<script src="js/alarmWayEdit.js"></script>
<style type="text/css">
html,body {
	margin: 0px;
	padding: 0px;
}

.groupTitle {
	font-size: 12px;
	font-weight: bold;
	margin: 4px;
	padding-left: 45px;
	float: left;
	height: 28px;
	width: 90%;
	line-height: 28px;
	border-bottom: 1px solid #D6D6D6;
	background: url('<%=basePath%>css/icons/communication.gif') no-repeat;
	background-position: 12px 2px;
}

#detailConfigContainer {
	width: 95%;
	float: left;
	margin-left: 10px;
}

#mailContainer,#smsContainer,#soundContainer {
	margin: 10px 0 0 0;
	border-bottom: 1px dotted black;
}

#mailConfigDiv,#smsConfigDiv,#soundConfigDiv {
	display: none;
	margin: 5px 0 0 0;
}

#mailConfigGrid,#smsConfigGrid,#soundConfigGrid {
	margin: 3px 0 0 0;
}

#lineThird {
	float: left;
	width: 100%;
	margin-top: 20px;
}

#actionDiv {
	width: 160px;
	margin-left: auto;
	margin-right: auto;
}
</style>

</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="id" type="hidden" value="<%=alarmWayId%>" />
	<div id="parentContainer">
		<div id="lineOne">
			<form id="baseFrom"></form>
		</div>
		<div id="lineTwo">
			<div class="groupTitle">详细配置</div>
			<div id="detailConfigContainer">

				<div id="mailContainer">
					<div class="op">
						<table>
							<tr>
								<td><input type="checkbox" id="mailCheck" /></td>
								<td>邮件告警</td>
							</tr>
						</table>
					</div>
					<div id="mailConfigDiv">
						<table>
							<tr>
								<td><div class="btAdd" id="mailBtAdd"></div></td>
								<td style="padding-left: 5px;"><div class="btDel" id="mailBtDel"></div></td>
							</tr>
						</table>
						<div id="mailConfigGrid" class="grid"></div>
					</div>
				</div>

				<div id="smsContainer">
					<div class="op">
						<table>
							<tr>
								<td><input type="checkbox" id="smsCheck" /></td>
								<td>短信告警</td>
							</tr>
						</table>
					</div>
					<div id="smsConfigDiv">
						<table>
							<tr>
								<td><div class="btAdd" id="smsBtAdd"></div></td>
								<td style="padding-left: 5px;"><div class="btDel" id="smsBtDel"></div></td>
							</tr>
						</table>
						<div id="smsConfigGrid" class="grid"></div>
					</div>
				</div>

				<div id="soundContainer">
					<div class="op">
						<table>
							<tr>
								<td><input type="checkbox" id="soundCheck" /></td>
								<td>声音告警</td>
							</tr>
						</table>
					</div>
					<div id="soundConfigDiv">
						<table>
							<tr>
								<td><div class="btAdd" id="soundBtAdd"></div></td>
								<td style="padding-left: 5px;"><div class="btDel" id="soundBtDel"></div></td>
							</tr>
						</table>
						<div id="soundConfigGrid" class="grid"></div>
					</div>
				</div>
			</div>
		</div>
		<div id="lineThird">
			<div id="actionDiv">
				<table>
					<tr>
						<td><div id="btSave"></div></td>
						<td style="padding-left: 5px;"><div id="btCancel"></div></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</body>