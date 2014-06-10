<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门添加</title>
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
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/deptAdd.js" type="text/javascript"></script>
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
	width: 350px;
	height: 28px;
	line-height: 28px;
	border-bottom: 1px solid #D6D6D6;
	background: url('<%=basePath%>css/icons/communication.gif') no-repeat;
	background-position: 0% 33.33333%;
}

.btDiv {
	text-align: center;
	margin: 10px 0 0 0;
	border-top: 1px dotted black;
	padding: 5px 0 0 0;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<form name="deptAdd" method="post" id="deptAdd">
		<div>
			<div class="groupTitle">部门信息</div>
			<div>
				<table cellpadding="0" cellspacing="0" class="l-table-edit">
					<tr>
						<td class="l-table-label-td">部门:</td>
						<td class="l-table-edit-td"><input name="dept" type="text" id="dept" ltype="text" /></td>
					</tr>
				</table>
			</div>
		</div>
		<div>
			<div class="groupTitle">部门联系人</div>
			<div>
				<table cellpadding="0" cellspacing="0" class="l-table-edit">
					<tr>
						<td class="l-table-label-td">姓名:</td>
						<td class="l-table-edit-td"><input name="man" type="text" id="man" ltype="text" /></td>
					</tr>
					<tr>
						<td class="l-table-label-td">电话号码:</td>
						<td class="l-table-edit-td"><input name="tel" type="text" id="tel" ltype="text" /></td>
					</tr>
				</table>
			</div>
		</div>
	</form>
	<div class="btDiv">
		<input type="submit" value="提交" id="submit" class="l-button" />
	</div>
</html>