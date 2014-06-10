var basePath = null;
var grid = null;
var key = null;
var tomcatApplicationData = null;
var ip = null;
var nodeId = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			tomcatApplicationData = f_getTomcatApplicationList();
			key = $("#key").ligerTextBox({});
			// grid
			grid = $("#tomcatApplicationGrid").ligerGrid({
						columns : [{
									display : '应用名称',
									name : 'applicationName',
									align : 'left',
									minWidth : 200
								}, {
									display : '最大会话数',
									name : 'maxSession',
									minWidth : 50
								}, {
									display : '活动会话数',
									name : 'activeSession',
									minWidth : 50
								}, {
									display : '会话数',
									name : 'session',
									minWidth : 50
								}],
						pageSize : 30,
						data : $.extend(true, {}, tomcatApplicationData),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						width : '99.8%',
						height : '99.5%',
						onReload : function() {
							refresh()
						}
					});

			$("#key").keyup(function() {
						f_search();
					});

		});

// 查询过滤方法
function f_search() {
	grid.options.data = $.extend(true, {}, tomcatApplicationData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		if (rowdata.applicationName.indexOf(key) > -1) {
			return true;
		}
	};
	return clause;
}
// 获取阈值数据方法
function f_getTomcatApplicationList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "tomcatPerformanceAjaxManager.ajax?action=getTomcatApplicationList",
		// 参数
		data : {
			ip : ip,
			nodeId : nodeId
		},
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
	tomcatApplicationData = f_getTomcatApplicationList();
	grid.set({
				data : $.extend(true, {}, tomcatApplicationData)
			});
	f_search();
}
