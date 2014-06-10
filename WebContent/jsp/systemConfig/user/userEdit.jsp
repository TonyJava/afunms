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
<title>用户添加</title>
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
<script src="js/userEdit.js" type="text/javascript"></script>
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
	<input id="id" type="hidden" value="<%=id%>" />
	<input id="bidValue" type="hidden" value="notSet" />
	<form name="userAdd" method="post" id="userAdd">
		<div>
			<div class="groupTitle">用户信息</div>
			<div>
				<table cellpadding="0" cellspacing="0" class="l-table-edit">
					<tr>
						<td class="l-table-label-td">登录名:</td>
						<td class="l-table-edit-td"><input name="userId" type="text" id="userId" ltype="text" /></td>
						<td class="l-table-label-td">用户名:</td>
						<td class="l-table-edit-td"><input name="name" type="text" id="name" ltype="text" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">密码:</td>
						<td class="l-table-edit-td"><input name="password" type="password" id="password" ltype="text" /></td>
						<td class="l-table-label-td">邮箱:</td>
						<td class="l-table-edit-td"><input name="email" type="text" id="email" ltype="text" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">手机:</td>
						<td class="l-table-edit-td"><input name="mobile" type="text" id="mobile" ltype="text" /></td>
						<td class="l-table-label-td">电话:</td>
						<td class="l-table-edit-td"><input name="phone" type="text" id="phone" ltype="text" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">角色:</td>
						<td class="l-table-edit-td"><select name="role" id="role" ltype="select">
						</select></td>
						<td class="l-table-label-td">部门:</td>
						<td class="l-table-edit-td"><select name="dept" id="dept" ltype="select">
						</select></td>
					</tr>
					<tr>
						<td class="l-table-label-td">职位:</td>
						<td class="l-table-edit-td"><select name="position" id="position" ltype="select">
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
		<input type="submit" value="修改" id="submit" class="l-button" />
	</div>
</html>