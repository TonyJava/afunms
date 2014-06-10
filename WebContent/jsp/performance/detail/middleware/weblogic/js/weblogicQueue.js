var basePath = null;
var grid = null;
var key = null;
var weblogicQueueData = null;
var ip = null;
var nodeId = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			weblogicQueueData = f_getWeblogicQueueList();
			key = $("#key").ligerTextBox({});
			// grid
			grid = $("#weblogicQueueGrid").ligerGrid({
						columns : [{
									display : '队列名称',
									name : 'executeQueueRuntimeName',
									align : 'left',
									minWidth : 200
								}, {
									display : '执行线程空闲数',
									name : 'thdPoolRunExeThdIdleCnt',
									minWidth : 50
								}, {
									display : '最长等待请求',
									name : 'exeQueRunPendReqOldTime',
									minWidth : 50
								}, {
									display : '当前线程数',
									name : 'exeQueRunPendReqCurCount',
									minWidth : 50
								}, {
									display : '总线程数',
									name : 'exeQueRunPendReqTotCount',
									minWidth : 50
								}],
						pageSize : 30,
						data : $.extend(true, {}, weblogicQueueData),
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
	grid.options.data = $.extend(true, {}, weblogicQueueData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		if (rowdata.executeQueueRuntimeName.indexOf(key) > -1) {
			return true;
		}
	};
	return clause;
}
// 获取阈值数据方法
function f_getWeblogicQueueList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "weblogicPerformanceAjaxManager.ajax?action=getWeblogicQueueList",
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
	weblogicQueueData = f_getWeblogicQueueList();
	grid.set({
				data : $.extend(true, {}, weblogicQueueData)
			});
	f_search();
}
