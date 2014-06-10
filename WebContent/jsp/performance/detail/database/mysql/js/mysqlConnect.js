var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#mysqlConnectGrid").ligerGrid({
						columns : [{
									display : '数据库',
									name : 'dbname',
									minWidth : 200,
									width : 200
								}, {
									display : '用户名',
									name : 'username',
									minWidth : 150,
									width : 180
								}, {
									display : '主机',
									name : 'hostname',
									minWidth : 300
								}, {
									display : '命令',
									name : 'comm',
									minWidth : 100,
									width : 200
								}, {
									display : '连接时间',
									name : 'conntime',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getMysqlConnectDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'dbname',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getMysqlConnectDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getMysqlConnectDetail",
				// 参数
				data : {
					ip : ip,
					id : id
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
