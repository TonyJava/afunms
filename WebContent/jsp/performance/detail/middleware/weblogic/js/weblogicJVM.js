var basePath = null;
var grid = null;
var key = null;
var weblogicJVMData = null;
var ip = null;
var nodeId = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			weblogicJVMData = f_getWeblogicJVMList();
			key = $("#key").ligerTextBox({});
			// grid
			grid = $("#weblogicJVMGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'jvmRuntimeName',
									align : 'left',
									minWidth : 200
								}, {
									display : '堆大小(MB)',
									name : 'jvmRuntimeHeapSizeCurrent',
									minWidth : 50
								}, {
									display : '当前空闲堆大小(MB)',
									name : 'jvmRuntimeHeapFreeCurrent',
									minWidth : 50
								}],
						pageSize : 30,
						data : $.extend(true, {}, weblogicJVMData),
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
	grid.options.data = $.extend(true, {}, weblogicJVMData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		if (rowdata.jvmRuntimeName.indexOf(key) > -1) {
			return true;
		}
	};
	return clause;
}
// 获取阈值数据方法
function f_getWeblogicJVMList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "weblogicPerformanceAjaxManager.ajax?action=getWeblogicJVMList",
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
	weblogicJVMData = f_getWeblogicJVMList();
	grid.set({
				data : $.extend(true, {}, weblogicJVMData)
			});
	f_search();
}
