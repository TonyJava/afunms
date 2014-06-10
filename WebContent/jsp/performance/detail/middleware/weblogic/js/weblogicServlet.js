var basePath = null;
var grid = null;
var key = null;
var weblogicServletData = null;
var ip = null;
var nodeId = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			weblogicServletData = f_getWeblogicServletList();
			key = $("#key").ligerTextBox({});
			// grid
			grid = $("#weblogicServletGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'RunName',
									align : 'left',
									minWidth : 200
								}, {
									display : '重装载数',
									name : 'RunReloadTotalCnt',
									minWidth : 50,
									width:80
								}, {
									display : '调用次数',
									name : 'RunInvoTotCnt',
									minWidth : 50,
									width:80
								}, {
									display : '最大容量',
									name : 'RunPoolMaxCapacity',
									minWidth : 50,
									width:80
								}, {
									display : '总执行时间',
									name : 'RunExecTimeTotal',
									minWidth : 50,
									width:80
								}, {
									display : '最高执行时间',
									name : 'RunExecTimeHigh',
									minWidth : 50,
									width:80
								}, {
									display : '最低执行时间',
									name : 'RunExecTimeLow',
									minWidth : 50,
									width:80
								}, {
									display : '平均执行时间',
									name : 'RunExecTimeAvg',
									minWidth : 50,
									width:80
								}, {
									display : 'URL',
									name : 'RunURL',
									align : 'left',
									minWidth : 50
								}],
						pageSize : 30,
						data : $.extend(true, {}, weblogicServletData),
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
	grid.options.data = $.extend(true, {}, weblogicServletData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		if (rowdata.RunName.indexOf(key) > -1) {
			return true;
		}
	};
	return clause;
}
// 获取阈值数据方法
function f_getWeblogicServletList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "weblogicPerformanceAjaxManager.ajax?action=getWeblogicServletList",
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
	weblogicServletData = f_getWeblogicServletList();
	grid.set({
				data : $.extend(true, {}, weblogicServletData)
			});
	f_search();
}
