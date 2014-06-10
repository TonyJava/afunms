var basePath = null;
var grid = null;
var key = null;
var weblogicApplicationData = null;
var ip = null;
var nodeId = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			weblogicApplicationData = f_getWeblogicApplicationList();
			key = $("#key").ligerTextBox({});
			// grid
			grid = $("#weblogicApplicationGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'CompRunComptName',
									align : 'left',
									minWidth : 200
								}, {
									display : '状态',
									name : 'CompRunStatus',
									minWidth : 50
								}, {
									display : '当前会话数',
									name : 'CompRunOpenSessCurCount',
									minWidth : 50
								}, {
									display : '最大会话数',
									name : 'CompRunOpenSessHighCount',
									minWidth : 50
								}, {
									display : '总会话数',
									name : 'CompRunSessOpenedTotCount',
									minWidth : 50
								}],
						pageSize : 30,
						data : $.extend(true, {}, weblogicApplicationData),
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
	grid.options.data = $.extend(true, {}, weblogicApplicationData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		if (rowdata.CompRunComptName.indexOf(key) > -1) {
			return true;
		}
	};
	return clause;
}
// 获取阈值数据方法
function f_getWeblogicApplicationList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "weblogicPerformanceAjaxManager.ajax?action=getWeblogicApplicationList",
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
	weblogicApplicationData = f_getWeblogicApplicationList();
	grid.set({
				data : $.extend(true, {}, weblogicApplicationData)
			});
	f_search();
}
