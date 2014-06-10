var basePath = null;
var grid = null;
var key = null;
var weblogicJDBCData = null;
var ip = null;
var nodeId = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			weblogicJDBCData = f_getWeblogicJDBCList();
			key = $("#key").ligerTextBox({});
			// grid
			grid = $("#weblogicJDBCGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'jdbcConnectionPoolName',
									align : 'left',
									minWidth : 200
								}, {
									display : '当前连接数',
									name : 'ConPoolRunActConnsCurCount',
									minWidth : 50
								}, {
									display : '驱动器版本',
									name : 'ConPoolRunVerJDBCDriver',
									minWidth : 50
								}, {
									display : '最大容量',
									name : 'ConPoolRunMaxCapacity',
									minWidth : 50
								}, {
									display : '平均连接数',
									name : 'ConPoolRunActConsAvgCount',
									minWidth : 50
								}, {
									display : '最高可活动连接数',
									name : 'ConPoolRunHighestNumAvai',
									minWidth : 50
								}, {
									display : '连接泄漏数',
									name : 'Leaked',
									minWidth : 50
								}, {
									display : '连接等待数',
									name : 'WaitCurrent',
									minWidth : 50
								}, {
									display : '等待最长时间',
									name : 'WaitMaxTime',
									minWidth : 50
								}],
						pageSize : 30,
						data : $.extend(true, {}, weblogicJDBCData),
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
	grid.options.data = $.extend(true, {}, weblogicJDBCData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		if (rowdata.jdbcConnectionPoolName.indexOf(key) > -1) {
			return true;
		}
	};
	return clause;
}
// 获取阈值数据方法
function f_getWeblogicJDBCList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "weblogicPerformanceAjaxManager.ajax?action=getWeblogicJDBCList",
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
	weblogicJDBCData = f_getWeblogicJDBCList();
	grid.set({
				data : $.extend(true, {}, weblogicJDBCData)
			});
	f_search();
}
