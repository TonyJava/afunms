var grid = null;
var basePath = null;

$(function() {
	basePath = $("#basePath").attr("value");
	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "greater", "less", "startwith", "endwith"];
	// grid
	grid = $("#syslogGrid").ligerGrid({
		columns : [{
			name : 'id',
			hide : true,
			width : 0.1
		}, {
			display : '用户',
			name : 'user',
			width : 150
		}, {
			display : '事件',
			name : 'event',
			minWidth : 150
		}, {
			display : '时间',
			name : 'time',
			width : 200
		}, {
			display : 'IP地址',
			name : 'ipaddress',
			width : 200
		}],
		pageSize : 30,
		checkbox : true,
		data : listSystemSyslog(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'user',
		width : '99.8%',
		height : '100%',
		heightDiff :-4,
		onReload : function() {
			grid.set({
						data : listSystemSyslog()
					});
		},
		// 工具栏
		toolbar : {
			items : [{
						text : '高级自定义查询',
						click : sItemclick,
						icon : 'search2'
					}, {
						text : '删除',
						click : itemclick,
						icon : 'delete'
					}]
		},
	});
});

function sItemclick() {
	grid.options.data = $.extend(true, {}, listSystemSyslog());
	grid.showFilter();
}

function listSystemSyslog() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "userAjaxManager.ajax?action=listSystemSyslog",
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
// 刷新列表
function refresh() {
	grid.set({
				data : listSystemSyslog()
			});
}

function itemclick(item) {
	if(item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.id + ";";
					});
			$.ligerDialog.success(f_deleteSyslog(idString));
		}
	}
}

function f_deleteSyslog(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "userAjaxManager.ajax?action=deleteSystemSyslog",
				// 参数
				data : {
					idString : string
				},
				dataType : "text",
				success : function(array) {
					// 成功删除则更新表格行
					refresh();
					grid.deleteSelectedRow();
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}
