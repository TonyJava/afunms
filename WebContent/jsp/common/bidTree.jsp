<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>业务列表</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet" type="text/css" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTree.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTree.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>

<script type="text/javascript">
	var manager = null;
	var basePath = null;
	$(function() {
		basePath = $("#basePath").attr("value");
		$("#bidTree").ligerTree({
			data : f_getBids(),
			idFieldName : 'id',
		});
		manager = $("#bidTree").ligerGetTreeManager();

		$("#btn").ligerButton({
			width : 60,
			text : '确定',
			click : function() {
				var notes = manager.getChecked();
				var text = "";
				var value = "";
				for (var i = 0; i < notes.length; i++) {
					//选择最终子节点
					if (!manager.hasChildren(notes[i].data)) {
						text += notes[i].data.text + ",";
						value += notes[i].data.id + ",";
					}
				}
				//给父窗口元素赋值
				$("#bid", opener.document).val(text);
				$("#bidValue", opener.document).val(value);

				//关闭当前窗口
				window.close();
			}
		});
	});

	function f_getBids() {
		var rs = null;
		$.ajax({
			type : "POST",
			async : false,
			url : basePath + "nodeHelperAjaxManager.ajax?action=getBids",
			dataType : "json",
			success : function(array) {
				rs = array;
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown);
			}
		});
		return rs;
	}
</script>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div>
		<div style="height: 27px; border-bottom: 2px solid black; padding: 2px 0 0 2px">
			<div id="btn"></div>
		</div>
		<div>
			<ul id="bidTree">
			</ul>
		</div>
	</div>

</body>
</html>