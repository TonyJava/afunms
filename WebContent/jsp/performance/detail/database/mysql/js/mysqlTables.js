var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#mysqlTablesGrid").ligerGrid({
						columns : [{
									display : '表名',
									name : 'tablename',
									minWidth : 150,
									width : 650
								}, {
									display : '表行数',
									name : 'tablerows',
									minWidth : 120,
									width : 200
								}, {
									display : '表大小',
									name : 'tablesize',
									minWidth : 120,
									width : 200
								}, {
									display : '创建时间',
									name : 'createtime',
									minWidth : 100,
									width : 210
								}],
						pageSize : 22,
						checkbox : true,
						data : getMysqlTablesDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'tablename',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getMysqlTablesDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getMysqlTablesDetail",
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
