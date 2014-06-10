<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String id = request.getParameter("id");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据库修改</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerForm.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerComboBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerCheckBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerButton.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTextBox.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/typeAndSubType.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/dbEdit.js" type="text/javascript"></script>
<style type="text/css">
.l-table-edit-td {
	padding: 4px;
}

.l-table-label-td {
	width: 90px;
	text-align: right;
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

.btDiv {
	text-align: center;
	margin: 10px 0 0 0;
	border-top: 2px solid black;
	padding: 5px 0 0 0;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="bidValue" type="hidden" value="notSet" />
	<input id="id" type="hidden" value="<%=id%>" />
	<form name="dbAdd" method="post" id="dbAdd">
		<div>
			<div class="groupTitle">相关信息</div>
			<div>
				<table cellpadding="0" cellspacing="0" class="l-table-edit">
					<tr>
						<td class="l-table-label-td">名称:</td>
						<td class="l-table-edit-td" id="db_value"><input name="alias" type="text" id="alias" ltype="text" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">IP地址:</td>
						<td class="l-table-edit-td"><input type="text" id="ip_address" ltype="text" /></td>
						<td class="l-table-label-td">数据库名:</td>
						<td class="l-table-edit-td"><input name="db_name" type="text" id="db_name" ltype="text" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">用户名:</td>
						<td class="l-table-edit-td"><input name="user" id="user" type="text" ltype="text" /></td>
						<td class="l-table-label-td">密码:</td>
						<td class="l-table-edit-td"><input name="password" id="password" type="password" ltype="password" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">端口:</td>
						<td class="l-table-edit-td"><input name="port" type="text" id="port" value="" ltype="text" /></td>
						<td class="l-table-label-td">数据库应用:</td>
						<td class="l-table-edit-td"><input name="dbuse" type="text" id="dbuse" ltype="text" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">是否监视:</td>
						<td class="l-table-edit-td"><select name="managed" id="managed" ltype="select">
						</select></td>
						<td class="l-table-label-td">采集方式:</td>
						<td class="l-table-edit-td"><select name="collecttype" id="collecttype" ltype="select">
						</select></td>
					</tr>
				</table>
			</div>
		</div>

		<div>
			<div class="groupTitle">业务</div>
			<div>
				<table cellpadding="0" cellspacing="0" class="l-table-edit">
					<tr>
						<td class="l-table-label-td" style="width: 65px">所属业务:</td>
						<td class="l-table-edit-td"><input name="bid" type="text" id="bid" ltype="popup" value="请选择业务" /></td>
					</tr>
				</table>
			</div>
		</div>
	</form>
	<div class="btDiv">
		<input type="submit" value="修改" id="submit" class="l-button" /> <input type="button" value="关闭" id="closeWindow" class="l-button" />
	</div>
</html>